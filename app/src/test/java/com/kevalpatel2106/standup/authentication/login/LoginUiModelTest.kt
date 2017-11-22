package com.kevalpatel2106.standup.authentication.login

import com.kevalpatel2106.standup.authentication.intro.IntroUiModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class LoginUiModelTest {

    @Test
    @Throws(IOException::class)
    fun checkIsSuccess() {
        val loginUiModel = LoginUiModel(true)
        assertTrue(loginUiModel.isSuccess)

        loginUiModel.isSuccess = false
        assertFalse(loginUiModel.isSuccess)
    }

    @Test
    @Throws(IOException::class)
    fun checkErrorMsg() {
        val loginUiModel = IntroUiModel(true)
        assertNull(loginUiModel.errorMsg)

        loginUiModel.errorMsg = "This is the test."
        assertNull(loginUiModel.errorMsg)

        loginUiModel.isSuccess = false
        assertEquals(loginUiModel.errorMsg, "This is the test.")
    }

    @Test
    @Throws(IOException::class)
    fun checkIsNewUser() {
        val loginUiModel = IntroUiModel(true)
        assertFalse(loginUiModel.isNewUser)

        loginUiModel.isNewUser = true
        assertTrue(loginUiModel.isNewUser)

        loginUiModel.isSuccess = false
        assertFalse(loginUiModel.isNewUser)
    }
}