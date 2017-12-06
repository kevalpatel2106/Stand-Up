package com.kevalpatel2106.standup.authentication.repo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
class ResendVerificationRequestTest {

    @Test
    @Throws(IOException::class)
    fun checkWithParams() {
        val resendVerificationRequest = ResendVerificationRequest(1234567890)

        assertEquals(resendVerificationRequest.userId, 1234567890)
    }

    @Test
    @Throws(IOException::class)
    fun checkEquals() {
        val resendVerificationRequest = ResendVerificationRequest(1234567890)
        val resendVerificationRequest1 = ResendVerificationRequest(1234567890)
        val resendVerificationRequest2 = ResendVerificationRequest(123456)

        assertEquals(resendVerificationRequest, resendVerificationRequest1)
        assertEquals(resendVerificationRequest, resendVerificationRequest)
        assertNotEquals(resendVerificationRequest, null)
        assertNotEquals(resendVerificationRequest, resendVerificationRequest2)
        assertNotEquals(resendVerificationRequest1, resendVerificationRequest2)
    }
}