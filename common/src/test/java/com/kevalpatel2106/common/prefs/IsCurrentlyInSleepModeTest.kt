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

package com.kevalpatel2106.common.prefs

import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 06-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class IsCurrentlyInSleepModeTest {

    @Test
    fun checkIsCurrentlyInSleepMode_WhenCurrentTimeBeforeDndStartTime() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)

        //Half an hour before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong()))
                .thenReturn(TimeUtils.millsFromMidnight(System.currentTimeMillis()) - 1800_000L)

        Assert.assertFalse(UserSettingsManager(sharedPrefsProvider).isCurrentlyInSleepMode())
    }

    @Test
    fun checkIsCurrentlyInSleepMode_WhenCurrentTimeAfterDndEndTime() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        //Half an hour before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(ArgumentMatchers.startsWith(SharedPreferenceKeys.PREF_KEY_SLEEP_START_TIME_FROM_12AM), ArgumentMatchers.anyLong()))
                .thenReturn(TimeUtils.millsFromMidnight(System.currentTimeMillis()) - 1800_000L)

        //15 mins before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(ArgumentMatchers.startsWith(SharedPreferenceKeys.PREF_KEY_SLEEP_END_TIME_FROM_12AM), ArgumentMatchers.anyLong()))
                .thenReturn(TimeUtils.millsFromMidnight(System.currentTimeMillis()) - 900_000L)

        Assert.assertFalse(UserSettingsManager(sharedPrefsProvider).isCurrentlyInSleepMode())
    }

    @Test
    fun checkIsCurrentlyInSleepMode_Positive() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)

        //15 mins before current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(ArgumentMatchers.startsWith(SharedPreferenceKeys.PREF_KEY_SLEEP_START_TIME_FROM_12AM), ArgumentMatchers.anyLong()))
                .thenReturn(TimeUtils.millsFromMidnight(System.currentTimeMillis()) - 900_000L)

        //15 mins after current time
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(ArgumentMatchers.startsWith(SharedPreferenceKeys.PREF_KEY_SLEEP_END_TIME_FROM_12AM), ArgumentMatchers.anyLong()))
                .thenReturn(TimeUtils.millsFromMidnight(System.currentTimeMillis()) + 900_000L)

        Assert.assertTrue(UserSettingsManager(sharedPrefsProvider).isCurrentlyInSleepMode())
    }
}
