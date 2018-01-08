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

package com.kevalpatel2106.standup.authentication.verification

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
class VerifyEmailModelTest {
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/kevalpatel2106/standup/authentication/repo", Paths.get("").toAbsolutePath().toString())

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var verifyEmailViewModel: VerifyEmailViewModel
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
        verifyEmailViewModel = VerifyEmailViewModel(UserAuthRepositoryImpl(mockServerManager.getBaseUrl()))
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }


    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)
        Assert.assertNull(verifyEmailViewModel.resendInProgress.value)

        //Normal init
        val model = VerifyEmailViewModel()
        Assert.assertTrue(model.userAuthRepo is UserAuthRepositoryImpl)
    }

    @Test
    @Throws(IOException::class)
    fun checkResendVerificationEmailSuccess() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/resend_verification_email_success.json"))

        Assert.assertNull(verifyEmailViewModel.resendInProgress.value)
        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)

        //Make the api call to the mock server
        verifyEmailViewModel.resendEmail(123)

        //There should be success.
        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)
        Assert.assertFalse(verifyEmailViewModel.resendInProgress.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun checkResendVerificationEmailFieldMissing() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)
        Assert.assertNull(verifyEmailViewModel.resendInProgress.value)

        //Make the api call to the mock server
        verifyEmailViewModel.resendEmail(123)

        //There should be success.
        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)
        Assert.assertFalse(verifyEmailViewModel.resendInProgress.value!!)
        Assert.assertEquals(verifyEmailViewModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }
}