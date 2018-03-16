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

package com.standup.app.settings.notifications

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.common.prefs.SharedPreferenceKeys
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.SharedPrefsProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 16-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class NotificationsSettingsViewModelTest {

    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val rule1 = RxSchedulersOverrideRule()

    private lateinit var model: NotificationsSettingsViewModel
    private lateinit var mockSharedPrefsProvider: SharedPrefsProvider
    private lateinit var mockSettingsProvider: UserSettingsManager

    @Before
    fun setUp() {
        mockSharedPrefsProvider = SharedPrefsProvider(MockSharedPreference())
        mockSettingsProvider = UserSettingsManager(mockSharedPrefsProvider)

        model = NotificationsSettingsViewModel(
                sharedPrefsProvider = mockSharedPrefsProvider,
                userSettingsManager = mockSettingsProvider
        )
    }

    @Test
    @Throws(Exception::class)
    fun checkInit() {
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_RINGTONE_NAME, "test-uri")

        model = NotificationsSettingsViewModel(
                sharedPrefsProvider = mockSharedPrefsProvider,
                userSettingsManager = mockSettingsProvider
        )

        Assert.assertNotNull(model.reminderToneName.value)
        Assert.assertEquals("test-uri", model.reminderToneName.value)
    }
}
