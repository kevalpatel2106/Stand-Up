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

package com.kevalpatel2106.standup.deeplink

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.rule.ActivityTestRule
import com.kevalpatel2106.standup.SplashActivity
import com.kevalpatel2106.standup.authentication.verification.EmailLinkVerificationActivity
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.utils.UserSessionManager
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 27-Nov-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class DeepLinkActivityTest : BaseTestClass() {

    @Rule
    @JvmField
    val rule = ActivityTestRule<DeepLinkActivity>(DeepLinkActivity::class.java, true, false)

    override fun getActivity(): Activity? = rule.activity

    @Before
    fun setUp() {
        UserSessionManager.setNewSession(3248678, "Test User",
                "test@example.com", "234435664536", null, false)
        Intents.init()
    }

    @Test
    @Throws(IOException::class)
    fun checkUserNotLoggedIn() {
        UserSessionManager.clearToken()
        //Prepare the intent
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://example.com/verifyEmailLink/0/0")
        rule.launchActivity(intent)

        Thread.sleep(1000)

        Intents.intended(IntentMatchers.hasComponent(ComponentName(InstrumentationRegistry.getTargetContext(),
                SplashActivity::class.java.name)))
    }

    @Test
    @Throws(IOException::class)
    fun checkVerifyLink() {
        //Prepare the intent
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://example.com/verifyEmailLink/0/0")
        rule.launchActivity(intent)

        Thread.sleep(1000)

        Intents.intended(IntentMatchers.hasComponent(ComponentName(InstrumentationRegistry.getTargetContext(),
                EmailLinkVerificationActivity::class.java.name)))
    }

    @Test
    @Throws(IOException::class)
    fun checkForgotPasswordLink() {
        //Prepare the intent
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://example.com/forgotPasswordLink/0/0")
        rule.launchActivity(intent)

        Thread.sleep(1000)

        Intents.intended(IntentMatchers.hasComponent(ComponentName(InstrumentationRegistry.getTargetContext(),
                WebViewActivity::class.java.name)))
    }


    @Test
    @Throws(IOException::class)
    fun checkInvalidVerifyLink() {
        //Prepare the intent
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://example.com/verifyEmailLink/")
        rule.launchActivity(intent)

        Thread.sleep(1000)

        Intents.intended(IntentMatchers.hasComponent(ComponentName(InstrumentationRegistry.getTargetContext(),
                WebViewActivity::class.java.name)))
    }


    @Test
    @Throws(IOException::class)
    fun checkInvalidAction() {
        //Prepare the intent
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("https://example.com/verifyEmailLink/0/0")
        rule.launchActivity(intent)

        Thread.sleep(1000)

        Intents.intended(IntentMatchers.hasComponent(ComponentName(InstrumentationRegistry.getTargetContext(),
                WebViewActivity::class.java.name)))
    }


    @Test
    @Throws(IOException::class)
    fun checkInvalidDeepLink() {
        //Prepare the intent
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("https://example.com/randomLink/0/0")
        rule.launchActivity(intent)

        Thread.sleep(1000)

        Intents.intended(IntentMatchers.hasComponent(ComponentName(InstrumentationRegistry.getTargetContext(),
                WebViewActivity::class.java.name)))
    }

    @After
    fun tearUp() {
        UserSessionManager.clearUserSession()
        Intents.release()
    }
}