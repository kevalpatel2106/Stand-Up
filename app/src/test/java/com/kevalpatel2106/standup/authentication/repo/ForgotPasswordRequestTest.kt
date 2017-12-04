package com.kevalpatel2106.standup.authentication.repo

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 04-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ForgotPasswordRequestTest {

    @Test
    fun checkInit() {
        val forgotPasswordRequest = ForgotPasswordRequest(email = "test@example.com")
        assertEquals(forgotPasswordRequest.email, "test@example.com")
    }
}