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

package com.kevalpatel2106.standup.reminder.activityMonitor

import android.content.SharedPreferences
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import com.kevalpatel2106.standup.reminder.ReminderConfig
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import java.io.IOException

/**
 * Created by Keval on 06/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ShouldScheduleNotificationTest {

    private val nextNotificationTimeLessThanHour = (System.currentTimeMillis()
            + TimeUtils.convertToMilli(ReminderConfig.STAND_UP_DURATION.toLong())
            - 120.times(1000)/* Decrease in value */)

    private val nextNotificationTimeAfterAnHour = (System.currentTimeMillis()
            + TimeUtils.convertToMilli(ReminderConfig.STAND_UP_DURATION.toLong())
            + 120.times(1000)/* Increase in value */)

    @Test
    @Throws(IOException::class)
    fun forSittingActivityWithNextNotificationInAnHour() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        //Next notification time is less than the stand up duration.
        //That means notification is scheduled in one hour (Stand Up Reminder period)
        Mockito.`when`(sharedPref.getLong(anyString(), anyLong())).thenReturn(nextNotificationTimeLessThanHour)
        SharedPrefsProvider.init(sharedPref)

        val userActivity = UserActivity(
                eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 10000,
                type = UserActivityType.SITTING.toString().toLowerCase(),
                isSynced = false
        )

        Assert.assertTrue(ActivityMonitorHelper.shouldScheduleNotification(userActivity))
    }


    @Test
    @Throws(IOException::class)
    fun forSittingActivityWithNextNotificationAfterAnHour() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        //Next notification time is more than the stand up duration.
        Mockito.`when`(sharedPref.getLong(anyString(), anyLong())).thenReturn(nextNotificationTimeAfterAnHour)
        SharedPrefsProvider.init(sharedPref)

        val userActivity = UserActivity(
                eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 10000,
                type = UserActivityType.SITTING.toString().toLowerCase(),
                isSynced = false
        )

        Assert.assertFalse(ActivityMonitorHelper.shouldScheduleNotification(userActivity))
    }

    @Test
    @Throws(IOException::class)
    fun forNonTrackedActivityWithNextNotificationInAnHour() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        //Next notification time is less than the stand up duration.
        //That means notification is scheduled in one hour (Stand Up Reminder period)
        Mockito.`when`(sharedPref.getLong(anyString(), anyLong())).thenReturn(nextNotificationTimeLessThanHour)
        SharedPrefsProvider.init(sharedPref)

        val userActivity = UserActivity(
                eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 10000,
                type = UserActivityType.NOT_TRACKED.toString().toLowerCase(),
                isSynced = false
        )

        Assert.assertTrue(ActivityMonitorHelper.shouldScheduleNotification(userActivity))
    }

    @Test
    @Throws(IOException::class)
    fun forNonTrackedActivityWithNextNotificationAfterAnHour() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        //Next notification time is more than the stand up duration.
        Mockito.`when`(sharedPref.getLong(anyString(), anyLong())).thenReturn(nextNotificationTimeAfterAnHour)
        SharedPrefsProvider.init(sharedPref)

        val userActivity = UserActivity(
                eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 10000,
                type = UserActivityType.NOT_TRACKED.toString().toLowerCase(),
                isSynced = false
        )

        Assert.assertFalse(ActivityMonitorHelper.shouldScheduleNotification(userActivity))
    }


    @Test
    @Throws(IOException::class)
    fun forMovingActivityWithNextNotificationInAnHour() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        //Next notification time is less than the stand up duration.
        //That means notification is scheduled in one hour (Stand Up Reminder period)
        Mockito.`when`(sharedPref.getLong(anyString(), anyLong())).thenReturn(nextNotificationTimeLessThanHour)
        SharedPrefsProvider.init(sharedPref)

        val userActivity = UserActivity(
                eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 10000,
                type = UserActivityType.MOVING.toString().toLowerCase(),
                isSynced = false
        )

        Assert.assertTrue(ActivityMonitorHelper.shouldScheduleNotification(userActivity))
    }

    @Test
    @Throws(IOException::class)
    fun forMovingActivityWithNextNotificationAfterAnHour() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        //Next notification time is more than the stand up duration.
        Mockito.`when`(sharedPref.getLong(anyString(), anyLong())).thenReturn(nextNotificationTimeAfterAnHour)
        SharedPrefsProvider.init(sharedPref)

        val userActivity = UserActivity(
                eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 10000,
                type = UserActivityType.MOVING.toString().toLowerCase(),
                isSynced = false
        )

        Assert.assertTrue(ActivityMonitorHelper.shouldScheduleNotification(userActivity))
    }
}