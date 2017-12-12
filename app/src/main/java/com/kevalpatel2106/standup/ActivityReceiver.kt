package com.kevalpatel2106.standup

import com.kevalpatel2106.standup.engine.detector.ActivityDetectionReceiver

class ActivityReceiver : ActivityDetectionReceiver() {
    override fun onUserSitting() {
//        TestNotification.buildNotification(context, "User is sitting.", System.currentTimeMillis().toInt())
    }

    override fun onUserMoving() {
//        TestNotification.buildNotification(context, "User is moving.", System.currentTimeMillis().toInt())
    }
}
