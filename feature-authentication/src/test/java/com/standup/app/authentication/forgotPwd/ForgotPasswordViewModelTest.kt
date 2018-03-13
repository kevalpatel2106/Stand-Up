/*
 *  Copyright 2018 Keval Patel.
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

package com.standup.app.authentication.forgotPwd

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.standup.app.authentication.R
import com.standup.app.authentication.repo.UserAuthRepositoryImpl
import org.junit.*
import org.junit.rules.TestRule
import java.io.File
import java.io.IOException
import java.nio.file.Paths

/**
 * Created by Keval on 26/11/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
class ForgotPasswordViewModelTest {
    private val path = Paths.get("").toAbsolutePath().toString().let {
        return@let if (it.endsWith("feature-authentication")) it else it.plus("/feature-authentication")
    }
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/standup/app/authentication/repo", path)

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    private val mockServerManager = MockServerManager()

    @Before
    fun setUp() {
        //Set the repo
        mockServerManager.startMockWebServer()
        forgotPasswordViewModel = ForgotPasswordViewModel(
                UserAuthRepositoryImpl(NetworkApi().getRetrofitClient(mockServerManager.getBaseUrl()))
        )
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(forgotPasswordViewModel.blockUi.value!!)
        Assert.assertFalse(forgotPasswordViewModel.isRequesting.value!!)
        Assert.assertFalse(forgotPasswordViewModel.isForgotRequestSuccessful.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun checkForgotPasswordInvalidEmail() {
        forgotPasswordViewModel.forgotPasswordRequest("test@example")

        Assert.assertFalse(forgotPasswordViewModel.blockUi.value!!)
        Assert.assertEquals(forgotPasswordViewModel.emailError.value!!.errorMessageRes, R.string.error_login_invalid_email)
    }

    @Test
    @Throws(IOException::class)
    fun checkForgotPasswordSuccess() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/forgot_password_success.json"))

        //Make the api call to the mock server
        forgotPasswordViewModel.forgotPasswordRequest("test@example.com")

        //There should be success.
        Assert.assertFalse(forgotPasswordViewModel.blockUi.value!!)
        Assert.assertFalse(forgotPasswordViewModel.isRequesting.value!!)
        Assert.assertTrue(forgotPasswordViewModel.isForgotRequestSuccessful.value!!)
        Assert.assertNull(forgotPasswordViewModel.errorMessage.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkForgotPasswordFieldMissing() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        //Make the api call to the mock server
        forgotPasswordViewModel.forgotPasswordRequest("test@example.com")

        //There should be success.
        Assert.assertFalse(forgotPasswordViewModel.blockUi.value!!)
        Assert.assertFalse(forgotPasswordViewModel.isRequesting.value!!)
        Assert.assertFalse(forgotPasswordViewModel.isForgotRequestSuccessful.value!!)
        Assert.assertEquals(forgotPasswordViewModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }
}
