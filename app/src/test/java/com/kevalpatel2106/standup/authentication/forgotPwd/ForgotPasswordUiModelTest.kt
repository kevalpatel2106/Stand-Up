package com.kevalpatel2106.standup.authentication.forgotPwd

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Keval on 26/11/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
@RunWith(JUnit4::class)
class ForgotPasswordUiModelTest {

    @Test
    @Throws(IOException::class)
    fun checkIsSuccess() {
        val verifyEmailUiModel = ForgotPasswordUiModel(true)
        assertTrue(verifyEmailUiModel.isSuccess)

        verifyEmailUiModel.isSuccess = false
        assertFalse(verifyEmailUiModel.isSuccess)
    }
}