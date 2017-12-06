package com.kevalpatel2106.standup.authentication.repo

import org.junit.Assert
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
class LoginResponseTest {


    @Test
    @Throws(IOException::class)
    fun checkInitWithDefaultParams() {
        val loginResponse = LoginResponse(uid = 0, name = "Test User", email = "test@example.com")

        Assert.assertEquals(loginResponse.uid, 0)
        Assert.assertEquals(loginResponse.name, "Test User")
        Assert.assertEquals(loginResponse.email, "test@example.com")
        Assert.assertFalse(loginResponse.isVerified)
        Assert.assertNull(loginResponse.photoUrl)
    }

    @Test
    @Throws(IOException::class)
    fun checkInitWithParams() {
        val loginResponse = LoginResponse(uid = 0,
                isVerified = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")

        Assert.assertEquals(loginResponse.uid, 0)
        Assert.assertEquals(loginResponse.name, "Test User")
        Assert.assertEquals(loginResponse.email, "test@example.com")
        Assert.assertEquals(loginResponse.photoUrl, "http://google.com")
        Assert.assertTrue(loginResponse.isVerified)
    }

    @Test
    @Throws(IOException::class)
    fun checkEquals() {
        val loginResponse = LoginResponse(uid = 0,
                isVerified = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")
        val loginResponse1 = LoginResponse(uid = 0,
                isVerified = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")
        val loginResponse2 = LoginResponse(uid = 123456789,
                isVerified = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")

        Assert.assertEquals(loginResponse, loginResponse1)
        Assert.assertNotEquals(loginResponse, loginResponse2)
        Assert.assertNotEquals(loginResponse1, loginResponse2)
        Assert.assertEquals(loginResponse, loginResponse)
        Assert.assertNotEquals(loginResponse, null)
    }

}