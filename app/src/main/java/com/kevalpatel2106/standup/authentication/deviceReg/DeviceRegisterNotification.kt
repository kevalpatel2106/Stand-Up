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

package com.kevalpatel2106.standup.authentication.deviceReg

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat
import com.kevalpatel2106.common.notifications.NotificationChannelType
import com.kevalpatel2106.common.notifications.addSyncNotificationChannel
import com.kevalpatel2106.standup.R

@Suppress("DEPRECATION")
internal object DeviceRegisterNotification {

    const val FOREGROUND_NOTIFICATION_ID = 7823

    @SuppressLint("VisibleForTests")
    internal fun getNotification(context: Context): Notification {

        //Add notification channel
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.addSyncNotificationChannel(context)

        return buildNotification(context).build()
    }

    @VisibleForTesting
    internal fun buildNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.application_name))
                .setSmallIcon(R.drawable.ic_notififcation_launcher)
                .setContentText(context.getString(R.string.register_device_service_notification_message))
                .setAutoCancel(false)
                .setTicker(context.getString(R.string.register_device_service_notification_message))
                .setPriority(Notification.PRIORITY_LOW)
                .setChannelId(NotificationChannelType.SYNC_NOTIFICATION_CHANNEL)
                .setStyle(NotificationCompat.BigTextStyle()
                        .setBigContentTitle(context.getString(R.string.application_name))
                        .bigText(context.getString(R.string.register_device_service_notification_message)))
    }
}
