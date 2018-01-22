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

package com.kevalpatel2106.standup.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat
import com.kevalpatel2106.common.notifications.NotificationChannelType
import com.kevalpatel2106.common.notifications.addOtherNotificationChannel
import com.kevalpatel2106.standup.R


@Suppress("DEPRECATION")
internal object PromotionalNotification {

    private val NOTIFICATION_ID = 3476

    @SuppressLint("VisibleForTests")
    internal fun notify(context: Context, title: String, message: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.addOtherNotificationChannel(context.applicationContext)
        nm.notify(NOTIFICATION_ID, buildNotification(context, title, message).build())
    }

    @VisibleForTesting
    internal fun buildNotification(context: Context, title: String, message: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_notififcation_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notififcation_launcher))
                .setTicker(message)
                .setChannelId(NotificationChannelType.OTHER_NOTIFICATION_CHANNEL)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setStyle(NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title))
    }

    internal fun cancel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)
    }
}
