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

package com.kevalpatel2106.standup.authentication.verification

import android.content.ComponentName
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.intro.IntroActivity
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.CustomMatchers
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.UserSessionManager
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


/**
 * Created by Kevalpatel2106 on 27-Nov-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class EmailLinkVerificationUiActivityTest : BaseTestClass() {

    @Rule
    @JvmField
    val rule = ActivityTestRule<EmailLinkVerificationActivity>(EmailLinkVerificationActivity::class.java, true, false)

    override fun getActivity(): EmailLinkVerificationActivity? = rule.activity

    private val mockServerManager = MockServerManager()
    private lateinit var mMockUserAuthRepository: UserAuthRepositoryImpl

    @Before
    fun setUp() {
        UserSessionManager.clearUserSession()
        ApiProvider.init(InstrumentationRegistry.getContext())

        mockServerManager.startMockWebServer()
        mMockUserAuthRepository = UserAuthRepositoryImpl(mockServerManager.getBaseUrl())
    }

    @After
    fun tearUp() {
        mockServerManager.close()
        ApiProvider.init()
    }

    @Test
    @Throws(IOException::class)
    fun checkVerifyEmailSuccess() {
        mockServerManager.enqueueResponse(mockServerManager.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.email_verify_success), type = "text/html")

        //Prepare the intent
        val intent = Intent()
        intent.putExtra(EmailLinkVerificationActivity.ARG_URL,
                mockServerManager.getBaseUrl() + "/verifyEmailLink/32894723874/dskfhj-sdf-vcx-cx-vczx")
        rule.launchActivity(intent)
        activity!!.mAuthRepo = mMockUserAuthRepository

        Intents.init()
        onView(withId(R.id.verify_email_link_description_tv)).check(ViewAssertions.matches(withText(R.string.verify_email_link_success)))
        onView(withId(R.id.verify_email_link_logo)).check(ViewAssertions.matches(CustomMatchers.hasImage()))
        Thread.sleep(3000)
        Intents.intended(IntentMatchers.hasComponent(ComponentName(InstrumentationRegistry.getTargetContext(), IntroActivity::class.java.name)))
        Intents.release()
    }


    @Test
    @Throws(IOException::class)
    fun checkVerifyEmailFail() {
        mockServerManager.enqueueResponse(mockServerManager.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.authentication_field_missing))

        //Prepare the intent
        val intent = Intent()
        intent.putExtra(EmailLinkVerificationActivity.ARG_URL,
                mockServerManager.getBaseUrl() + "/verifyEmailLink/32894723874/dskfhj-sdf-vcx-cx-vczx")
        rule.launchActivity(intent)
        activity!!.mAuthRepo = mMockUserAuthRepository

        Intents.init()
        onView(withId(R.id.verify_email_link_logo)).check(ViewAssertions.matches(CustomMatchers.hasImage()))
        onView(withId(R.id.verify_email_link_description_tv)).check(ViewAssertions.matches(withText(R.string.verify_email_link_fail)))
        Thread.sleep(3000)
        Intents.intended(IntentMatchers.hasComponent(ComponentName(InstrumentationRegistry.getTargetContext(), IntroActivity::class.java.name)))
        Intents.release()
    }
}