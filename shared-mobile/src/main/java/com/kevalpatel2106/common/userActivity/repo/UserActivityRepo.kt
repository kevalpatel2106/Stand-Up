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

import com.kevalpatel2106.common.userActivity.UserActivity
import io.reactivex.Single

/**
 * Created by Kevalpatel2106 on 13-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface UserActivityRepo {

    companion object {
        const val DURATION = 90_000L
    }

    /**
     * This will store [newActivity] into the database and terminate the previous event with the
     * [newActivity] start time. If the previous [UserActivity] is already terminated it will just
     * inert [newActivity].
     *
     * @see UserActivityRepoImpl.insertNewUserActivity for how it's implemented.
     */
    fun insertNewUserActivity(newActivity: UserActivity, doNotMergeWithPrevious: Boolean): Single<Long>

    /**
     * This will store [newActivity] into the database and terminate the previous event with the
     * [newActivity] start time. If the previous [UserActivity] is already terminated it will just
     * inert [newActivity].
     *
     * @see UserActivityRepoImpl.insertNewUserActivity for how it's implemented.
     */
    fun insertNewUserActivity(newActivity: UserActivity): Single<Long>

    /**
     * This method will send the data of all the pending events to the server. This is going to run
     * on asynchronous background thread. It will return [Single] to notify whenever sync is
     * completed or failed. onSuccess in the single will emit the number of activities synced.
     */
    fun sendPendingActivitiesToServer(): Single<Int>

    /**
     * onSuccess in the single will emit the number of activities received from the server.
     */
    fun getActivitiesFromServer(): Single<Int>
}
