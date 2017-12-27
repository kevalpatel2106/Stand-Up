/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.kevalpatel2106.standup.authentication.login

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.standup.UnitTestUtils
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException

import java.nio.file.Paths

/**
 * Created by Keval on 20/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class LoginViewModelLoginTest {
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/kevalpatel2106/standup/authentication/repo", Paths.get("").toAbsolutePath().toString())

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var loginViewModel: LoginViewModel
    private val mockServerManager = MockServerManager()

    companion object {

        @JvmStatic
        @BeforeClass
        fun setGlobal() = UnitTestUtils.initApp()
    }

    @Before
    fun setUp() {
        //Set the repo
        mockServerManager.startMockWebServer()
        loginViewModel = LoginViewModel(UserAuthRepositoryImpl(mockServerManager.getBaseUrl()))
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(loginViewModel.blockUi.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun checkEmailValidation() {
        loginViewModel.performSignIn("test@example", "1234567989")

        Assert.assertFalse(loginViewModel.blockUi.value!!)
        Assert.assertEquals(loginViewModel.mEmailError.value!!.errorRes, com.kevalpatel2106.standup.R.string.error_login_invalid_email)
        Assert.assertNull(loginViewModel.mPasswordError.value)
        Assert.assertNull(loginViewModel.mNameError.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkPasswordValidation() {
        loginViewModel.performSignIn("test@example.com", "1234")

        Assert.assertFalse(loginViewModel.blockUi.value!!)
        Assert.assertNull(loginViewModel.mEmailError.value)
        Assert.assertEquals(loginViewModel.mPasswordError.value!!.errorRes, com.kevalpatel2106.standup.R.string.error_login_invalid_password)
        Assert.assertNull(loginViewModel.mNameError.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkInvalidEmailAndPassword() {
        loginViewModel.performSignIn("test@example", "1234")

        Assert.assertFalse(loginViewModel.blockUi.value!!)
        Assert.assertEquals(loginViewModel.mEmailError.value!!.errorRes, com.kevalpatel2106.standup.R.string.error_login_invalid_email)
        Assert.assertNull(loginViewModel.mPasswordError.value)
        Assert.assertNull(loginViewModel.mNameError.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateLoginSuccess() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/user_login_success.json"))

        //Make the api call to the mock server
        loginViewModel.performSignIn("test@example.com", "1234567989")

        //There should be success.
        Assert.assertFalse(loginViewModel.blockUi.value!!)
        Assert.assertTrue(loginViewModel.mLoginUiModel.value!!.isSuccess)
        Assert.assertTrue(loginViewModel.mLoginUiModel.value!!.isVerify)
        Assert.assertFalse(loginViewModel.mLoginUiModel.value!!.isNewUser)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateLoginFieldMissing() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        //Make the api call to the mock server
        loginViewModel.performSignIn("test@example.com", "1234567989")

        //There should be success.
        Assert.assertFalse(loginViewModel.blockUi.value!!)
        Assert.assertFalse(loginViewModel.mLoginUiModel.value!!.isSuccess)
        Assert.assertFalse(loginViewModel.mLoginUiModel.value!!.isVerify)
        Assert.assertFalse(loginViewModel.mLoginUiModel.value!!.isVerify)
        Assert.assertEquals(loginViewModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }

}