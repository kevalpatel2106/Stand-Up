package com.kevalpatel2106.standup.authentication.verifyEmail

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
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.intro.IntroActivity
import com.kevalpatel2106.standup.authentication.repo.MockUiUserAuthRepository
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.CustomMatchers
import com.kevalpatel2106.testutils.MockWebserverUtils
import org.junit.After
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

    private var mTestRepoMock = MockUiUserAuthRepository()

    @Test
    @Throws(IOException::class)
    fun checkVerifyEmailSuccess() {
        mTestRepoMock.enqueueResponse(response = MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.email_verify_success), type = "text/html")

        //Prepare the intent
        val intent = Intent()
        intent.putExtra(EmailLinkVerificationActivity.ARG_URL, mTestRepoMock.getBase() + "/verifyEmailLink/32894723874/dskfhj-sdf-vcx-cx-vczx")
        rule.launchActivity(intent)
        activity!!.mAuthRepo = mTestRepoMock

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
        mTestRepoMock.enqueueResponse(response = MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.authentication_field_missing))

        //Prepare the intent
        val intent = Intent()
        intent.putExtra(EmailLinkVerificationActivity.ARG_URL, mTestRepoMock.getBase() + "/verifyEmailLink/32894723874/dskfhj-sdf-vcx-cx-vczx")
        rule.launchActivity(intent)
        activity!!.mAuthRepo = mTestRepoMock

        Intents.init()
        onView(withId(R.id.verify_email_link_logo)).check(ViewAssertions.matches(CustomMatchers.hasImage()))
        onView(withId(R.id.verify_email_link_description_tv)).check(ViewAssertions.matches(withText(R.string.verify_email_link_fail)))
        Thread.sleep(3000)
        Intents.intended(IntentMatchers.hasComponent(ComponentName(InstrumentationRegistry.getTargetContext(), IntroActivity::class.java.name)))
        Intents.release()
    }


    @After
    fun tearUp() {
        mTestRepoMock.close()
    }
}