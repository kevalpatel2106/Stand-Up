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

package com.standup.core.activityMonitor

import android.content.SharedPreferences
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import java.io.IOException

/**
 * Created by Keval on 06/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ShouldMonitorActivityTest {

    @Test
    @Throws(IOException::class)
    fun checkShouldMonitor_UserNotLoggedIn() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(sharedPref.getLong(anyString(), anyLong())).thenReturn(12345L)
        Mockito.`when`(sharedPref.getString(anyString(), isNull())).thenReturn(null)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode()).thenReturn(true)

        Assert.assertFalse(ActivityMonitorHelper.shouldMonitoringActivity(
                UserSessionManager(SharedPrefsProvider(sharedPref)), userSettingsManager))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldMonitor_InSleepModeUserLogIn() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(sharedPref.getLong(anyString(), anyLong())).thenReturn(12345L)
        Mockito.`when`(sharedPref.getString(anyString(), isNull())).thenReturn("test-reponseToken")

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode()).thenReturn(true)

        Assert.assertFalse(ActivityMonitorHelper.shouldMonitoringActivity(
                UserSessionManager(SharedPrefsProvider(sharedPref)), userSettingsManager))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldMonitor_NoSleepModeUserLogIn() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(sharedPref.getLong(anyString(), anyLong())).thenReturn(12345L)
        Mockito.`when`(sharedPref.getString(anyString(), isNull())).thenReturn("test-reponseToken")

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode()).thenReturn(false)

        Assert.assertTrue(ActivityMonitorHelper.shouldMonitoringActivity(
                UserSessionManager(SharedPrefsProvider(sharedPref)), userSettingsManager)
        )
    }
}
