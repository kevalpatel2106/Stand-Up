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

package com.kevalpatel2106.standup.authentication.deviceReg

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.fcm.NotificationChannelType
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 05-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DeviceRegisterNotificationTest {

    private val TEST_TITLE_STRING = "This is test notification title."
    private val TEST_MESSAGE_STRING = "This is test notification message."

    private lateinit var notification: NotificationCompat.Builder

    @Before
    fun setUp() {
        val context = Mockito.mock(Context::class.java)
        val resources = Mockito.mock(Resources::class.java)
        Mockito.`when`(context.getString(R.string.application_name)).thenReturn(TEST_TITLE_STRING)
        Mockito.`when`(context.getString(R.string.register_device_service_notification_message)).thenReturn(TEST_MESSAGE_STRING)
        Mockito.`when`(context.getColor(ArgumentMatchers.anyInt())).thenReturn(Color.RED)
        Mockito.`when`(context.resources).thenReturn(resources)
        Mockito.`when`(context.resources.getColor(ArgumentMatchers.anyInt())).thenReturn(Color.RED)

        notification = DeviceRegisterNotification.buildNotification(context)
    }

    @Test
    @Throws(IOException::class)
    fun testNotificationPriority() {
        Assert.assertEquals(notification.priority, NotificationCompat.PRIORITY_LOW)
    }

    @Test
    @Throws(IOException::class)
    fun testNotificationColor() {
        Assert.assertEquals(notification.color, 0 /* Color not set */)
    }

    @Test
    @Throws(IOException::class)
    fun testNotificationChannel() {
        //Channel id
        val channelId = notification.javaClass.getDeclaredField("mChannelId")
        channelId.isAccessible = true
        Assert.assertEquals(channelId.get(notification) as String, NotificationChannelType.SYNC_NOTIFICATION_CHANNEL)
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