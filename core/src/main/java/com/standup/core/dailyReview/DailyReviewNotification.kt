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

package com.standup.core.dailyReview

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat
import com.kevalpatel2106.common.notifications.NotificationChannelType
import com.kevalpatel2106.common.notifications.addDailySummaryNotificationChannel
import com.standup.core.R

/**
 * Class to manage [Notification] with previous day summary.
 *
 * @see DailyReviewJob
 */
@Suppress("DEPRECATION")
internal object DailyReviewNotification {

    /**
     * Unique notification id.
     */
    @Suppress("MayBeConstant")
    private val NOTIFICATION_ID = 9870

    /**
     * Fire the [DailyReviewNotification]. This will add [NotificationManager.addDailySummaryNotificationChannel]
     * for this notification.
     *
     * @see NotificationManager.addDailySummaryNotificationChannel
     */
    @SuppressLint("VisibleForTests")
    internal fun notify(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.addDailySummaryNotificationChannel(context.applicationContext)
        nm.notify(NOTIFICATION_ID, buildNotification(context).build())
    }

    /**
     * Build the [DailyReviewNotification].
     *
     * @see NotificationCompat.Builder
     */
    @VisibleForTesting
    internal fun buildNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_daily_review_notification)
                .setContentTitle(context.getString(R.string.daily_review_notification_title))
                .setContentText(context.getString(R.string.daily_review_notification_message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_daily_review_notification))
                .setTicker(context.getString(R.string.daily_review_notification_message))
                .setChannelId(NotificationChannelType.DAILY_SUMMARY_NOTIFICATION_CHANNEL)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.daily_review_notification_message))
                        .setBigContentTitle(context.getString(R.string.daily_review_notification_title)))
    }

    /**
     * Cancel [DailyReviewNotification].
     *
     * @see NotificationManager.cancel
     */
    internal fun cancel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)
    }
}
