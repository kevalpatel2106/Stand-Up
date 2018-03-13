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

import android.content.Context
import android.support.v4.app.NotificationCompat
import com.kevalpatel2106.common.notifications.NotificationChannelType
import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.standup.core.R
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.io.IOException
import java.util.*


/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DailyReviewNotificationTest {
    private val TEST_TITLE_STRING = "This is test notification title."
    private val TEST_MESSAGE_STRING = "You were sitting for %1\$s yesterday. Your efficiency is %2$.2f%%."

    private lateinit var notification: NotificationCompat.Builder
    private lateinit var mockContext: Context
    private lateinit var dailyActivitySummary: DailyActivitySummary

    @Before
    fun setUp() {
        mockContext = Mockito.mock(Context::class.java)
        Mockito.`when`(mockContext.getString(R.string.daily_review_notification_title))
                .thenReturn(TEST_TITLE_STRING)
        Mockito.`when`(mockContext.getString(R.string.daily_review_notification_message))
                .thenReturn(TEST_MESSAGE_STRING)

        dailyActivitySummary = createDailySummary()
        notification = DailyReviewNotification.buildNotification(mockContext, dailyActivitySummary)
    }

    private fun createDailySummary(): DailyActivitySummary {
        val userActivities = ArrayList<UserActivity>(1)
        userActivities.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 60_000,
                isSynced = true,
                type = UserActivityType.MOVING.name.toLowerCase()))
        userActivities.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 120_000,
                isSynced = true,
                type = UserActivityType.SITTING.name.toLowerCase()))
        return DailyActivitySummary(dayOfMonth = 1,
                monthOfYear = 11,
                year = 2017,
                dayActivity = userActivities)
    }

    @Test
    @Throws(IOException::class)
    fun testPrepareSummaryMessage() {
        val message = DailyReviewNotification.prepareSummaryMessage(mockContext, dailyActivitySummary)
        Assert.assertEquals(message, String.format(TEST_MESSAGE_STRING,
                dailyActivitySummary.sittingTimeHours, dailyActivitySummary.sittingPercent))
    }


    @Test
    @Throws(IOException::class)
    fun testNotificationPriority() {
        assertEquals(notification.priority, NotificationCompat.PRIORITY_HIGH)
    }

    @Test
    @Throws(IOException::class)
    fun testNotificationChannel() {
        //Channel id
        val channelId = notification.javaClass.getDeclaredField("mChannelId")
        channelId.isAccessible = true
        Assert.assertEquals(channelId.get(notification) as String, NotificationChannelType.DAILY_SUMMARY_NOTIFICATION_CHANNEL)
    }

    @Test
    @Throws(IOException::class)
    fun testNotificationTitle() {
        //notification title
        val title = notification.javaClass.getDeclaredField("mContentTitle")
        title.isAccessible = true
        Assert.assertEquals(title.get(notification) as String, TEST_TITLE_STRING)
    }

    @Test
    @Throws(IOException::class)
    fun testNotificationMessage() {
        //notification message
        val message = notification.javaClass.getDeclaredField("mContentText")
        message.isAccessible = true
        Assert.assertEquals(message.get(notification) as String, String.format(TEST_MESSAGE_STRING,
                dailyActivitySummary.sittingTimeHours, dailyActivitySummary.sittingPercent))
    }

    @Test
    @Throws(IOException::class)
    fun testNotificationStyle() {
        //notification style
        val style = notification.javaClass.getDeclaredField("mStyle")
        style.isAccessible = true
        Assert.assertTrue(style.get(notification) is NotificationCompat.BigTextStyle)
    }
}
