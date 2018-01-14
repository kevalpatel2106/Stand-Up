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

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.misc.UserSettingsManager

/**
 * Created by Kevalpatel2106 on 04-Dec-17.
 * Ids of the notification channels available for the notifications in Android O+.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object NotificationChannelType {

    /**
     * Notification channel for the syncing data tasks. These notifications are basically for the
     * foreground services.
     */
    const val SYNC_NOTIFICATION_CHANNEL = "sync_notification_channel"

    /**
     * Notifications for account activities.
     */
    const val ACCOUNT_NOTIFICATION_CHANNEL = "account_notification_channel"

    /**
     * Notifications for reminder activities.
     */
    const val REMINDER_NOTIFICATION_CHANNEL = "reminder_notification_channel"
}

//============== Extension functions for the notification channel management. ==================//

@TargetApi(Build.VERSION_CODES.O)
fun NotificationManager.addSyncNotificationChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val chan1 = NotificationChannel(NotificationChannelType.SYNC_NOTIFICATION_CHANNEL,
                context.getString(R.string.notification_channel_title_syncing), NotificationManager.IMPORTANCE_LOW)
        chan1.lightColor = Color.GREEN
        chan1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        createNotificationChannel(chan1)
    }
}

@TargetApi(Build.VERSION_CODES.O)
fun NotificationManager.addAccountNotificationChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val chan2 = NotificationChannel(NotificationChannelType.ACCOUNT_NOTIFICATION_CHANNEL,
                context.getString(R.string.notification_channel_title_accounts), NotificationManager.IMPORTANCE_LOW)
        chan2.lightColor = Color.BLUE
        chan2.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        createNotificationChannel(chan2)
    }
}

/**
 * Create or update the reminder notification channel in the application settings. This channel
 * settings will be in sync with the notification settings user selected in the application settings
 * screen. Method will read [userSettingsManager] settings such as vibration, led color and ringtone.
 * We cannot relay on [NotificationManager] to set the prams while building the notification.
 * [Here is the explanation.](https://stackoverflow.com/q/45081815)
 */
@TargetApi(Build.VERSION_CODES.O)
fun NotificationManager.addReminderNotificationChannel(context: Context,
                                                       userSettingsManager: UserSettingsManager) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val chan2 = NotificationChannel(NotificationChannelType.REMINDER_NOTIFICATION_CHANNEL,
                context.getString(R.string.notification_channel_title_reminders),
                NotificationManager.IMPORTANCE_HIGH)
        chan2.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        //Set the vibration to vibration provided in settings
        chan2.enableVibration(userSettingsManager.shouldVibrate)

        //Set the sound to ring tone uri to empty. We don't want android system to control the sound
        //We will play the sound while displaying the notification using the media player API.
        chan2.setSound(null, null)

        //Set the light color
        chan2.enableLights(true)
        chan2.lightColor = userSettingsManager.ledColor

        createNotificationChannel(chan2)
    }

}