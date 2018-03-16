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

package com.standup.app.settings.dnd

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.common.prefs.SharedPreferenceKeys
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import com.standup.core.Core
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 16-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DndSettingsViewModelTest {


    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val rule1 = RxSchedulersOverrideRule()

    private lateinit var model: DndSettingsViewModel
    private lateinit var mockSharedPrefsProvider: SharedPrefsProvider
    private lateinit var mockUserSettingsManager: UserSettingsManager
    private lateinit var mockCore: Core

    @Before
    fun setUp() {
        mockSharedPrefsProvider = SharedPrefsProvider(MockSharedPreference())
        mockUserSettingsManager = Mockito.spy(UserSettingsManager(mockSharedPrefsProvider))

        mockCore = Mockito.mock(Core::class.java)

        model = DndSettingsViewModel(
                userSettingsManager = mockUserSettingsManager,
                core = mockCore
        )
    }

    @Test
    @Throws(Exception::class)
    fun checkInit() {
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE, true)
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_FORCE_DND_ENABLE, true)

        mockUserSettingsManager.setAutoDndTime(TimeUtils.ONE_HOUR_MILLS, TimeUtils.ONE_HOUR_MILLS + 2 * TimeUtils.ONE_MIN_MILLS)
        mockUserSettingsManager.setSleepTime(3 * TimeUtils.ONE_HOUR_MILLS, 7 * TimeUtils.ONE_HOUR_MILLS)

        model = DndSettingsViewModel(
                userSettingsManager = mockUserSettingsManager,
                core = mockCore
        )

        Assert.assertTrue(model.isAutoDndEnable.value!!)
        Assert.assertTrue(model.isDndEnable.value!!)
        Assert.assertEquals("01:00 AM - 01:02 AM", model.autoDndTime.value)
        Assert.assertEquals("03:00 AM - 07:00 AM", model.sleepTime.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkOnForceDndChange() {
        var isCoreRefreshed = false
        Mockito.`when`(mockCore.refresh()).thenAnswer {
            isCoreRefreshed = true
            return@thenAnswer Any()
        }

        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_FORCE_DND_ENABLE, true)
        model.onForceDndChanged()

        Assert.assertTrue(model.isDndEnable.value!!)
        Assert.assertTrue(isCoreRefreshed)
    }

    @Test
    @Throws(Exception::class)
    fun checkOnAutoDndChange_AutoDndOn() {
        var isCoreRefreshed = false
        Mockito.`when`(mockCore.refresh()).thenAnswer {
            isCoreRefreshed = true
            return@thenAnswer Any()
        }

        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE, true)
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_FORCE_DND_ENABLE, true)
        mockUserSettingsManager.setAutoDndTime(TimeUtils.ONE_HOUR_MILLS, TimeUtils.ONE_HOUR_MILLS + 2 * TimeUtils.ONE_MIN_MILLS)

        model.onAutoDndChanged()

        Assert.assertTrue(model.isAutoDndEnable.value!!)
        Assert.assertTrue(model.isDndEnable.value!!)
        Assert.assertEquals("01:00 AM - 01:02 AM", model.autoDndTime.value)
        Assert.assertTrue(isCoreRefreshed)
    }

    @Test
    @Throws(Exception::class)
    fun checkOnAutoDndChange_AutoDndOff() {
        var isCoreRefreshed = false
        Mockito.`when`(mockCore.refresh()).thenAnswer {
            isCoreRefreshed = true
            return@thenAnswer Any()
        }

        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE, false)
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_FORCE_DND_ENABLE, true)
        mockUserSettingsManager.setAutoDndTime(TimeUtils.ONE_HOUR_MILLS, TimeUtils.ONE_HOUR_MILLS + 2 * TimeUtils.ONE_MIN_MILLS)

        model.onAutoDndChanged()

        Assert.assertFalse(model.isAutoDndEnable.value!!)
        Assert.assertTrue(model.isDndEnable.value!!)
        Assert.assertEquals("01:00 AM - 01:02 AM", model.autoDndTime.value)
        Assert.assertTrue(isCoreRefreshed)
    }

    @Test
    @Throws(Exception::class)
    fun checkOnSleepTimeChange() {
        var isCoreRefreshed = false
        Mockito.`when`(mockCore.refresh()).thenAnswer {
            isCoreRefreshed = true
            return@thenAnswer Any()
        }

        mockUserSettingsManager.setSleepTime(3 * TimeUtils.ONE_HOUR_MILLS, 7 * TimeUtils.ONE_HOUR_MILLS)

        model.onSleepTimeChanged()

        Assert.assertEquals("03:00 AM - 07:00 AM", model.sleepTime.value)
        Assert.assertTrue(isCoreRefreshed)
    }

    @Test
    @Throws(Exception::class)
    fun checkAutoDndTimeListener() {
        var isCoreRefreshed = false
        Mockito.`when`(mockCore.refresh()).thenAnswer {
            isCoreRefreshed = true
            return@thenAnswer Any()
        }

        model.autoDndTimePickerListener.onTimeSelected(2, 2, 5, 5)

        Assert.assertEquals("02:02 AM - 05:05 AM", model.autoDndTime.value)
        Assert.assertEquals(2 * TimeUtils.ONE_HOUR_MILLS + 2 * TimeUtils.ONE_MIN_MILLS, mockUserSettingsManager.autoDndStartTime)
        Assert.assertEquals(5 * TimeUtils.ONE_HOUR_MILLS + 5 * TimeUtils.ONE_MIN_MILLS, mockUserSettingsManager.autoDndEndTime)
        Assert.assertTrue(isCoreRefreshed)
    }

    @Test
    @Throws(Exception::class)
    fun checkSleepTimeListener() {
        var isCoreRefreshed = false
        Mockito.`when`(mockCore.refresh()).thenAnswer {
            isCoreRefreshed = true
            return@thenAnswer Any()
        }

        model.sleepTimePickerListener.onTimeSelected(2, 2, 5, 5)

        Assert.assertEquals("02:02 AM - 05:05 AM", model.sleepTime.value)
        Assert.assertEquals(2 * TimeUtils.ONE_HOUR_MILLS + 2 * TimeUtils.ONE_MIN_MILLS, mockUserSettingsManager.sleepStartTime)
        Assert.assertEquals(5 * TimeUtils.ONE_HOUR_MILLS + 5 * TimeUtils.ONE_MIN_MILLS, mockUserSettingsManager.sleepEndTime)
        Assert.assertTrue(isCoreRefreshed)
    }
}
