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

import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 31-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DailyReviewHelperTest {

    @Test
    fun checkShouldScheduleJob_WhenUserNotLoggedIn() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        Mockito.`when`(sharedPrefsProvider.getStringFromPreferences(anyString(), isNull())).thenReturn(null)
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong())).thenReturn(0)
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(anyString(), anyBoolean())).thenReturn(true)

        Assert.assertFalse(DailyReviewHelper.shouldRunningThisJob(UserSessionManager(sharedPrefsProvider),
                UserSettingsManager(sharedPrefsProvider)))
    }

    @Test
    fun checkShouldScheduleJob_WhenDailyReviewDisable() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        Mockito.`when`(sharedPrefsProvider.getStringFromPreferences(anyString(), isNull())).thenReturn("test-token")
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong())).thenReturn(15613L)
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(anyString(), anyBoolean())).thenReturn(false)

        Assert.assertFalse(DailyReviewHelper.shouldRunningThisJob(UserSessionManager(sharedPrefsProvider),
                UserSettingsManager(sharedPrefsProvider)))
    }

    @Test
    fun checkShouldScheduleJobPositive() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        Mockito.`when`(sharedPrefsProvider.getStringFromPreferences(anyString(), isNull())).thenReturn("test-token")
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong())).thenReturn(15613L)
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(anyString(), anyBoolean())).thenReturn(true)

        Assert.assertTrue(DailyReviewHelper.shouldRunningThisJob(UserSessionManager(sharedPrefsProvider),
                UserSettingsManager(sharedPrefsProvider)))
    }

    @Test
    fun checkNextAlarmTime_ForToday() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val dailyAlarmTimeFrom12Am = TimeUtils.millsFromMidnight(System.currentTimeMillis()) + 1800_000L

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(dailyAlarmTimeFrom12Am)

        val nextAlarmTime = DailyReviewHelper.getNextAlarmTime(UserSettingsManager(sharedPrefsProvider))
        Assert.assertEquals(nextAlarmTime, TimeUtils.todayMidnightCal().timeInMillis + dailyAlarmTimeFrom12Am)
    }

    @Test
    fun checkNextAlarmTime_ForTomorrow() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val dailyAlarmTimeFrom12Am = TimeUtils.millsFromMidnight(System.currentTimeMillis()) - 1800_000

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(dailyAlarmTimeFrom12Am)


        val nextAlarmTime = DailyReviewHelper.getNextAlarmTime(UserSettingsManager(sharedPrefsProvider))
        Assert.assertEquals(nextAlarmTime, TimeUtils.tomorrowMidnightCal().timeInMillis + dailyAlarmTimeFrom12Am)
    }
}
