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

package com.standup.core.sleepManager

import com.kevalpatel2106.common.SharedPreferenceKeys
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito

/**
 * Created by Keval on 31/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SleepModeMonitoringHelperTest {

    @Test
    fun checkSleepStartTiming_ForToday() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val sleepStartTimeFrom12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) + 1800_000L

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(sleepStartTimeFrom12Am)

        val nextAlarmTime = SleepModeMonitoringHelper.getSleepStartTiming(UserSettingsManager(sharedPrefsProvider))
        Assert.assertEquals(nextAlarmTime, TimeUtils.getTodaysCalender12AM().timeInMillis + sleepStartTimeFrom12Am)
    }

    @Test
    fun checkSleepStartTiming_ForTomorrow() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val sleepStartTimeFrom12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 1800_000

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(sleepStartTimeFrom12Am)


        val nextAlarmTime = SleepModeMonitoringHelper.getSleepStartTiming(UserSettingsManager(sharedPrefsProvider))
        Assert.assertEquals(nextAlarmTime, TimeUtils.getTommorowsCalender12AM().timeInMillis + sleepStartTimeFrom12Am)
    }

    @Test
    fun checkSleepEndTiming_ForToday() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val sleepEndTimeFrom12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) + 1800_000L

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(sleepEndTimeFrom12Am)

        val nextAlarmTime = SleepModeMonitoringHelper.getSleepEndTiming(UserSettingsManager(sharedPrefsProvider))
        Assert.assertEquals(nextAlarmTime, TimeUtils.getTodaysCalender12AM().timeInMillis + sleepEndTimeFrom12Am)
    }

    @Test
    fun checkSleepEndTiming_ForTomorrow() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val sleepEndTimeFrom12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 1800_000

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(sleepEndTimeFrom12Am)


        val nextAlarmTime = SleepModeMonitoringHelper.getSleepEndTiming(UserSettingsManager(sharedPrefsProvider))
        Assert.assertEquals(nextAlarmTime, TimeUtils.getTommorowsCalender12AM().timeInMillis + sleepEndTimeFrom12Am)
    }

    @Test
    fun checkShouldScheduleJob_WhenUserNotLoggedIn() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        Mockito.`when`(sharedPrefsProvider.getStringFromPreferences(anyString(), isNull())).thenReturn(null)
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong())).thenReturn(0)

        Assert.assertFalse(SleepModeMonitoringHelper.shouldRunningJob(UserSessionManager(sharedPrefsProvider)))
    }

    @Test
    fun checkShouldScheduleJobPositive() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        Mockito.`when`(sharedPrefsProvider.getStringFromPreferences(anyString(), isNull())).thenReturn("test-token")
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong())).thenReturn(15613L)

        Assert.assertTrue(SleepModeMonitoringHelper.shouldRunningJob(UserSessionManager(sharedPrefsProvider)))
    }

    @Test
    fun checkIsCurrentlyInSleepMode_WhenCurrentTimeBeforeDndStartTime() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)

        //Half an hour before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 1800_000L)

        Assert.assertFalse(SleepModeMonitoringHelper.isCurrentlyInSleepMode(UserSettingsManager(sharedPrefsProvider)))
    }

    @Test
    fun checkIsCurrentlyInSleepMode_WhenCurrentTimeAfterDndEndTime() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        //Half an hour before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(startsWith(SharedPreferenceKeys.PREF_KEY_SLEEP_START_TIME_FROM_12AM), anyLong()))
                .thenReturn(TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 1800_000L)

        //15 mins before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(startsWith(SharedPreferenceKeys.PREF_KEY_SLEEP_END_TIME_FROM_12AM), anyLong()))
                .thenReturn(TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 900_000L)

        Assert.assertFalse(SleepModeMonitoringHelper.isCurrentlyInSleepMode(UserSettingsManager(sharedPrefsProvider)))
    }

    @Test
    fun checkIsCurrentlyInSleepMode_Positive() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)

        //15 mins before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(startsWith(SharedPreferenceKeys.PREF_KEY_SLEEP_START_TIME_FROM_12AM), anyLong()))
                .thenReturn(TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 900_000L)

        //15 mins after current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(startsWith(SharedPreferenceKeys.PREF_KEY_SLEEP_END_TIME_FROM_12AM), anyLong()))
                .thenReturn(TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) + 900_000L)

        Assert.assertTrue(SleepModeMonitoringHelper.isCurrentlyInSleepMode(UserSettingsManager(sharedPrefsProvider)))
    }
}