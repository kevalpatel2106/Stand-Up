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

package com.standup.core.dndManager

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
 * Created by Kevalpatel2106 on 31-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class AutoDndMonitoringHelperTest {

    @Test
    fun checkAutoDndStartTiming_ForToday() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val autoDndStartTimeFrom12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) + 1800_000L

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(autoDndStartTimeFrom12Am)

        val nextAlarmTime = AutoDndMonitoringHelper.getAutoDndStartTiming(UserSettingsManager(sharedPrefsProvider))
        Assert.assertEquals(nextAlarmTime, TimeUtils.getTodaysCalender12AM().timeInMillis + autoDndStartTimeFrom12Am)
    }

    @Test
    fun checkAutoDndStartTiming_ForTomorrow() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val autoDndStartTimeFrom12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 1800_000

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(autoDndStartTimeFrom12Am)


        val nextAlarmTime = AutoDndMonitoringHelper.getAutoDndStartTiming(UserSettingsManager(sharedPrefsProvider))
        Assert.assertEquals(nextAlarmTime, TimeUtils.getTommorowsCalender12AM().timeInMillis + autoDndStartTimeFrom12Am)
    }

    @Test
    fun checkAutoDndEndTiming_ForToday() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val autoDndEndTimeFrom12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) + 1800_000L

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(autoDndEndTimeFrom12Am)

        val nextAlarmTime = AutoDndMonitoringHelper.getAutoDndEndTiming(UserSettingsManager(sharedPrefsProvider))
        Assert.assertEquals(nextAlarmTime, TimeUtils.getTodaysCalender12AM().timeInMillis + autoDndEndTimeFrom12Am)
    }

    @Test
    fun checkAutoDndEndTiming_ForTomorrow() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val autoDndEndTimeFrom12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 1800_000

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(autoDndEndTimeFrom12Am)


        val nextAlarmTime = AutoDndMonitoringHelper.getAutoDndEndTiming(UserSettingsManager(sharedPrefsProvider))
        Assert.assertEquals(nextAlarmTime, TimeUtils.getTommorowsCalender12AM().timeInMillis + autoDndEndTimeFrom12Am)
    }

    @Test
    fun checkShouldScheduleJob_WhenUserNotLoggedIn() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        Mockito.`when`(sharedPrefsProvider.getStringFromPreferences(anyString(), isNull())).thenReturn(null)
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong())).thenReturn(0)
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(anyString(), anyBoolean())).thenReturn(true)

        Assert.assertFalse(AutoDndMonitoringHelper.shouldRunningThisJob(UserSettingsManager(sharedPrefsProvider),
                UserSessionManager(sharedPrefsProvider)))
    }

    @Test
    fun checkShouldScheduleJob_WhenAutoDndDisable() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        Mockito.`when`(sharedPrefsProvider.getStringFromPreferences(anyString(), isNull())).thenReturn("test-token")
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong())).thenReturn(15613L)
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(anyString(), anyBoolean())).thenReturn(false)

        Assert.assertFalse(AutoDndMonitoringHelper.shouldRunningThisJob(UserSettingsManager(sharedPrefsProvider),
                UserSessionManager(sharedPrefsProvider)))
    }

    @Test
    fun checkShouldScheduleJobPositive() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        Mockito.`when`(sharedPrefsProvider.getStringFromPreferences(anyString(), isNull())).thenReturn("test-token")
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong())).thenReturn(15613L)
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(anyString(), anyBoolean())).thenReturn(true)

        Assert.assertTrue(AutoDndMonitoringHelper.shouldRunningThisJob(UserSettingsManager(sharedPrefsProvider),
                UserSessionManager(sharedPrefsProvider)))
    }

    @Test
    fun checkIsCurrentlyInAutoDndMode_WhenAutoDndDisable() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(anyString(), anyBoolean())).thenReturn(false)

        Assert.assertFalse(AutoDndMonitoringHelper.isCurrentlyInAutoDndMode(UserSettingsManager(sharedPrefsProvider)))
    }

    @Test
    fun checkIsCurrentlyInAutoDndMode_WhenCurrentTimeBeforeDndStartTime() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        //Make auto dnd enable
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(anyString(), anyBoolean())).thenReturn(true)

        //Half an hour before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(anyString(), anyLong()))
                .thenReturn(TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 1800_000L)

        Assert.assertFalse(AutoDndMonitoringHelper.isCurrentlyInAutoDndMode(UserSettingsManager(sharedPrefsProvider)))
    }

    @Test
    fun checkIsCurrentlyInAutoDndMode_WhenCurrentTimeAfterDndEndTime() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        //Make auto dnd enable
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(anyString(), anyBoolean())).thenReturn(true)

        //Half an hour before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(startsWith(SharedPreferenceKeys.PREF_KEY_AUTO_DND_START_TIME_FROM_12AM), anyLong()))
                .thenReturn(TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 1800_000L)

        //15 mins before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(startsWith(SharedPreferenceKeys.PREF_KEY_AUTO_DND_END_TIME_FROM_12AM), anyLong()))
                .thenReturn(TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 900_000L)

        Assert.assertFalse(AutoDndMonitoringHelper.isCurrentlyInAutoDndMode(UserSettingsManager(sharedPrefsProvider)))
    }

    @Test
    fun checkIsCurrentlyInAutoDndMode_Positive() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        //Make auto dnd enable
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(anyString(), anyBoolean())).thenReturn(true)

        //15 mins before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(startsWith(SharedPreferenceKeys.PREF_KEY_AUTO_DND_START_TIME_FROM_12AM), anyLong()))
                .thenReturn(TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) - 900_000L)

        //15 mins after current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(startsWith(SharedPreferenceKeys.PREF_KEY_AUTO_DND_END_TIME_FROM_12AM), anyLong()))
                .thenReturn(TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) + 900_000L)

        Assert.assertTrue(AutoDndMonitoringHelper.isCurrentlyInAutoDndMode(UserSettingsManager(sharedPrefsProvider)))
    }
}
