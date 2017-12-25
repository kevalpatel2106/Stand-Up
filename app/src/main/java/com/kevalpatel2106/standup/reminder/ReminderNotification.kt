package com.kevalpatel2106.standup.reminder

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.fcm.NotificationChannelType

@Suppress("DEPRECATION")
internal object ReminderNotification {
    private val NOTIFICATION_ID = 3455

    @SuppressLint("VisibleForTests")
    internal fun notify(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(context).build())
    }

    @VisibleForTesting
    internal fun buildNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle(context.getString(R.string.reminder_notification_title))
                .setContentText(context.getString(R.string.reminder_notification_message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.getString(R.string.reminder_notification_message))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setWhen(System.currentTimeMillis())
                .setChannelId(NotificationChannelType.REMINDER_NOTIFICATION_CHANNEL)
                .setStyle(NotificationCompat.BigTextStyle()
                        .setBigContentTitle(context.getString(R.string.reminder_notification_title))
                        .bigText(context.getString(R.string.reminder_notification_message)))
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
