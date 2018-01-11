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

package com.kevalpatel2106.standup.reminder.notification

import android.content.Context
import android.content.SharedPreferences
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.RetryStrategy
import com.kevalpatel2106.standup.reminder.ReminderConfig
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 05-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class NotificationSchedulerHelperTest {

    @Test
    @Throws(IOException::class)
    fun checkPrepareJobAfterDefaultTime() {
        val builder = NotificationSchedulerHelper.prepareJob(
                context = RuntimeEnvironment.application,
                sharedPrefsProvider = SharedPrefsProvider(MockSharedPreference())
        )

        Assert.assertEquals(builder.constraints.size, 0)
        Assert.assertFalse(builder.isRecurring)
        Assert.assertEquals(builder.tag, NotificationSchedulerHelper.REMINDER_NOTIFICATION_JOB_TAG)
        Assert.assertEquals(builder.retryStrategy, RetryStrategy.DEFAULT_LINEAR)
        Assert.assertEquals(builder.lifetime, Lifetime.UNTIL_NEXT_BOOT)
        Assert.assertEquals(builder.service, NotificationSchedulerService::class.java.canonicalName)
    }


    @Test
    @Throws(IOException::class)
    fun checkShouldNotSyncSync() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(sharedPref.getLong(anyString(), anyLong())).thenReturn(0L)
        Mockito.`when`(sharedPref.getString(anyString(), isNull())).thenReturn(null)

        Assert.assertFalse(NotificationSchedulerHelper.shouldDisplayNotification(
                UserSessionManager(SharedPrefsProvider(sharedPref))
        ))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldSyncSync() {
        val context = Mockito.mock(Context::class.java)
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPref)
        Mockito.`when`(sharedPref.getLong(anyString(), anyLong())).thenReturn(12345L)
        Mockito.`when`(sharedPref.getString(anyString(), isNull())).thenReturn("test-reponseToken")

        Assert.assertTrue(NotificationSchedulerHelper.shouldDisplayNotification(
                UserSessionManager(SharedPrefsProvider(sharedPref))
        ))
    }

    @Test
    @Throws(IOException::class)
    fun checkGetExecutionWindow() {
        val executionWindow = NotificationSchedulerHelper.getExecutionWindow(3600)
        Assert.assertEquals(executionWindow.windowStart,
                3600 - ReminderConfig.NOTIFICATION_SERVICE_PERIOD_TOLERANCE)
        Assert.assertEquals(executionWindow.windowEnd,
                3600 + ReminderConfig.NOTIFICATION_SERVICE_PERIOD_TOLERANCE)
    }
}
