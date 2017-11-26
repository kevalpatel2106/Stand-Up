package com.kevalpatel2106.standup.authentication.forgotPwd

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.repo.MockUiUserAuthRepository
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.CustomMatchers
import com.kevalpatel2106.testutils.MockWebserverUtils
import com.kevalpatel2106.utils.UserSessionManager
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/**
 * Created by Keval on 26/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class ForgotPasswordActivityTest : BaseTestClass() {

    @JvmField
    @Rule
    val rule = ActivityTestRule<ForgotPasswordActivity>(ForgotPasswordActivity::class.java)

    override fun getActivity(): ForgotPasswordActivity = rule.activity

    @Before
    fun setUp() {
        UserSessionManager.clearUserSession()
        ApiProvider.init(InstrumentationRegistry.getContext())
    }

    @Test
    @Throws(Exception::class)
    fun checkApiRunningStateChange() {
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn)).check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        activity.runOnUiThread { activity.mModel.mIsAuthenticationRunning.value = true }

        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))

        //switch to the landscape
        switchToLandscape()
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
    }

    /**
     * Test if the invalid emails are being rejected in email edit text.
     */
    @Test
    @Throws(Exception::class)
    fun testInvalidEmailError() {
        //Check with invalid email address
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .perform(ViewActions.clearText(), ViewActions.typeText("testaexample.com"))
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn))
                .perform(ViewActions.closeSoftKeyboard())
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .check(ViewAssertions.matches(CustomMatchers
                        .withErrorText(Matchers.containsString(activity.getString(R.string.error_login_invalid_email)))))
    }

    /**
     * Check if the right email is validated in email edit text.
     */
    @Test
    @Throws(Exception::class)
    fun testValidEmailEt() {
        //Check with valid email address
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .perform(ViewActions.clearText(), ViewActions.typeText("text@example.com"))

        //Perform validation
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn))
                .perform(ViewActions.closeSoftKeyboard())
                .perform(ViewActions.click())

        //check weather error is generated?
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .check(ViewAssertions.matches(Matchers.not(CustomMatchers.hasError())))
    }

    @Test
    @Throws(IOException::class)
    fun checkUi() {
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn)).check(ViewAssertions.matches(ViewMatchers.isClickable()))
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et)).check(ViewAssertions.matches(ViewMatchers.isClickable()))
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_logo_iv)).check(ViewAssertions.matches(ViewMatchers.isClickable()))

        switchToLandscape()

        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn)).check(ViewAssertions.matches(ViewMatchers.isClickable()))
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et)).check(ViewAssertions.matches(ViewMatchers.isClickable()))
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_logo_iv)).check(ViewAssertions.matches(ViewMatchers.isClickable()))
    }

    /**
     * Test if in forgot password flow everything is working correctly?
     */
    @Test
    @Throws(Exception::class)
    fun testForgotPasswordSuccess() {
        val mockRepo = MockUiUserAuthRepository()
        mockRepo.enqueueResponse(MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.forgot_password_success))
        activity.mModel.mUserAuthRepo = mockRepo

        //Check with valid email address
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .perform(ViewActions.clearText(), ViewActions.typeText("text@example.com"))

        //Perform validation
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn))
                .perform(ViewActions.closeSoftKeyboard())
                .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .check(ViewAssertions.matches(Matchers.not(CustomMatchers.hasError())))
        Assert.assertTrue(activity.isFinishing)
    }

    /**
     * Test if in forgot password flow everything is working correctly?
     */
    @Test
    @Throws(Exception::class)
    fun testForgotPasswordError() {
        val mockRepo = MockUiUserAuthRepository()
        mockRepo.enqueueResponse(MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.authentication_field_missing))
        activity.mModel.mUserAuthRepo = mockRepo

        //Check with valid email address
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .perform(ViewActions.clearText(), ViewActions.typeText("text@example.com"))

        //Perform validation
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn))
                .perform(ViewActions.closeSoftKeyboard())
                .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .check(ViewAssertions.matches(Matchers.not(CustomMatchers.hasError())))
        Assert.assertFalse(activity.isFinishing)
    }
}