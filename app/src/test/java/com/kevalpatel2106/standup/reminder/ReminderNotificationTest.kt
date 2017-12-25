package com.kevalpatel2106.standup.reminder

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.fcm.NotificationChannelType
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import java.io.IOException


/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class ReminderNotificationTest {
    private val TEST_TITLE_STRING = "This is test notification title."
    private val TEST_MESSAGE_STRING = "This is test notification message."

    private lateinit var notification: NotificationCompat.Builder

    @Before
    fun setUp() {
        val context = Mockito.mock(Context::class.java)
        val resources = Mockito.mock(Resources::class.java)
        Mockito.`when`(context.getString(R.string.reminder_notification_title)).thenReturn(TEST_TITLE_STRING)
        Mockito.`when`(context.getString(R.string.reminder_notification_message)).thenReturn(TEST_MESSAGE_STRING)
        Mockito.`when`(context.getColor(anyInt())).thenReturn(Color.RED)
        Mockito.`when`(context.resources).thenReturn(resources)
        Mockito.`when`(context.resources.getColor(anyInt())).thenReturn(Color.RED)

        notification = ReminderNotification.buildNotification(context)
    }

    @Test
    @Throws(IOException::class)
    fun testNotificationPriority() {
        assertEquals(notification.priority, NotificationCompat.PRIORITY_HIGH)
    }

    @Test
    @Throws(IOException::class)
    fun testNotificationColor() {
        assertEquals(notification.color, Color.RED)

        //Channel id
        val channelId = notification.javaClass.getDeclaredField("mChannelId")
        channelId.isAccessible = true
        Assert.assertEquals(channelId.get(notification) as String, NotificationChannelType.REMINDER_NOTIFICATION_CHANNEL)

        //notification title
        val title = notification.javaClass.getDeclaredField("mContentTitle")
        title.isAccessible = true
        Assert.assertEquals(title.get(notification) as String, TEST_TITLE_STRING)

        //notification message
        val message = notification.javaClass.getDeclaredField("mContentText")
        message.isAccessible = true
        Assert.assertEquals(message.get(notification) as String, TEST_MESSAGE_STRING)

        //notification style
        val style = notification.javaClass.getDeclaredField("mStyle")
        style.isAccessible = true
        Assert.assertTrue(style.get(notification) is NotificationCompat.BigTextStyle)
    }

    @Test
    @Throws(IOException::class)
    fun testNotificationChannel() {
        //Channel id
        val channelId = notification.javaClass.getDeclaredField("mChannelId")
        channelId.isAccessible = true
        Assert.assertEquals(channelId.get(notification) as String, NotificationChannelType.REMINDER_NOTIFICATION_CHANNEL)
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
        Assert.assertEquals(message.get(notification) as String, TEST_MESSAGE_STRING)
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