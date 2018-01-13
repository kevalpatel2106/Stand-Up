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

package com.kevalpatel2106.standup.settings.list

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.SharedPrefsProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by Keval on 11/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class SettingsViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rule1 = RxSchedulersOverrideRule()

    private lateinit var sharedPrefProvider: SharedPrefsProvider
    private lateinit var model: SettingsViewModel

    @Before
    fun setUp() {
        sharedPrefProvider = SharedPrefsProvider(MockSharedPreference())
        model = SettingsViewModel(sharedPrefProvider)
    }

    @Test
    @Throws(Exception::class)
    fun checkInit() {
        Assert.assertNull(model.errorMessage.value)
        Assert.assertFalse(model.blockUi.value!!)
    }
}