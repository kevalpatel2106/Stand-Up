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

import com.kevalpatel2106.common.db.DailyActivitySummary
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.utils.annotations.Repository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Keval on 15/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
internal interface CoreRepo {

    /**
     * This will store [newActivity] into the database and terminate the previous event with the
     * [newActivity] start time. If the previous [UserActivity] is already terminated it will just
     * inert [newActivity].
     *
     * @see CoreRepoImpl.insertNewUserActivity for how it's implemented.
     */
    fun insertNewUserActivity(newActivity: UserActivity, doNotMergeWithPrevious: Boolean): Single<Long>

    /**
     * This will store [newActivity] into the database and terminate the previous event with the
     * [newActivity] start time. If the previous [UserActivity] is already terminated it will just
     * inert [newActivity].
     *
     * @see CoreRepoImpl.insertNewUserActivity for how it's implemented.
     */
    fun insertNewUserActivity(newActivity: UserActivity): Single<Long>

    /**
     * This method will send the data of all the pending events to the server. This is going to run
     * on asynchronous background thread. It will return [Completable] to notify whenever sync is
     * completed or failed.
     */
    fun sendPendingActivitiesToServer(): Completable

    /**
     * Load user activity summary for the previous day.
     *
     * @see DailyActivitySummary
     */
    fun loadYesterdaySummary(): Flowable<DailyActivitySummary>

    fun getActivitiesFromServer(): Completable
}
