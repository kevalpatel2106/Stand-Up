package com.kevalpatel2106.activityengine

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat

/**
 * Helper class for showing and canceling test
 * notifications.
 *
 *
 * This class makes heavy use of the [NotificationCompat.Builder] helper
 * class to create notifications in a backward-compatible way.
 */
object TestNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private val NOTIFICATION_TAG = "Test"

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     *
     *
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     *
     *
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of test notifications. Make
     * sure to follow the
     * [
 * Notification design guidelines](https://developer.android.com/design/patterns/notifications.html) when doing so.
     *
     * @see .cancel
     */
    fun notify(context: Context,
               exampleString: String, number: Int) {

        val builder = NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setContentTitle(exampleString)
                .setContentText(exampleString)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Set ticker text (preview) information for this notification.
                .setTicker(exampleString)

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(number)
                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true)

        notify(context, builder.build())
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private fun notify(context: Context, notification: Notification) {
        val nm = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification)
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification)
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * [.notify].
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    fun cancel(context: Context) {
        val nm = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0)
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode())
        }
    }
}
