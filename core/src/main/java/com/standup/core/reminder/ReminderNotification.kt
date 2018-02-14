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

package com.standup.core.reminder

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat
import com.kevalpatel2106.common.ReminderMessageProvider
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.notifications.NotificationChannelType
import com.kevalpatel2106.common.notifications.addReminderNotificationChannel
import com.kevalpatel2106.utils.getColorCompat
import com.kevalpatel2106.utils.vibrate
import com.standup.core.R
import com.standup.core.di.DaggerCoreComponent
import javax.inject.Inject


@Suppress("DEPRECATION")
internal class ReminderNotification {

    @Inject
    internal lateinit var userSettingsManager: UserSettingsManager

    @Inject
    internal lateinit var reminderProvider: ReminderMessageProvider

    @VisibleForTesting
    constructor(userSettingsManager: UserSettingsManager,
                reminderMessageProvider: ReminderMessageProvider) {
        this.userSettingsManager = userSettingsManager
        this.reminderProvider = reminderMessageProvider
    }

    constructor() {
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this)
    }

    private val NOTIFICATION_ID = 3455

    @SuppressLint("VisibleForTests", "NewApi")
    fun notify(context: Context) {

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.addReminderNotificationChannel(context, userSettingsManager)

        //Fire notification
        nm.notify(NOTIFICATION_ID, buildNotification(context, userSettingsManager.ledColor)
                .build())

        if (NotificationSchedulerHelper.shouldVibrate(context, userSettingsManager)) {
            context.vibrate(200)
        }

        if (NotificationSchedulerHelper.shouldPlaySound(context, userSettingsManager)) {
            NotificationSchedulerHelper.playSound(context, userSettingsManager.getReminderToneUri)
        }
    }

    @VisibleForTesting
    internal fun buildNotification(context: Context,
                                   @ColorInt lightColor: Int): NotificationCompat.Builder {

        return NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_reminder_notification_small)
                .setContentTitle(context.getString(R.string.reminder_notification_title))
                .setContentText(reminderProvider.getReminderMessage())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(reminderProvider.getReminderMessage())
                .setAutoCancel(true)
                .setColor(context.getColorCompat(R.color.reminder_notification_color))
                .setWhen(System.currentTimeMillis())
                .setLights(lightColor, 100, 1000)
                .setChannelId(NotificationChannelType.REMINDER_NOTIFICATION_CHANNEL)
                .setStyle(NotificationCompat.BigTextStyle()
                        .setBigContentTitle(context.getString(R.string.reminder_notification_title))
                        .bigText(reminderProvider.getReminderMessage()))
    }


    /**
     * Cancels any notifications of this type previously shown using
     * [.buildNotification].
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    internal fun cancel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)
    }
}
