package com.kevalpatel2106.standup.authentication.repo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 04-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DeviceRegisterRequestTest {

    @Test
    fun checkInitWithDefaultParams() {
        val deviceRegisterData = DeviceRegisterResponse(userId = 29384)

        assertEquals(deviceRegisterData.userId, 29384)
        assertNull(deviceRegisterData.token)
    }

    @Test
    fun checkInitWithParams() {
        val deviceRegisterData = DeviceRegisterResponse(userId = 29384, token = "testtokenfromthefirebase")

        assertEquals(deviceRegisterData.userId, 29384)
        assertEquals(deviceRegisterData.token, "testtokenfromthefirebase")
    }
}