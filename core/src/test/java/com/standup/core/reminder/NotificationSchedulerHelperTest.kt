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
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.getAudioDevice
import android.os.PowerManager
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobManagerRule
import com.evernote.android.job.JobRequest
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.standup.core.CoreTestUtils
import com.standup.core.misc.CoreJobCreator
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 05-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(Enclosed::class)
class NotificationSchedulerHelperTest {

    @RunWith(JUnit4::class)
    class ShouldDisplayNotificationTest {

        @Test
        @Throws(IOException::class)
        fun checkShouldRemind_Positive() {
            val userSessionManager = Mockito.mock(UserSessionManager::class.java)
            Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)

            val userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
            Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode()).thenReturn(false)
            Mockito.`when`(userSettingsManager.isCurrentlyInDnd).thenReturn(false)
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
            Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode()).thenReturn(false)
            Mockito.`when`(userSettingsManager.isCurrentlyInDnd).thenReturn(false)
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
            Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode()).thenReturn(false)
            Mockito.`when`(userSettingsManager.isCurrentlyInDnd).thenReturn(false)
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
            Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode()).thenReturn(false)
            Mockito.`when`(userSettingsManager.isCurrentlyInDnd).thenReturn(true)
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
            Mockito.`when`(userSettingsManager.isCurrentlyInSleepMode()).thenReturn(true)
            Mockito.`when`(userSettingsManager.isCurrentlyInDnd).thenReturn(false)
            Mockito.`when`(userSettingsManager.sleepStartTime).thenReturn(0)
            Mockito.`when`(userSettingsManager.sleepEndTime).thenReturn(Long.MAX_VALUE)

            Assert.assertFalse(NotificationSchedulerHelper
                    .shouldDisplayNotification(userSessionManager, userSettingsManager))
        }
    }

    @RunWith(JUnit4::class)
    class ShouldDisplayPopUpTest {

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
    }

    @RunWith(JUnit4::class)
    class ShouldVibrateTest {
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

    @RunWith(RobolectricTestRunner::class)
    @Config(sdk = [21], manifest = Config.NONE)
    class ShouldPlaySoundTest {

        private lateinit var mockContext: Context
        private lateinit var audioManager: AudioManager
        private lateinit var userSettingsManager: UserSettingsManager

        @Before
        fun setUp() {
            audioManager = Mockito.mock(AudioManager::class.java)

            mockContext = Mockito.mock(Context::class.java)
            Mockito.`when`(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(audioManager)

            userSettingsManager = Mockito.mock(UserSettingsManager::class.java)
        }

        /////////// Normal mode

        @Test
        @Throws(Exception::class)
        fun checkShouldPlaySound_NormalMode() {
            Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_NORMAL)

            Assert.assertTrue(NotificationSchedulerHelper.shouldPlaySound(mockContext, userSettingsManager))
        }

        /////////// Silent mode

        @Test
        @Throws(Exception::class)
        fun checkShouldPlaySound_SilentMode_DontPlayInSilentMode() {
            Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_SILENT)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundInSilent).thenReturn(false)

            Assert.assertFalse(NotificationSchedulerHelper.shouldPlaySound(mockContext, userSettingsManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkShouldPlaySound_SilentMode_PlayReminderAlways() {
            Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_SILENT)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundInSilent).thenReturn(true)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundAlways).thenReturn(true)

            Assert.assertTrue(NotificationSchedulerHelper.shouldPlaySound(mockContext, userSettingsManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkShouldPlaySound_SilentMode_PlayReminderSoundWithHeadphones_HeadphoneConnected() {
            Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_SILENT)
            Mockito.`when`(audioManager.isWiredHeadsetOn).thenReturn(true)  //Headphone connected


            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundInSilent).thenReturn(true)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundAlways).thenReturn(false)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundWithHeadphones).thenReturn(true)

            Assert.assertTrue(NotificationSchedulerHelper.shouldPlaySound(mockContext, userSettingsManager))
        }


        @Test
        @Throws(Exception::class)
        fun checkShouldPlaySound_SilentMode_PlayReminderSoundWithHeadphones_HeadphoneDisconnected() {
            Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_SILENT)
            Mockito.`when`(audioManager.isWiredHeadsetOn).thenReturn(false)  //Headphone disconnected


            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundInSilent).thenReturn(true)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundAlways).thenReturn(false)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundWithHeadphones).thenReturn(true)

            Assert.assertFalse(NotificationSchedulerHelper.shouldPlaySound(mockContext, userSettingsManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkShouldPlaySound_SilentMode_Negative() {
            Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_SILENT)
            Mockito.`when`(audioManager.isWiredHeadsetOn).thenReturn(true)  //Headphone connected

            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundInSilent).thenReturn(true)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundAlways).thenReturn(false)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundWithHeadphones).thenReturn(false)

            Assert.assertFalse(NotificationSchedulerHelper.shouldPlaySound(mockContext, userSettingsManager))
        }


        /////////// Vibrate mode

        @Test
        @Throws(Exception::class)
        fun checkShouldPlaySound_VibrateMode_DontPlayInVibrateMode() {
            Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_VIBRATE)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundInSilent).thenReturn(false)

            Assert.assertFalse(NotificationSchedulerHelper.shouldPlaySound(mockContext, userSettingsManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkShouldPlaySound_VibrateMode_PlayReminderAlways() {
            Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_VIBRATE)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundInSilent).thenReturn(true)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundAlways).thenReturn(true)

            Assert.assertTrue(NotificationSchedulerHelper.shouldPlaySound(mockContext, userSettingsManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkShouldPlaySound_VibrateMode_PlayReminderSoundWithHeadphones_HeadphoneConnected() {
            Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_VIBRATE)
            Mockito.`when`(audioManager.isWiredHeadsetOn).thenReturn(true)  //Headphone connected


            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundInSilent).thenReturn(true)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundAlways).thenReturn(false)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundWithHeadphones).thenReturn(true)

            Assert.assertTrue(NotificationSchedulerHelper.shouldPlaySound(mockContext, userSettingsManager))
        }


        @Test
        @Throws(Exception::class)
        fun checkShouldPlaySound_VibrateMode_PlayReminderSoundWithHeadphones_HeadphoneDisconnected() {
            Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_VIBRATE)
            Mockito.`when`(audioManager.isWiredHeadsetOn).thenReturn(false)  //Headphone disconnected


            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundInSilent).thenReturn(true)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundAlways).thenReturn(false)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundWithHeadphones).thenReturn(true)

            Assert.assertFalse(NotificationSchedulerHelper.shouldPlaySound(mockContext, userSettingsManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkShouldPlaySound_VibrateMode_Negative() {
            Mockito.`when`(audioManager.ringerMode).thenReturn(AudioManager.RINGER_MODE_VIBRATE)
            Mockito.`when`(audioManager.isWiredHeadsetOn).thenReturn(true)  //Headphone connected

            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundInSilent).thenReturn(true)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundAlways).thenReturn(false)
            Mockito.`when`(userSettingsManager.shouldPlayReminderSoundWithHeadphones).thenReturn(false)

            Assert.assertFalse(NotificationSchedulerHelper.shouldPlaySound(mockContext, userSettingsManager))
        }
    }

    @RunWith(RobolectricTestRunner::class)
    @Config(sdk = [21], manifest = Config.NONE)
    class IsHeadphonesConnectedApi21Test {

        private lateinit var mockContext: Context
        private lateinit var audioManager: AudioManager

        @Before
        fun setUp() {
            audioManager = Mockito.mock(AudioManager::class.java)

            mockContext = Mockito.mock(Context::class.java)
            Mockito.`when`(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(audioManager)
        }

        @Test
        @Throws(Exception::class)
        fun checkWhenWiredHeadphonesConnected() {
            Mockito.`when`(audioManager.isWiredHeadsetOn).thenReturn(true)  //Headphone connected

            Assert.assertTrue(NotificationSchedulerHelper.isHeadsetOn(audioManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkWhenBluetoothHeadphonesConnected() {
            Mockito.`when`(audioManager.isBluetoothA2dpOn).thenReturn(true)  //Headphone connected

            Assert.assertTrue(NotificationSchedulerHelper.isHeadsetOn(audioManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkWhenNoHeadphonesConnected() {
            Mockito.`when`(audioManager.isBluetoothA2dpOn).thenReturn(false)  //Headphone connected
            Mockito.`when`(audioManager.isWiredHeadsetOn).thenReturn(false)  //Headphone connected

            Assert.assertFalse(NotificationSchedulerHelper.isHeadsetOn(audioManager))
        }

    }

    @RunWith(RobolectricTestRunner::class)
    @Config(sdk = [26], manifest = Config.NONE)
    class IsHeadphonesConnectedApi26Test {

        private lateinit var mockContext: Context
        private lateinit var audioManager: AudioManager

        private lateinit var audioDevices: Array<AudioDeviceInfo>

        @Before
        fun setUp() {
            audioManager = Mockito.mock(AudioManager::class.java)

            mockContext = Mockito.mock(Context::class.java)
            Mockito.`when`(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(audioManager)
        }

        @Test
        @Throws(Exception::class)
        fun checkWhenWiredHeadphonesConnected() {
            audioDevices = arrayOf(getAudioDevice(AudioDeviceInfo.TYPE_WIRED_HEADPHONES))
            Mockito.`when`(audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(audioDevices)

            Assert.assertTrue(NotificationSchedulerHelper.isHeadsetOn(audioManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkWhenWiredHeadsetConnected() {
            audioDevices = arrayOf(getAudioDevice(AudioDeviceInfo.TYPE_WIRED_HEADSET))
            Mockito.`when`(audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(audioDevices)

            Assert.assertTrue(NotificationSchedulerHelper.isHeadsetOn(audioManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkWhenBlueToothA2DPConnected() {
            audioDevices = arrayOf(getAudioDevice(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP))
            Mockito.`when`(audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(audioDevices)

            Assert.assertTrue(NotificationSchedulerHelper.isHeadsetOn(audioManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkWhenBlueToothSCOConnected() {
            audioDevices = arrayOf(getAudioDevice(AudioDeviceInfo.TYPE_BLUETOOTH_SCO))
            Mockito.`when`(audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(audioDevices)

            Assert.assertTrue(NotificationSchedulerHelper.isHeadsetOn(audioManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkWhenUnknownDeviceConnected() {
            audioDevices = arrayOf(getAudioDevice(AudioDeviceInfo.TYPE_UNKNOWN))
            Mockito.`when`(audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(audioDevices)

            Assert.assertFalse(NotificationSchedulerHelper.isHeadsetOn(audioManager))
        }

        @Test
        @Throws(Exception::class)
        fun checkWhenDockConnected() {
            audioDevices = arrayOf(getAudioDevice(AudioDeviceInfo.TYPE_DOCK))
            Mockito.`when`(audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(audioDevices)

            Assert.assertFalse(NotificationSchedulerHelper.isHeadsetOn(audioManager))
        }
    }

    @RunWith(RobolectricTestRunner::class)
    @Config(sdk = [26], manifest = Config.NONE)
    class IsNotificationJobScheduledTest {

        @Rule
        @JvmField
        var jobManagerRule: JobManagerRule = JobManagerRule(CoreJobCreator(),
                CoreTestUtils.createMockContext().applicationContext)

        @Test
        @Throws(Exception::class)
        fun checkIsAnyJobSchedulePositive() {
            jobManagerRule.jobManager.schedule(JobRequest.Builder(NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG)
                    .setPeriodic(1800_000L)
                    .build())
            Assert.assertTrue(NotificationSchedulerHelper.isReminderScheduled())
        }

        @Test
        @Throws(Exception::class)
        fun checkIsAnyJobScheduleNegative() {
            JobManager.create(CoreTestUtils.createMockContext())

            val jobManager = JobManager.instance()
            jobManager.cancelAll()
            Assert.assertFalse(NotificationSchedulerHelper.isReminderScheduled())
        }
    }
}
