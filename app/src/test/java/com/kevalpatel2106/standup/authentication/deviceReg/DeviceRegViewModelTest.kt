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

package com.kevalpatel2106.standup.authentication.deviceReg

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.io.File
import java.io.IOException
import java.nio.file.Paths


/**
 * Created by Kevalpatel2106 on 07-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DeviceRegViewModelTest {
    private val RESPONSE_DIR_PATH = String.format("%s/app/src/test/java/com/kevalpatel2106/standup/authentication/repo", Paths.get("").toAbsolutePath().toString())

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var deviceRegViewModel: DeviceRegViewModel
    private lateinit var apiProvider: ApiProvider
    private lateinit var userSessionManager: UserSessionManager
    private val mockServerManager = MockServerManager()

    @Before
    fun setUp() {
        val context = Mockito.mock(Context::class.java)
        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        val sharedPrefsEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPrefs)
        Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn("149.3")

        //Init server
        apiProvider = ApiProvider()
        userSessionManager = UserSessionManager(SharedPrefsProvider(sharedPrefs))
        mockServerManager.startMockWebServer()
        deviceRegViewModel = DeviceRegViewModel(UserAuthRepositoryImpl(mockServerManager.getBaseUrl()))
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(deviceRegViewModel.blockUi.value!!)
        Assert.assertNull(deviceRegViewModel.errorMessage.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkInvalidDeviceId() {
        deviceRegViewModel.register("", "test-firebase-id")

        Assert.assertEquals(deviceRegViewModel.errorMessage.value!!.errorRes,
                R.string.device_reg_error_invalid_device_id)
    }

    @Test
    @Throws(IOException::class)
    fun checkInvalidFcmId() {
        deviceRegViewModel.register("test-device-id", null)

        Assert.assertEquals(deviceRegViewModel.errorMessage.value!!.errorRes, R.string.device_reg_error_invalid_fcm_id)
    }

    @Test
    @Throws(IOException::class)
    fun checkInvalidDeviceRegSuccess() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/device_reg_success.json"))

        deviceRegViewModel.register("test-device-id", "test-firebase-id")

        Assert.assertNull(deviceRegViewModel.errorMessage.value)
        Assert.assertEquals(deviceRegViewModel.reposeToken.value, "64df48e6-45de-4bb5-879d-4c1a722f23fd")
    }

    @Test
    @Throws(IOException::class)
    fun checkInvalidDeviceRegFieldMissing() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        deviceRegViewModel.register("test-device-id", "test-firebase-id")

        Assert.assertEquals(deviceRegViewModel.errorMessage.value!!.getMessage(null), "Required field missing.")
        Assert.assertNull(deviceRegViewModel.reposeToken.value)
    }
}
