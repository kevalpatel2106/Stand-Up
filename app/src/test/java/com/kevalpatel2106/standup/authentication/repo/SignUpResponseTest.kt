package com.kevalpatel2106.standup.authentication.repo

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 04-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SignUpResponseTest {

    @Test
    fun checkInitWithDefaultParams() {
        val signUpResponseData = SignUpResponse(uid = 0, name = "Test User", email = "test@example.com")

        assertEquals(signUpResponseData.uid, 0)
        assertEquals(signUpResponseData.name, "Test User")
        assertEquals(signUpResponseData.email, "test@example.com")
        assertFalse(signUpResponseData.isNewUser)
        assertFalse(signUpResponseData.isVerified)
        assertNull(signUpResponseData.photoUrl)
    }

    @Test
    fun checkInitWithParams() {
        val signUpResponseData = SignUpResponse(uid = 0,
                isVerified = true,
                isNewUser = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")

        assertEquals(signUpResponseData.uid, 0)
        assertEquals(signUpResponseData.name, "Test User")
        assertEquals(signUpResponseData.email, "test@example.com")
        assertEquals(signUpResponseData.photoUrl, "http://google.com")
        assertTrue(signUpResponseData.isNewUser)
        assertTrue(signUpResponseData.isVerified)
    }
}