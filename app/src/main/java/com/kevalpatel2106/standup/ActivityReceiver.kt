package com.kevalpatel2106.standup

import com.kevalpatel2106.activityengine.ActivityDetectionReceiver
import com.kevalpatel2106.activityengine.TestNotification

class ActivityReceiver : ActivityDetectionReceiver() {
    override fun onUserSitting() {
//        TestNotification.notify(context, "User is sitting.", System.currentTimeMillis().toInt())
    }

    override fun onUserMoving() {
//        TestNotification.notify(context, "User is moving.", System.currentTimeMillis().toInt())
    }
}
