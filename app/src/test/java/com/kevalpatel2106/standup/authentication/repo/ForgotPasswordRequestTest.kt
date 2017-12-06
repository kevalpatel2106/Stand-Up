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
class ForgotPasswordRequestTest {

    @Test
    @Throws(IOException::class)
    fun checkInit() {
        val forgotPasswordRequest = ForgotPasswordRequest(email = "test@example.com")
        assertEquals(forgotPasswordRequest.email, "test@example.com")
    }

    @Test
    @Throws(IOException::class)
    fun checkEquals() {
        val forgotPasswordRequest = ForgotPasswordRequest(email = "test@example.com")
        val forgotPasswordRequest1 = ForgotPasswordRequest(email = "test@example.com")
        val forgotPasswordRequest2 = ForgotPasswordRequest(email = "test1@example.com")

        assertEquals(forgotPasswordRequest, forgotPasswordRequest1)
        Assert.assertNotEquals(forgotPasswordRequest, forgotPasswordRequest2)
        Assert.assertNotEquals(forgotPasswordRequest1, forgotPasswordRequest2)
        Assert.assertEquals(forgotPasswordRequest, forgotPasswordRequest)
        Assert.assertNotEquals(forgotPasswordRequest, null)
    }
}