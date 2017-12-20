package com.kevalpatel2106.standup.reminder

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat

@Suppress("DEPRECATION")
internal object ReminderNotification {
    private val NOTIFICATION_ID = 3455

    @SuppressLint("VisibleForTests")
    internal fun notify(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(context))
    }

    private fun buildNotification(context: Context): Notification {
        val builder = NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Stand up now")
                .setContentText("Stand up now")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker("Stand up now")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())

        return builder.build()
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
