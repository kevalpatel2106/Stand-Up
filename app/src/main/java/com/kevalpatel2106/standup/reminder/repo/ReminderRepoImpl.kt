/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.reminder.repo

import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.standup.BuildConfig
import com.kevalpatel2106.standup.db.StandUpDb
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityDao
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

/**
 * Created by Keval on 14/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class ReminderRepoImpl @VisibleForTesting constructor(private val userActivityDao: UserActivityDao,
                                                               private val baseUrl: String) : ReminderRepo {

    constructor() : this(StandUpDb.getDb().userActivityDao(), BuildConfig.BASE_URL)

    override fun sendPendingActivitiesToServer(): Completable {
        return Completable.create {
            Timber.d("Syncing pending events...")
            it.onComplete()
        }
    }

    /**
     * This will store [newActivity] into the database and terminate the previous event with the
     * [newActivity] start time. If the previous [UserActivity] is already terminated it will just
     * inert [newActivity].
     */
    override fun insertNewAndTerminatePreviousActivity(newActivity: UserActivity): Single<Long> {
        return Single.create({
            val lastActivity = userActivityDao.getLatestActivity()

            when {
                lastActivity == null -> {
                    //Add the first event to the database
                    val id = userActivityDao.insert(newActivity)
                    it.onSuccess(id)
                }
                lastActivity.type != newActivity.type -> {
                    //Check if the user activity is changed or not?

                    //This is new user activity.
                    //Update the end time of the last user event.
                    lastActivity.eventEndTimeMills = newActivity.eventStartTimeMills
                    userActivityDao.update(lastActivity)

                    //Add the event to the database
                    val id = userActivityDao.insert(newActivity)
                    it.onSuccess(id)
                }
                else -> {
                    //Activity type did not changed/
                    //Do noting
                    it.onSuccess(0)
                }
            }
        })
    }
}