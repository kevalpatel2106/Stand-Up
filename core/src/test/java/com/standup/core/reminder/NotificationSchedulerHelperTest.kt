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

package com.standup.core.reminder

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioManager
import android.os.PowerManager
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 05-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class NotificationSchedulerHelperTest {


    @Test
    @Throws(IOException::class)
    fun checkShouldRemind_Positive() {
        val userSessionManager = Mockito.mock(UserSessionManager::class.java)
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode).thenReturn(false)
        Mockito.`when`(userSettingsManager.isCurrentlyDndEnable).thenReturn(false)
        Mockito.`when`(userSettingsManager.sleepStartTime).thenReturn(0)
        Mockito.`when`(userSettingsManager.sleepEndTime).thenReturn(10_000)

        Assert.assertTrue(NotificationSchedulerHelper
                .shouldDisplayNotification(userSessionManager, userSettingsManager))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldRemind_WhenUserNotLoggedIn() {
        val userSessionManager = Mockito.mock(UserSessionManager::class.java)
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(false)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode).thenReturn(false)
        Mockito.`when`(userSettingsManager.isCurrentlyDndEnable).thenReturn(false)
        Mockito.`when`(userSettingsManager.sleepStartTime).thenReturn(0)
        Mockito.`when`(userSettingsManager.sleepEndTime).thenReturn(Long.MAX_VALUE)

        Assert.assertFalse(NotificationSchedulerHelper
                .shouldDisplayNotification(userSessionManager, userSettingsManager))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldRemind_WhenUserLoggedInDndDisableNoSleepMode() {
        val userSessionManager = Mockito.mock(UserSessionManager::class.java)
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode).thenReturn(false)
        Mockito.`when`(userSettingsManager.isCurrentlyDndEnable).thenReturn(false)
        Mockito.`when`(userSettingsManager.sleepStartTime).thenReturn(Long.MAX_VALUE)
        Mockito.`when`(userSettingsManager.sleepEndTime).thenReturn(0)

        Assert.assertTrue(NotificationSchedulerHelper
                .shouldDisplayNotification(userSessionManager, userSettingsManager))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldRemind_WhenUserLoggedInDndEnableNoSleepMode() {
        val userSessionManager = Mockito.mock(UserSessionManager::class.java)
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode).thenReturn(false)
        Mockito.`when`(userSettingsManager.isCurrentlyDndEnable).thenReturn(true)
        Mockito.`when`(userSettingsManager.sleepStartTime).thenReturn(0)
        Mockito.`when`(userSettingsManager.sleepEndTime).thenReturn(Long.MAX_VALUE)

        Assert.assertFalse(NotificationSchedulerHelper
                .shouldDisplayNotification(userSessionManager, userSettingsManager))
    }


    @Test
    @Throws(IOException::class)
    fun checkShouldRemind_WhenUserLoggedInDndDisableSleepModeOn1() {
        val userSessionManager = Mockito.mock(UserSessionManager::class.java)
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode).thenReturn(true)
        Mockito.`when`(userSettingsManager.isCurrentlyDndEnable).thenReturn(false)
        Mockito.`when`(userSettingsManager.sleepStartTime).thenReturn(0)
        Mockito.`when`(userSettingsManager.sleepEndTime).thenReturn(Long.MAX_VALUE)

        Assert.assertFalse(NotificationSchedulerHelper
                .shouldDisplayNotification(userSessionManager, userSettingsManager))
    }


    @Test
    @Throws(IOException::class)
    fun checkShouldRemind_WhenUserLoggedInDndDisableSleepModeOn2() {
        val userSessionManager = Mockito.mock(UserSessionManager::class.java)
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode).thenReturn(false)
        Mockito.`when`(userSettingsManager.isCurrentlyDndEnable).thenReturn(false)
        Mockito.`when`(userSettingsManager.sleepEndTime).thenReturn(System.currentTimeMillis())
        Mockito.`when`(userSettingsManager.sleepStartTime).thenReturn(0)

        Assert.assertFalse(NotificationSchedulerHelper
                .shouldDisplayNotification(userSessionManager, userSettingsManager))
    }


    @Test
    @Throws(IOException::class)
    fun checkShouldDisplayPopUp_PopUpDisable() {
        val mockContext = Mockito.mock(Context::class.java)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.shouldDisplayPopUp).thenReturn(false)

        Assert.assertFalse(NotificationSchedulerHelper.shouldDisplayPopUp(userSettingsManager, mockContext))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldDisplayPopUp_PopUpEnable_ScreenOff() {
        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.shouldDisplayPopUp).thenReturn(true)

        val context = Mockito.mock(Context::class.java)
        val powerManager = Mockito.mock(PowerManager::class.java)
        Mockito.`when`(powerManager.isScreenOn).thenReturn(false)
        Mockito.`when`(context.getSystemService(Context.POWER_SERVICE)).thenReturn(powerManager)

        val keyguardManager = Mockito.mock(KeyguardManager::class.java)
        Mockito.`when`(keyguardManager.inKeyguardRestrictedInputMode()).thenReturn(false)
        Mockito.`when`(context.getSystemService(Context.KEYGUARD_SERVICE)).thenReturn(keyguardManager)

        Assert.assertFalse(NotificationSchedulerHelper.shouldDisplayPopUp(userSettingsManager,
                context))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldDisplayPopUp_PopUpEnable_ScreenOn_DeviceLock() {
        val context = Mockito.mock(Context::class.java)

        val powerManager = Mockito.mock(PowerManager::class.java)
        Mockito.`when`(powerManager.isScreenOn).thenReturn(true)
        Mockito.`when`(context.getSystemService(Context.POWER_SERVICE)).thenReturn(powerManager)

        val keyguardManager = Mockito.mock(KeyguardManager::class.java)
        Mockito.`when`(keyguardManager.inKeyguardRestrictedInputMode()).thenReturn(true)
        Mockito.`when`(context.getSystemService(Context.KEYGUARD_SERVICE)).thenReturn(keyguardManager)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.shouldDisplayPopUp).thenReturn(true)

        Assert.assertFalse(NotificationSchedulerHelper.shouldDisplayPopUp(userSettingsManager, context))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldDisplayPopUp_Positive() {
        val context = Mockito.mock(Context::class.java)

        val powerManager = Mockito.mock(PowerManager::class.java)
        Mockito.`when`(powerManager.isScreenOn).thenReturn(true)
        Mockito.`when`(context.getSystemService(Context.POWER_SERVICE)).thenReturn(powerManager)

        val keyguardManager = Mockito.mock(KeyguardManager::class.java)
        Mockito.`when`(keyguardManager.inKeyguardRestrictedInputMode()).thenReturn(false)
        Mockito.`when`(context.getSystemService(Context.KEYGUARD_SERVICE)).thenReturn(keyguardManager)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.shouldDisplayPopUp).thenReturn(true)

        Assert.assertTrue(NotificationSchedulerHelper.shouldDisplayPopUp(userSettingsManager, context))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldVibrate_VibrateOff() {
        val context = Mockito.mock(Context::class.java)

        val audioManager = Mockito.mock(AudioManager::class.java)
        Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_SILENT)
        Mockito.`when`(context.getSystemService(Context.AUDIO_SERVICE)).thenReturn(audioManager)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.shouldVibrate).thenReturn(false)

        Assert.assertFalse(NotificationSchedulerHelper.shouldVibrate(context, userSettingsManager))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldVibrate_VibrateOn_RingerSilent() {
        val context = Mockito.mock(Context::class.java)

        val audioManager = Mockito.mock(AudioManager::class.java)
        Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_SILENT)
        Mockito.`when`(context.getSystemService(Context.AUDIO_SERVICE)).thenReturn(audioManager)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.shouldVibrate).thenReturn(true)

        Assert.assertFalse(NotificationSchedulerHelper.shouldVibrate(context, userSettingsManager))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldVibrate_VibrateOn_RingerNormal() {
        val context = Mockito.mock(Context::class.java)

        val audioManager = Mockito.mock(AudioManager::class.java)
        Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_NORMAL)
        Mockito.`when`(context.getSystemService(Context.AUDIO_SERVICE)).thenReturn(audioManager)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.shouldVibrate).thenReturn(true)

        Assert.assertTrue(NotificationSchedulerHelper.shouldVibrate(context, userSettingsManager))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldVibrate_VibrateOn_RingerVibrate() {
        val context = Mockito.mock(Context::class.java)

        val audioManager = Mockito.mock(AudioManager::class.java)
        Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_VIBRATE)
        Mockito.`when`(context.getSystemService(Context.AUDIO_SERVICE)).thenReturn(audioManager)

        val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        Mockito.`when`(userSettingsManager.shouldVibrate).thenReturn(true)

        Assert.assertTrue(NotificationSchedulerHelper.shouldVibrate(context, userSettingsManager))
    }
}