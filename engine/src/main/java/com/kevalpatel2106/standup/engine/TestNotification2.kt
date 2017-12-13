package com.kevalpatel2106.standup.engine

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat

@Deprecated("Don't use")
@Suppress("DEPRECATION")
internal object TestNotification2 {
    private val NOTIFICATION_ID = 5467

    @SuppressLint("VisibleForTests")
    internal fun notify(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(context))
    }

    @VisibleForTesting
    internal fun buildNotification(context: Context): Notification {
        val builder = NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_temp)
                .setContentTitle("User is moving")
                .setContentText("User is moving")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker("User is moving")
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
