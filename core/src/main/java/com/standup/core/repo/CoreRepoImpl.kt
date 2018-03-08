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

package com.standup.core.repo

import com.kevalpatel2106.common.application.di.AppModule
import com.kevalpatel2106.common.db.DailyActivitySummary
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityDao
import com.kevalpatel2106.common.db.userActivity.UserActivityHelper
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.common.repository.RepoBuilder
import com.kevalpatel2106.network.executor.refresher.RetrofitNetworkRefresher
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.annotations.Repository
import com.standup.core.CoreConfig
import io.reactivex.*
import retrofit2.Retrofit
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList

/**
 * Created by Keval on 14/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
internal class CoreRepoImpl @Inject constructor(private val userActivityDao: UserActivityDao,
                                                @Named(AppModule.WITH_TOKEN) private val retrofit: Retrofit)
    : CoreRepo {

    /**
     * This method will send the data of all the pending events to the server. This is going to run
     * on asynchronous background thread. It will return [Completable] to notify whenever sync is
     * completed or failed.
     *
     * Implementation:
     */
    override fun sendPendingActivitiesToServer(): Completable {
        return Completable.create {
            //Get all the pending to sync activities.
            val pendingActivities = userActivityDao.getPendingActivity(false)

            if (pendingActivities.isEmpty()) {
                //No item to sync
                it.onComplete()
                return@create
            }

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
                                }, {
                                    //API call failed.
                                    Timber.i("Save activity API call failed. Message: ${it.message}")
                                })
                    }

            it.onComplete()
        }
    }

    override fun getActivitiesToServer(): Completable {
        return Completable.create {
            //Get all the pending to sync activities.
            val oldestTimeStamp = userActivityDao.getOldestTimestamp()

            //Execute the network call.
            val call = retrofit.create(CoreApiService::class.java)
                    .getActivities(GetActivityRequest(oldestTimeStamp))

            RepoBuilder<GetActivityResponse>()
                    .addRefresher(RetrofitNetworkRefresher(call))
                    .build()
                    .fetch()
                    .doOnTerminate { it.onComplete() }
                    .map { it.data!!.activities }
                    .subscribe({

                        //Save every thing into the database.
                        it.filter { it.remoteId != 0L }
                                .map {
                                    it.isSynced = true
                                    return@map it
                                }
                                .forEach { userActivityDao.update(it) }
                    }, {
                        //API call failed.
                        Timber.i("Get activity API call failed. Message: ${it.message}")
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
                            CoreConfig.MONITOR_SERVICE_PERIOD.times(2)) {

                        //Update the end time.
                        lastActivity.eventEndTimeMills = newActivity.eventStartTimeMills
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
                    userActivityDao.update(lastActivity)

                    it.onSuccess(lastActivity.localId)
                }
            }
        })
    }


    /**
     * Load user activity summary for the previous day.
     *
     * @see DailyActivitySummary
     */
    override fun loadYesterdaySummary(): Flowable<DailyActivitySummary> {
        //Get the calender for 12 AM of that day
        var dayCal = Calendar.getInstance()
        dayCal.add(Calendar.DAY_OF_MONTH, -2)       //Previous day
        dayCal = TimeUtils.getMidnightCal(dayCal.timeInMillis)

        val startTimeMills = dayCal.timeInMillis

        dayCal.set(Calendar.HOUR_OF_DAY, 24)
        val endTimeMills = dayCal.timeInMillis

        return Flowable.create(FlowableOnSubscribe<List<UserActivity>> {
            val item = userActivityDao.getActivityBetweenDuration(startTimeMills, endTimeMills)

            it.onNext(item)
            it.onComplete()
        }, BackpressureStrategy.DROP)
                .filter { t -> t.isNotEmpty() }
                .map { t -> ArrayList(t) }
                .map { arrayList ->
                    //Generate the summary
                    DailyActivitySummary.fromDayActivityList(arrayList)
                }
    }
}
