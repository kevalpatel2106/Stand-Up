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

package com.standup.app.authentication.verification

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.common.misc.lottie.LottieJson
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.app.authentication.repo.UserAuthRepositoryImpl
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 28-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class EmailLinkVerifyViewModelTest {
    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var emailLinkVerifyViewModel: EmailLinkVerifyViewModel
    private val userSessionManager = UserSessionManager(SharedPrefsProvider(MockSharedPreference()))
    private val mockServerManager = MockServerManager()

    @Before
    fun setUp() {
        //Set the repo
        mockServerManager.startMockWebServer()
        emailLinkVerifyViewModel = EmailLinkVerifyViewModel(
                UserAuthRepositoryImpl(NetworkApi().getRetrofitClient(mockServerManager.getBaseUrl())),
                userSessionManager
        )
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(emailLinkVerifyViewModel.blockUi.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun testVerifyEmailSuccess() {
        mockServerManager.enqueueResponse(File(mockServerManager.getResponsesPath() + "/email_verify_success.html"), "text/html")

        emailLinkVerifyViewModel.verifyEmail(mockServerManager.getBaseUrl())

        Assert.assertFalse(emailLinkVerifyViewModel.blockUi.value!!)
        Assert.assertTrue(userSessionManager.isUserVerified)
        Assert.assertNull(emailLinkVerifyViewModel.errorMessage.value)
    }

    @Test
    @Throws(IOException::class)
    fun testVerifyEmailError() {
        mockServerManager.enqueueResponse(File(mockServerManager.getResponsesPath() + "/authentication_field_missing.json"))

        emailLinkVerifyViewModel.verifyEmail(mockServerManager.getBaseUrl())

        Thread.sleep(1000)
        Assert.assertFalse(emailLinkVerifyViewModel.blockUi.value!!)
        Assert.assertFalse(userSessionManager.isUserVerified)
        Assert.assertEquals(emailLinkVerifyViewModel.errorMessage.value!!.errorImage, LottieJson.WARNING)
    }
}
