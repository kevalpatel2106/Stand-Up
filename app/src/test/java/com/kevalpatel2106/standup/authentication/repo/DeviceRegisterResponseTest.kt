package com.kevalpatel2106.standup.authentication.repo

import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

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
    fun checkInit() {
        val deviceRegisterRequest = DeviceRegisterRequest(deviceId = "test-device-id", gcmKey = "test-firebase-fcm-key")

        assertEquals(deviceRegisterRequest.deviceId, "test-device-id")
        assertEquals(deviceRegisterRequest.gcmKey, "test-firebase-fcm-key")
    }

    @Test
    fun checkDeviceName() {
        val deviceRegisterRequest = DeviceRegisterRequest(deviceId = "test-device-id", gcmKey = "test-firebase-fcm-key")
        assertEquals(deviceRegisterRequest.deviceName, Utils.getDeviceName())
    }

    @Test
    fun checkUserId() {
        val deviceRegisterRequest = DeviceRegisterRequest(deviceId = "test-device-id", gcmKey = "test-firebase-fcm-key")
        assertEquals(deviceRegisterRequest.userId, TEST_USER_ID)
    }
}