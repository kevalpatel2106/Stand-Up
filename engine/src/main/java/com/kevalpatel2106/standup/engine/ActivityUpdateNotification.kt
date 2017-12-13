package com.kevalpatel2106.standup.engine

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.google.android.gms.location.DetectedActivity
import timber.log.Timber

@Deprecated("For debug purpose only.")
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
            message = message.plus(String.format("%s : %d, ", getActivityString(it.type), it.confidence))
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

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    fun getActivityString(detectedActivityType: Int): String {
        return when (detectedActivityType) {
            DetectedActivity.IN_VEHICLE -> "In vehicle"
            DetectedActivity.ON_BICYCLE -> "On bicycle"
            DetectedActivity.ON_FOOT -> "On foot"
            DetectedActivity.RUNNING -> "Running"
            DetectedActivity.STILL -> "Still"
            DetectedActivity.TILTING -> "Tilting"
            DetectedActivity.UNKNOWN -> "Unknown"
            DetectedActivity.WALKING -> "Walking"
            else -> "Other"
        }
    }
}
