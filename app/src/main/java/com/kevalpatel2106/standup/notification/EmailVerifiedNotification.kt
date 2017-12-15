package com.kevalpatel2106.standup.notification

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat
import com.kevalpatel2106.standup.R

@Suppress("DEPRECATION")
internal object EmailVerifiedNotification {
    private val NOTIFICATION_ID = 2345

    @SuppressLint("VisibleForTests")
    internal fun notify(context: Context, message: String?) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.addAccountNotificationChannel(context.applicationContext)
        nm.notify(NOTIFICATION_ID, buildNotification(context, message))
    }

    @VisibleForTesting
    internal fun buildNotification(context: Context, message: String?): Notification {
        val builder = NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_notififcation_launcher)
                .setContentTitle(context.getString(R.string.application_name))
                .setContentText(message ?: context.getString(R.string.application_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification_verified))
                .setTicker(message)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message)
                        .setBigContentTitle(context.getString(R.string.application_name)))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NotificationChannelType.ACCOUNT_NOTIFICATION_CHANNEL)
        }

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
