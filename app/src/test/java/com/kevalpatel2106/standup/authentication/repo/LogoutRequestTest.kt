package com.kevalpatel2106.standup.authentication.repo

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 06-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class LogoutRequestTest {


    @Test
    @Throws(IOException::class)
    fun checkInit() {
        val loginRequest = LogoutRequest(uid = 123456789, deviceId = "64df48e6-45de-4bb5-879d-4c1a722f23fd")
        assertEquals(loginRequest.uid, 123456789)
        assertEquals(loginRequest.deviceId, "64df48e6-45de-4bb5-879d-4c1a722f23fd")
    }


    @Test
    @Throws(IOException::class)
    fun checkEquals() {
        val logoutRequest = LogoutRequest(uid = 123456789, deviceId = "64df48e6-45de-4bb5-879d-4c1a722f23fd")
        val logoutRequest1 = LogoutRequest(uid = 123456789, deviceId = "64df48e6-45de-4bb5-879d-4c1a722f23fd")
        val logoutRequest2 = LogoutRequest(uid = 1234567, deviceId = "64df48e6-45de-4bb5-879d-4c1a722f23fd")

        assertEquals(logoutRequest, logoutRequest1)
        Assert.assertNotEquals(logoutRequest, logoutRequest2)
        Assert.assertNotEquals(logoutRequest1, logoutRequest2)
        Assert.assertEquals(logoutRequest, logoutRequest)
        Assert.assertNotEquals(logoutRequest, null)
    }

}