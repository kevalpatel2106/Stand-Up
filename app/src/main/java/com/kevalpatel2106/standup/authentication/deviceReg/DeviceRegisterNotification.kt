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

package com.kevalpatel2106.standup.authentication.deviceReg

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.fcm.NotificationChannelType
import com.kevalpatel2106.standup.fcm.addSyncNotificationChannel

@Suppress("DEPRECATION")
internal object DeviceRegisterNotification {

    const val FOREGROUND_NOTIFICATION_ID = 7823

    internal fun buildNotification(context: Context): Notification {
        val builder = Notification.Builder(context)
                .setContentTitle(context.getString(R.string.application_name))
                .setSmallIcon(R.drawable.ic_notififcation_launcher)
                .setContentText(context.getString(R.string.register_device_service_notification_message))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setAutoCancel(false)
                .setTicker(context.getString(R.string.register_device_service_notification_message))
                .setPriority(Notification.PRIORITY_LOW)
                .setStyle(Notification.BigTextStyle()
                        .setBigContentTitle(context.getString(R.string.application_name))
                        .bigText(context.getString(R.string.register_device_service_notification_message)))


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NotificationChannelType.SYNC_NOTIFICATION_CHANNEL)
        }

        //Add notification channel
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.addSyncNotificationChannel(context)

        return builder.build()
    }
}
