/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.kevalpatel2106.common.userActivity.repo

import android.support.annotation.WorkerThread
import com.kevalpatel2106.common.di.AppModule
import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityDao
import com.kevalpatel2106.common.userActivity.UserActivityHelper
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.kevalpatel2106.network.repository.RepoBuilder
import com.kevalpatel2106.network.repository.refresher.RetrofitNetworkRefresher
import com.kevalpatel2106.utils.TimeUtils
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Named

/**
 * Created by Kevalpatel2106 on 13-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class UserActivityRepoImpl(private val userActivityDao: UserActivityDao,
                           @Named(AppModule.WITH_TOKEN) private val retrofit: Retrofit) : UserActivityRepo {


    /**
     * This method will send the data of all the pending events to the server. This is going to run
     * on asynchronous background thread. It will return [Completable] to notify whenever sync is
     * completed or failed. onSuccess in the single will emit the number of activities synced.
     *
     * Implementation:
     */
    @WorkerThread
    override fun sendPendingActivitiesToServer(): Single<Int> {
        return Single.create {
            //Get all the pending to sync activities.
            val pendingActivities = userActivityDao.getPendingActivity(false)

            if (pendingActivities.isEmpty()) {
                //No item to sync
                it.onSuccess(0)
                return@create
            }

            var numOfSync = 0       //Holds the count of number of items synced.
            pendingActivities
                    .filter { it.userActivityType == UserActivityType.SITTING || it.userActivityType == UserActivityType.MOVING }
                    .forEach {
                        val activityToSync = it

                        //Execute the network call.
                        val call = retrofit.create(CoreApiService::class.java)
                                .saveActivity(SaveActivityRequest(it))

                        RepoBuilder<SaveActivityResponse>()
                                .addRefresher(RetrofitNetworkRefresher(call))
                                .build()
                                .fetch()
                                .map { it.data!! }
                                .subscribe({
                                    activityToSync.remoteId = it.id                 //Add the remote id.
                                    activityToSync.isSynced = true                  //Mark it as synced.
                                    userActivityDao.update(activityToSync)          //Save it to the database

                                    numOfSync++
                                }, {
                                    //API call failed.
                                    Timber.i("Save activity API call failed. Message: ${it.message}")
                                })
                    }

            it.onSuccess(numOfSync)
        }
    }

    @WorkerThread
    override fun getActivitiesFromServer(): Single<Int> {

        return Single.create {
            val emitter = it

            //Get all the pending to sync activities.
            var oldestTimeStamp = TimeUtils.convertToNano(userActivityDao.getOldestTimestamp())
            if (oldestTimeStamp == 0L) oldestTimeStamp = TimeUtils.convertToNano(System.currentTimeMillis())

            //Execute the network call.
            val call = retrofit.create(CoreApiService::class.java)
                    .getActivities(GetActivityRequest(oldestTimeStamp))

            RepoBuilder<GetActivityResponse>()
                    .addRefresher(RetrofitNetworkRefresher(call))
                    .build()
                    .fetch()
                    .map { it.data!!.activities }
                    .subscribe({

                        //Save every thing into the database.
                        it.filter { it.remoteId != 0L }
                                .map {
                                    return@map it.getUserActivity()
                                }
                                .forEach {

                                    with(userActivityDao.getActivityForRemoteId(it.remoteId)) {

                                        @Suppress("IMPLICIT_CAST_TO_ANY")
                                        if (this == null) {
                                            userActivityDao.insert(it)
                                        } else {
                                            it.localId = this.localId
                                            userActivityDao.update(it)
                                        }
                                    }
                                }

                        emitter.onSuccess(it.size)
                    }, {
                        //API call failed.
                        Timber.i("Get activity API call failed. Message: ${it.message}")
                        emitter.onError(it)
                    })
        }
    }

    override fun insertNewUserActivity(newActivity: UserActivity): Single<Long> {
        return insertNewUserActivity(newActivity, false)
    }

    /**
     * This will store [newActivity] into the database and terminate the previous event with the
     * [newActivity] start time. If the previous [UserActivity] is already terminated it will just
     * inert [newActivity].
     *
     * Implementation:
     * - If the end time of [newActivity] is empty (i.e.0) this method will set end time to start
     * time + 10 second. This assumption is made to make future calculations easy.
     * - First check if there is any [UserActivity] into the database? If there are no [UserActivity],
     * insert the [newActivity].
     * - If there is previous [UserActivity] into the database, check if the [UserActivity.userActivityType]
     * for the previous [UserActivity] and [newActivity] is same?
     * - If the type is same, just update the end time of the previous activity with the end time
     * of [newActivity].
     * - If the [UserActivity.userActivityType] of previous activity and [newActivity] are different,
     * enter the [newActivity] into the database.
     * - To make correction into the end time of the previous activity (previous activity will have the
     * end time set in the last run of the
     * [com.standup.core.activityMonitor.ActivityMonitorJob].) we will check if the
     * difference between start time of new activity and end time of the previous activity are less
     * than 2 times of the [CoreConfig.MONITOR_SERVICE_PERIOD]? If the difference is small, update
     * the last [UserActivity] with start time of [newActivity]. If the difference is more, than
     * we are assuming that the [com.standup.core.activityMonitor.ActivityMonitorJob]
     * stopped and the user's activity was not tracked in those duration.
     *
     * @return Id of the newly inserted or updated record.
     * @throws IllegalArgumentException if the start time of [newActivity] is 0.
     */
    override fun insertNewUserActivity(newActivity: UserActivity,
                                       doNotMergeWithPrevious: Boolean): Single<Long> { //Validate new activity
        if (newActivity.eventStartTimeMills == 0L)
            throw IllegalArgumentException("Start time invalid. Start time: ${newActivity.eventStartTimeMills}")

        //Make correction into the end date.
        if (newActivity.eventEndTimeMills <= 0L)
            newActivity.eventEndTimeMills = newActivity.eventStartTimeMills + UserActivityHelper.endTimeCorrectionValue

        return Single.create({
            val lastActivity = userActivityDao.getLatestActivity()

            when {

            //There is no item into the database.
                lastActivity == null || doNotMergeWithPrevious -> {
                    //Add the first event to the database
                    val id = userActivityDao.insert(newActivity)
                    it.onSuccess(id)
                }

            //New activity and previous activities are of different types.
                lastActivity.type != newActivity.type -> {
                    //Check if the difference between start time of new activity and end time of the
                    //previous activity are less than 2 times of the monitoring duration?
                    if (newActivity.eventStartTimeMills - lastActivity.eventEndTimeMills <
                            UserActivityRepo.DURATION.times(2)) {

                        //Update the end time.
                        lastActivity.eventEndTimeMills = newActivity.eventStartTimeMills
                        lastActivity.isSynced = false
                        userActivityDao.update(lastActivity)
                    }

                    //Add the event to the database
                    val id = userActivityDao.insert(newActivity)
                    it.onSuccess(id)
                }

            //Both new and previous activities are of same type.
                else -> {
                    //Update the end time into the database.
                    lastActivity.eventEndTimeMills = newActivity.eventEndTimeMills
                    lastActivity.isSynced = false
                    userActivityDao.update(lastActivity)

                    it.onSuccess(lastActivity.localId)
                }
            }
        })
    }


}
