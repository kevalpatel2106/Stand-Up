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

package com.standup.app.settings.dailyReview

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.view.ViewGroup
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
 * Created by Kevalpatel2106 on 15-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DailyReviewSettingsViewModelTest {

    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val rule1 = RxSchedulersOverrideRule()

    private lateinit var model: DailyReviewSettingsViewModel
    private lateinit var mockSharedPrefsProvider: SharedPrefsProvider
    private lateinit var mockSettingsProvider: UserSettingsManager
    private lateinit var mockCore: Core

    @Before
    fun setUp() {
        mockSharedPrefsProvider = SharedPrefsProvider(MockSharedPreference())
        mockSettingsProvider = UserSettingsManager(mockSharedPrefsProvider)

        mockCore = Mockito.mock(Core::class.java)

        model = DailyReviewSettingsViewModel(
                settingsManager = mockSettingsProvider,
                core = mockCore
        )
    }

    @Test
    @Throws(Exception::class)
    fun checkInit_DailyReviewEnable() {
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_ENABLE, true)
        mockSettingsProvider.dailyReviewTimeFrom12Am = 8 * TimeUtils.ONE_HOUR_MILLS

        model = DailyReviewSettingsViewModel(
                settingsManager = mockSettingsProvider,
                core = mockCore
        )

        Assert.assertTrue(model.isDailyReviewEnable.value!!)
        Assert.assertNotNull(model.dailyReviewTimeSummary.value)
        Assert.assertEquals(model.dailyReviewTimeSummary.value, "08:00 AM")
    }


    @Test
    @Throws(Exception::class)
    fun checkInit_DailyReviewDisable() {
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_ENABLE, false)
        mockSettingsProvider.dailyReviewTimeFrom12Am = 8 * TimeUtils.ONE_HOUR_MILLS

        model = DailyReviewSettingsViewModel(
                settingsManager = mockSettingsProvider,
                core = mockCore
        )

        Assert.assertFalse(model.isDailyReviewEnable.value!!)
        Assert.assertNotNull(model.dailyReviewTimeSummary.value)
        Assert.assertEquals(model.dailyReviewTimeSummary.value, "08:00 AM")
    }

    @Test
    @Throws(Exception::class)
    fun checkOnTimeChangeListener() {
        var isCoreRefreshed = false
        Mockito.`when`(mockCore.refresh()).thenAnswer {
            isCoreRefreshed = true
            return@thenAnswer Any()
        }

        model.onTimeSetListener.onTimeSet(Mockito.mock(ViewGroup::class.java), 2, 23)

        //Check the time
        Assert.assertEquals(
                mockSettingsProvider.dailyReviewTimeFrom12Am,
                2 * TimeUtils.ONE_HOUR_MILLS + 23 * TimeUtils.ONE_MIN_MILLS
        )

        Assert.assertNotNull(model.dailyReviewTimeSummary.value)
        Assert.assertEquals(model.dailyReviewTimeSummary.value, "02:23 AM")

        //Check if the core refreshed
        Assert.assertTrue(isCoreRefreshed)
    }

    @Test
    @Throws(Exception::class)
    fun checkOnDailyReviewSettingChange_DailyReviewDisable() {
        var isCoreRefreshed = false
        Mockito.`when`(mockCore.refresh()).thenAnswer {
            isCoreRefreshed = true
            return@thenAnswer Any()
        }
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_ENABLE, false)
        mockSettingsProvider.dailyReviewTimeFrom12Am = 8 * TimeUtils.ONE_HOUR_MILLS

        model.onDailyReviewSettingChange()

        Assert.assertFalse(model.isDailyReviewEnable.value!!)
        Assert.assertNotNull(model.dailyReviewTimeSummary.value)
        Assert.assertEquals(model.dailyReviewTimeSummary.value, "08:00 AM")

        //Check if the core refreshed
        Assert.assertTrue(isCoreRefreshed)
    }

    @Test
    @Throws(Exception::class)
    fun checkOnDailyReviewSettingChange_DailyReviewEnable() {
        var isCoreRefreshed = false
        Mockito.`when`(mockCore.refresh()).thenAnswer {
            isCoreRefreshed = true
            return@thenAnswer Any()
        }
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_ENABLE, true)
        mockSettingsProvider.dailyReviewTimeFrom12Am = 8 * TimeUtils.ONE_HOUR_MILLS

        model.onDailyReviewSettingChange()

        Assert.assertTrue(model.isDailyReviewEnable.value!!)
        Assert.assertNotNull(model.dailyReviewTimeSummary.value)
        Assert.assertEquals(model.dailyReviewTimeSummary.value, "08:00 AM")

        //Check if the core refreshed
        Assert.assertTrue(isCoreRefreshed)
    }
}
