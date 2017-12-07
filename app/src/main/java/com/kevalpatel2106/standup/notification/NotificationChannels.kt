package com.kevalpatel2106.standup.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.kevalpatel2106.standup.R

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
}

//============== Extension functions for the notification channel management. ==================//

@TargetApi(Build.VERSION_CODES.O)
fun NotificationManager.addSyncNotificationChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val chan1 = NotificationChannel(NotificationChannelType.SYNC_NOTIFICATION_CHANNEL,
                context.getString(R.string.notification_channel_title_syncing), NotificationManager.IMPORTANCE_DEFAULT)
        chan1.lightColor = Color.GREEN
        chan1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        createNotificationChannel(chan1)
    }
}

@TargetApi(Build.VERSION_CODES.O)
fun NotificationManager.addAccountNotificationChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val chan2 = NotificationChannel(NotificationChannelType.ACCOUNT_NOTIFICATION_CHANNEL,
                context.getString(R.string.notification_channel_title_accounts), NotificationManager.IMPORTANCE_HIGH)
        chan2.lightColor = Color.BLUE
        chan2.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        createNotificationChannel(chan2)
    }
}