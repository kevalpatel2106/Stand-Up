package com.kevalpatel2106.standup.userActivity

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