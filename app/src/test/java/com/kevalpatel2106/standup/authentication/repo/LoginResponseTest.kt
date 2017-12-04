package com.kevalpatel2106.standup.authentication.repo

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 04-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class LoginResponseTest {


    @Test
    fun checkInitWithDefaultParams() {
        val loginResponse = LoginResponse(uid = 0, name = "Test User", email = "test@example.com")

        Assert.assertEquals(loginResponse.uid, 0)
        Assert.assertEquals(loginResponse.name, "Test User")
        Assert.assertEquals(loginResponse.email, "test@example.com")
        Assert.assertFalse(loginResponse.isVerified)
        Assert.assertNull(loginResponse.photoUrl)
    }

    @Test
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
}