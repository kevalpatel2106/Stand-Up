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

package com.standup.app.deeplink

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.app.R
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 28-Feb-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DeepLinkViewModelTest {
    private val testInvitationLink = "https://r3pqj.app.goo.gl/KPoa"
    private val testVerificationLink = com.standup.app.BuildConfig.BASE_URL + "/" + DeepLinkViewModel.VERIFY_EMAIL_ENDPOINT + "/756732562934569238465/53f85f43-7cab-449d-9ee2-6691adf6b208"
    private val testResetPasswordLink = com.standup.app.BuildConfig.BASE_URL + "/" + DeepLinkViewModel.RESET_PASSWORD_ENDPOINT + "/756732562934569238465/53f85f43-7cab-449d-9ee2-6691adf6b208"
    private val testInternalLink = com.standup.app.BuildConfig.BASE_URL + "/756732562934569238465/53f85f43-7cab-449d-9ee2-6691adf6b208"

    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var mockContext: Context
    private lateinit var mockSessionManager: UserSessionManager
    private lateinit var mockSharedPreference: SharedPreferences

    private lateinit var model: DeepLinkViewModel

    @Before
    fun setUp() {
        mockContext = Mockito.mock(Context::class.java)
        Mockito.`when`(mockContext.getString(anyInt())).thenReturn(testInvitationLink)

        //Mock shared prefrance
        mockSharedPreference = Mockito.mock(SharedPreferences::class.java)

        //Make user logged in
        Mockito.`when`(mockSharedPreference.getLong(anyString(), anyLong())).thenReturn(1616L)
        Mockito.`when`(mockSharedPreference.getString(anyString(), isNull())).thenReturn("test_token")

        //Make user not verified
        Mockito.`when`(mockSharedPreference.getBoolean(anyString(), anyBoolean())).thenReturn(false)

        //Set session manager
        val sharedPrefsProvider = SharedPrefsProvider(mockSharedPreference)
        mockSessionManager = UserSessionManager(sharedPrefsProvider)

        model = DeepLinkViewModel(mockSessionManager)
    }

    @Test
    @Throws(Exception::class)
    fun checkInit() {
        Assert.assertNull(model.errorMessage.value)
        Assert.assertFalse(model.fromInvitationLink.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkIfDeepLinkAllowed_UserLoggedIn() {
        Assert.assertTrue(model.checkIfDeepLinkAllowed())
    }

    @Test
    @Throws(Exception::class)
    fun checkIfDeepLinkAllowed_UserNotLoggedIn() {
        Mockito.`when`(mockSharedPreference.getString(anyString(), isNull())).thenReturn(null)
        Assert.assertFalse(model.checkIfDeepLinkAllowed())
    }

    @Test
    @Throws(Exception::class)
    fun checkInvitationLinkProcessing() {
        Assert.assertFalse(model.fromInvitationLink.value!!)
        model.processIncomingLink(mockContext, testInvitationLink)
        Assert.assertNotNull(model.fromInvitationLink.value)
        Assert.assertTrue(model.fromInvitationLink.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkLinkProcessing_ExternalHost() {
        Assert.assertNull(model.otherLink.value)

        val externalHostLink = "http://www.google.com"
        model.processIncomingLink(mockContext, externalHostLink)

        Assert.assertNotNull(model.otherLink.value)
        Assert.assertEquals(model.otherLink.value!!, externalHostLink)
    }

    @Test
    @Throws(Exception::class)
    fun checkLinkProcessing_ExternalHost_WithUpperCase() {
        Assert.assertNull(model.otherLink.value)

        val externalHostLink = com.standup.app.BuildConfig.BASE_URL.toUpperCase() + "/" +
                DeepLinkViewModel.VERIFY_EMAIL_ENDPOINT
        model.processIncomingLink(mockContext, externalHostLink)

        Assert.assertNotNull(model.otherLink.value)
        Assert.assertEquals(model.otherLink.value!!, externalHostLink)
    }

    @Test
    @Throws(Exception::class)
    fun checkValidationLinkProcessing_InvalidLink() {
        Assert.assertNull(model.errorMessage.value)

        val invalidLink = com.standup.app.BuildConfig.BASE_URL + "/" +
                DeepLinkViewModel.VERIFY_EMAIL_ENDPOINT + "/53f85f43-7cab-449d-9ee2-6691adf6b208"
        model.processIncomingLink(mockContext, invalidLink)

        Assert.assertNotNull(model.errorMessage.value)
        Assert.assertEquals(model.errorMessage.value!!.errorMessageRes,
                com.standup.app.R.string.error_invalid_verification_link)
    }

    @Test
    @Throws(Exception::class)
    fun checkValidationLinkProcessing_AlreadyVerified() {
        //Make is verified true.
        Mockito.`when`(mockSharedPreference.getBoolean(anyString(), anyBoolean())).thenReturn(true)

        Assert.assertNull(model.errorMessage.value)
        model.processIncomingLink(mockContext, testVerificationLink)

        Assert.assertNotNull(model.errorMessage.value)
        Assert.assertEquals(model.errorMessage.value!!.errorMessageRes,
                R.string.error_user_already_verified)
    }

    @Test
    @Throws(Exception::class)
    fun checkValidationLinkProcessing_ValidLink() {
        model.processIncomingLink(mockContext, testVerificationLink)
        Assert.assertNull(model.errorMessage.value)
        Assert.assertEquals(testVerificationLink, model.verifyEmailLink.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkResetPasswordLinkProcessing() {
        model.processIncomingLink(mockContext, testResetPasswordLink)
        Assert.assertNull(model.errorMessage.value)
        Assert.assertEquals(testResetPasswordLink, model.forgotPasswordLink.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkOtherInternalLinkProcessing() {
        model.processIncomingLink(mockContext, testInternalLink)
        Assert.assertNull(model.errorMessage.value)
        Assert.assertEquals(testInternalLink, model.otherLink.value)
    }
}
