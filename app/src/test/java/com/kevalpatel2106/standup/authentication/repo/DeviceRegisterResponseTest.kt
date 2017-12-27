/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.authentication.repo

import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 04-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DeviceRegisterResponseTest {
    private val TEST_USER_ID = 123456L

    @Before
    fun setUp() {
        val context = Mockito.mock(Context::class.java)

        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPrefs)
        Mockito.`when`(sharedPrefs.getLong(anyString(), anyLong())).thenReturn(TEST_USER_ID)

        SharedPrefsProvider.init(context)
    }

    @Test
    @Throws(IOException::class)
    fun checkInit() {
        val deviceRegisterRequest = DeviceRegisterRequest(deviceId = "test-device-id", gcmKey = "test-firebase-fcm-key")

        assertEquals(deviceRegisterRequest.deviceId, "test-device-id")
        assertEquals(deviceRegisterRequest.gcmKey, "test-firebase-fcm-key")
    }

    @Test
    @Throws(IOException::class)
    fun checkDeviceName() {
        val deviceRegisterRequest = DeviceRegisterRequest(deviceId = "test-device-id", gcmKey = "test-firebase-fcm-key")
        assertEquals(deviceRegisterRequest.deviceName, Utils.getDeviceName())
    }

    @Test
    @Throws(IOException::class)
    fun checkUserId() {
        val deviceRegisterRequest = DeviceRegisterRequest(deviceId = "test-device-id", gcmKey = "test-firebase-fcm-key")
        assertEquals(deviceRegisterRequest.userId, TEST_USER_ID)
    }

    @Test
    @Throws(IOException::class)
    fun checkEquals() {
        val deviceRegisterRequest = DeviceRegisterRequest(deviceId = "test-device-id", gcmKey = "test-firebase-fcm-key")
        val deviceRegisterRequest1 = DeviceRegisterRequest(deviceId = "test-device-id", gcmKey = "test-firebase-fcm-key")
        val deviceRegisterRequest2 = DeviceRegisterRequest(deviceId = "test-device-id-1", gcmKey = "test-firebase-fcm-key")

        assertEquals(deviceRegisterRequest, deviceRegisterRequest1)
        assertNotEquals(deviceRegisterRequest, deviceRegisterRequest2)
        assertNotEquals(deviceRegisterRequest1, deviceRegisterRequest2)
        assertEquals(deviceRegisterRequest1, deviceRegisterRequest1)
        assertNotEquals(deviceRegisterRequest, null)
    }
}