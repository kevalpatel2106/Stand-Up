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
import android.content.SharedPreferences
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.app.authentication.repo.UserAuthRepositoryImpl
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import java.io.File
import java.io.IOException

/**
 * Created by Keval on 20/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class VerifyEmailModelTest {
    private val TEST_USER_ID = 12L

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var verifyEmailViewModel: VerifyEmailViewModel
    private val mockServerManager = MockServerManager()

    @Before
    fun setUp() {
        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(sharedPrefs.getLong(anyString(), anyLong())).thenReturn(TEST_USER_ID)

        val sharedPrefsEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)

        //Set the repo
        mockServerManager.startMockWebServer()
        verifyEmailViewModel = VerifyEmailViewModel(
                UserAuthRepositoryImpl(NetworkApi().getRetrofitClient(mockServerManager.getBaseUrl())),
                UserSessionManager(SharedPrefsProvider(sharedPrefs))
        )
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
    }

    @Test
    @Throws(IOException::class)
    fun checkResendVerificationEmailSuccess() {
        mockServerManager.enqueueResponse(File(mockServerManager.getResponsesPath() + "/resend_verification_email_success.json"))

        Assert.assertNull(verifyEmailViewModel.resendInProgress.value)
        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)

        //Make the api call to the mock server
        verifyEmailViewModel.resendEmail()

        //There should be success.
        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)
        Assert.assertFalse(verifyEmailViewModel.resendInProgress.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun checkResendVerificationEmailFieldMissing() {
        mockServerManager.enqueueResponse(File(mockServerManager.getResponsesPath() + "/authentication_field_missing.json"))

        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)
        Assert.assertNull(verifyEmailViewModel.resendInProgress.value)

        //Make the api call to the mock server
        verifyEmailViewModel.resendEmail()

        //There should be success.
        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)
        Assert.assertFalse(verifyEmailViewModel.resendInProgress.value!!)
        Assert.assertEquals(verifyEmailViewModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }
}
