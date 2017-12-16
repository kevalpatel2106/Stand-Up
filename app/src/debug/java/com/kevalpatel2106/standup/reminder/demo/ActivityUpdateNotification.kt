package com.kevalpatel2106.standup.reminder.demo

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.google.android.gms.location.DetectedActivity
import timber.log.Timber

@Suppress("DEPRECATION")
internal object ActivityUpdateNotification {
    private val NOTIFICATION_ID = 7852

    internal fun notify(context: Context, detectedActivity: ArrayList<DetectedActivity>) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(context, detectedActivity))
    }

    private fun buildNotification(context: Context, detectedActivity: ArrayList<DetectedActivity>): Notification {

        var message = ""
        detectedActivity.forEach {
            message = message.plus(String.format("%s : %d, ",
                    DemoRecognitionActivity.getActivityString(it.type), it.confidence))
        }
        Timber.d(message)

        val builder = NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Activity Update")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker(message)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setWhen(System.currentTimeMillis())
                .setStyle(NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle("Activity Update"))

        return builder.build()
    }
}
