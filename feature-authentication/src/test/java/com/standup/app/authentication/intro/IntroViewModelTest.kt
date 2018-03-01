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

package com.standup.app.authentication.intro

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthUser
import com.kevalpatel2106.network.NetworkModule
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.app.authentication.R
import com.standup.app.authentication.repo.SignUpRequest
import com.standup.app.authentication.repo.UserAuthRepositoryImpl
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
//TODO check user session manager also
class IntroViewModelTest {
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

    private lateinit var introViewModel: IntroViewModel
    private val userSessionManager = UserSessionManager(SharedPrefsProvider(MockSharedPreference()))
    private val mockServerManager = MockServerManager()

    @Before
    fun setUp() {
        //Set the repo
        mockServerManager.startMockWebServer()
        introViewModel = IntroViewModel(
                UserAuthRepositoryImpl(NetworkModule().getRetrofitClient(mockServerManager.getBaseUrl())),
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
        Assert.assertFalse(introViewModel.blockUi.value!!)
        Assert.assertFalse(introViewModel.isGoogleLoginProgress.value!!)
        Assert.assertFalse(introViewModel.isFacebookLoginProgress.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateSocialUserSignUpSuccess() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/social_user_sign_up_success.json"))

        //Make the api call to the mock server
        val signInRequest = SignUpRequest("test@example.com", "Test User", null, null)
        introViewModel.authenticateSocialUser(signInRequest)

        //There should be success.
        Assert.assertFalse(introViewModel.blockUi.value!!)
        Assert.assertNull(introViewModel.errorMessage.value)
        Assert.assertTrue(introViewModel.introUiModel.value!!.isSuccess)
        Assert.assertTrue(introViewModel.introUiModel.value!!.isNewUser)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateSocialUserLoginSuccess() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/social_user_login_success.json"))

        //Make the api call to the mock server
        val signInRequest = SignUpRequest("test@example.com", "Test User", null, null)
        introViewModel.authenticateSocialUser(signInRequest)

        //There should be success.
        Assert.assertFalse(introViewModel.blockUi.value!!)
        Assert.assertNull(introViewModel.errorMessage.value)
        Assert.assertTrue(introViewModel.introUiModel.value!!.isSuccess)
        Assert.assertFalse(introViewModel.introUiModel.value!!.isNewUser)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateSocialUserFieldMissing() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        //Make the api call to the mock server
        introViewModel.authenticateSocialUser(SignUpRequest("test@example.com", "Test User", null, null))

        //There should be success.
        Thread.sleep(2000)
        Assert.assertFalse(introViewModel.blockUi.value!!)
        Assert.assertFalse(introViewModel.introUiModel.value!!.isSuccess)
        Assert.assertEquals(introViewModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateGoogleUserWithoutEmail() {
        val googleAuthUser = GoogleAuthUser("46753782367943")
        googleAuthUser.email = ""
        googleAuthUser.name = ""
        introViewModel.authenticateSocialUser(googleAuthUser)

        //There should be success.
        Assert.assertEquals(introViewModel.errorMessage.value!!.errorMessageRes, R.string.error_google_login_email_not_found)
        Assert.assertFalse(introViewModel.blockUi.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateFbUserWithoutEmail() {
        val fbUser = FacebookUser("46753782367943")
        introViewModel.authenticateSocialUser(fbUser)

        //There should be success.
        Assert.assertEquals(introViewModel.errorMessage.value!!.errorMessageRes, R.string.error_fb_login_email_not_found)
        Assert.assertFalse(introViewModel.blockUi.value!!)
    }
}
