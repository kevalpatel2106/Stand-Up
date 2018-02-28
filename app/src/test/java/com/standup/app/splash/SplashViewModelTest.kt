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

package com.standup.app.splash

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.common.prefs.SharedPreferenceKeys
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.utils.SharedPrefsProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 28-Feb-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SplashViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var userSessionManager: UserSessionManager
    private lateinit var sharedPrefProvider: SharedPrefsProvider
    private lateinit var model: SplashViewModel

    @Before
    fun setUp() {
        sharedPrefProvider = SharedPrefsProvider(MockSharedPreference())
        //Register device
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, true)

        userSessionManager = UserSessionManager(sharedPrefProvider)
        //Set new user session
        userSessionManager.setNewSession(
                1234567,
                "Test",
                "test@gmail.com",
                "test_token",
                null,
                true
        )

        model = SplashViewModel(userSessionManager, sharedPrefProvider)
    }

    @Test
    @Throws(Exception::class)
    fun checkInit() {
        Assert.assertFalse(model.openIntro.value!!)
        Assert.assertFalse(model.openDeviceRegister.value!!)
        Assert.assertFalse(model.openVerifyEmail.value!!)
        Assert.assertFalse(model.openProfile.value!!)
        Assert.assertFalse(model.openDashboard.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkInitiateFlow_UserNotLoggedIn() {
        //Logout user
        userSessionManager.clearToken()

        model.initiateFlow()

        Assert.assertTrue(model.openIntro.value!!)
        Assert.assertFalse(model.openDeviceRegister.value!!)
        Assert.assertFalse(model.openVerifyEmail.value!!)
        Assert.assertFalse(model.openProfile.value!!)
        Assert.assertFalse(model.openDashboard.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkInitiateFlow_DeviceNotRegister() {
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, false)

        model.initiateFlow()

        Assert.assertFalse(model.openIntro.value!!)
        Assert.assertTrue(model.openDeviceRegister.value!!)
        Assert.assertFalse(model.openVerifyEmail.value!!)
        Assert.assertFalse(model.openProfile.value!!)
        Assert.assertFalse(model.openDashboard.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkInitiateFlow_UserNotVerified() {
        userSessionManager.isUserVerified = false

        model.initiateFlow()

        Assert.assertFalse(model.openIntro.value!!)
        Assert.assertFalse(model.openDeviceRegister.value!!)
        Assert.assertTrue(model.openVerifyEmail.value!!)
        Assert.assertFalse(model.openProfile.value!!)
        Assert.assertFalse(model.openDashboard.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkInitiateFlow_NoDisplayName() {
        userSessionManager.updateSession("")

        model.initiateFlow()

        Assert.assertFalse(model.openIntro.value!!)
        Assert.assertFalse(model.openDeviceRegister.value!!)
        Assert.assertFalse(model.openVerifyEmail.value!!)
        Assert.assertTrue(model.openProfile.value!!)
        Assert.assertFalse(model.openDashboard.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkInitiateFlow_OpenDashboard() {
        model.initiateFlow()

        Assert.assertFalse(model.openIntro.value!!)
        Assert.assertFalse(model.openDeviceRegister.value!!)
        Assert.assertFalse(model.openVerifyEmail.value!!)
        Assert.assertFalse(model.openProfile.value!!)
        Assert.assertTrue(model.openDashboard.value!!)
    }
}
