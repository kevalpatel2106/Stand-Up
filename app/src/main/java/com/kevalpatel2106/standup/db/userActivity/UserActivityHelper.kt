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

package com.kevalpatel2106.standup.db.userActivity

import com.kevalpatel2106.base.annotations.Helper
import timber.log.Timber

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Helper(UserActivity::class)
internal object UserActivityHelper {

    @JvmStatic
    fun getActivityType(type: String): UserActivityType {

        return when (type) {
            UserActivityType.SITTING.name.toLowerCase() -> UserActivityType.SITTING
            UserActivityType.MOVING.name.toLowerCase() -> UserActivityType.MOVING
            UserActivityType.NOT_TRACKED.name.toLowerCase() -> UserActivityType.NOT_TRACKED
            else -> {/*This should never happen*/
                Timber.e("Invalid user activity type ->".plus(type))
                UserActivityType.NOT_TRACKED
            }
        }
    }

    @JvmStatic
    fun createLocalUserActivity(type: UserActivityType): UserActivity {
        return UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = 0, /*We don't know when the event will end*/
                type = type.name.toLowerCase(),
                isSynced = false)
    }
}