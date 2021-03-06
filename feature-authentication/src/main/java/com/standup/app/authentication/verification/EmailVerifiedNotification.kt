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

package com.standup.app.authentication.verification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat
import com.kevalpatel2106.common.notifications.NotificationChannelType
import com.kevalpatel2106.common.notifications.addAccountNotificationChannel
import com.standup.app.authentication.R

@Suppress("DEPRECATION")
internal object EmailVerifiedNotification {
    private val NOTIFICATION_ID = 2345

    @SuppressLint("VisibleForTests")
    internal fun notify(context: Context, message: String?) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.addAccountNotificationChannel(context.applicationContext)
        nm.notify(NOTIFICATION_ID, buildNotification(context, message).build())
    }

    @VisibleForTesting
    internal fun buildNotification(context: Context,
                                   message: String?,
                                   @VisibleForTesting largeIcon: Bitmap? = null): NotificationCompat.Builder {

        return NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(com.kevalpatel2106.common.R.drawable.ic_notification_launcher)
                .setContentTitle(context.getString(R.string.application_name))
                .setContentText(message ?: context.getString(R.string.application_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(largeIcon ?: BitmapFactory
                        .decodeResource(context.resources, R.drawable.ic_notification_verified))
                .setTicker(message)
                .setChannelId(NotificationChannelType.ACCOUNT_NOTIFICATION_CHANNEL)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message)
                        .setBigContentTitle(context.getString(R.string.application_name)))
    }
}
