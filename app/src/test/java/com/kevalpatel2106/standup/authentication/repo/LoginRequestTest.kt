package com.kevalpatel2106.standup.authentication.repo

import org.junit.Assert
import org.junit.Assert.assertEquals
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
class LoginRequestTest {

    @Test
    @Throws(IOException::class)
    fun checkInit() {
        val loginRequest = LoginRequest(email = "test@example.com", password = "1234567")
        assertEquals(loginRequest.email, "test@example.com")
        assertEquals(loginRequest.password, "1234567")
    }

    @Test
    @Throws(IOException::class)
    fun checkEquals() {
        val loginRequest = LoginRequest(email = "test@example.com", password = "123456789")
        val loginRequest1 = LoginRequest(email = "test@example.com", password = "123456789")
        val loginRequest2 = LoginRequest(email = "test1@example.com", password = "123456789")

        assertEquals(loginRequest, loginRequest1)
        Assert.assertNotEquals(loginRequest, loginRequest2)
        Assert.assertNotEquals(loginRequest1, loginRequest2)
        Assert.assertEquals(loginRequest, loginRequest)
        Assert.assertNotEquals(loginRequest, null)
    }
}