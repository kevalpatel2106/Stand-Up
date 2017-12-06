package com.kevalpatel2106.standup.authentication.repo

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 04-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DeviceRegisterRequestTest {

    @Test
    @Throws(IOException::class)
    fun checkInitWithDefaultParams() {
        val deviceRegisterData = DeviceRegisterResponse(userId = 29384)

        assertEquals(deviceRegisterData.userId, 29384)
        assertNull(deviceRegisterData.token)
    }

    @Test
    @Throws(IOException::class)
    fun checkInitWithParams() {
        val deviceRegisterData = DeviceRegisterResponse(userId = 29384, token = "testtokenfromthefirebase")

        assertEquals(deviceRegisterData.userId, 29384)
        assertEquals(deviceRegisterData.token, "testtokenfromthefirebase")
    }

    @Test
    @Throws(IOException::class)
    fun checkEquals() {
        val deviceRegisterData = DeviceRegisterResponse(userId = 29384, token = "testtokenfromthefirebase")
        val deviceRegisterData1 = DeviceRegisterResponse(userId = 29384, token = "testtokenfromthefirebase")
        val deviceRegisterData2 = DeviceRegisterResponse(userId = 123456789, token = "testtokenfromthefirebase")

        assertEquals(deviceRegisterData, deviceRegisterData1)
        assertNotEquals(deviceRegisterData, deviceRegisterData2)
        assertNotEquals(deviceRegisterData1, deviceRegisterData2)
        assertEquals(deviceRegisterData1, deviceRegisterData1)
        assertNotEquals(deviceRegisterData, null)
    }
}