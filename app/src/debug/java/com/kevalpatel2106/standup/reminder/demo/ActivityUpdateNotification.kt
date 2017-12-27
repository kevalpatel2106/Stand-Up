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

package com.kevalpatel2106.standup.reminder.demo

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.google.android.gms.location.DetectedActivity
import timber.log.Timber

@Suppress("DEPRECATION")
internal object ActivityUpdateNotification {
    private val NOTIFICATION_ID = 7852

    internal fun notify(context: Context, detectedActivity: ArrayList<DetectedActivity>) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(context, detectedActivity))
    }

    private fun buildNotification(context: Context, detectedActivity: ArrayList<DetectedActivity>): Notification {

        var message = ""
        detectedActivity.forEach {
            message = message.plus(String.format("%s : %d, ",
                    DemoRecognitionActivity.getActivityString(it.type), it.confidence))
        }
        Timber.d(message)

        val builder = NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Activity Update")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker(message)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setWhen(System.currentTimeMillis())
                .setStyle(NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle("Activity Update"))

        return builder.build()
    }
}
