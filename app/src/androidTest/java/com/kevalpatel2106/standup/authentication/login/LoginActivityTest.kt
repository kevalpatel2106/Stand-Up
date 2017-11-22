package com.kevalpatel2106.standup.authentication.login

import android.Manifest
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.CustomMatchers
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Kevalpatel2106 on 21-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(AndroidJUnit4::class)
class LoginActivityTest : BaseTestClass() {

    @JvmField
    @Rule
    var mLoginActivityRule = ActivityTestRule(LoginActivity::class.java)

    @JvmField
    @Rule
    var mGetAccountPermissionRule: GrantPermissionRule = GrantPermissionRule
            .grant(Manifest.permission.GET_ACCOUNTS)

    override fun getActivity(): LoginActivity = mLoginActivityRule.activity

    @Test
    @Throws(Exception::class)
    fun checkApiRunningStateChange() {
        onView(withId(R.id.login_scroll)).perform(swipeUp())

        onView(withId(R.id.btn_login_fb_signin)).check(matches(isEnabled()))
        onView(withId(R.id.btn_login_google_signin)).check(matches(isEnabled()))
        onView(withId(R.id.btn_login)).check(matches(isEnabled()))

        activity.runOnUiThread { activity.mModel.mIsAuthenticationRunning.value = true }

        onView(withId(R.id.btn_login_fb_signin)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_login_google_signin)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_login)).check(matches(not(isEnabled())))

        //switch to the landscape
        switchToLandscape()
        onView(withId(R.id.btn_login_fb_signin)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_login_google_signin)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_login)).check(matches(not(isEnabled())))
    }

    /**
     * Test if the invalid emails are being rejected in email edit text.
     */
    @Test
    @Throws(Exception::class)
    fun testInvalidEmailError() {
        //Check with invalid email address
        onView(withId(R.id.login_email_et))
                .perform(clearText(), typeText("testaexample.com"))
        onView(withId(R.id.btn_login))
                .perform(ViewActions.closeSoftKeyboard())
                .perform(ViewActions.click())
        onView(withId(R.id.login_email_et)).check(matches(CustomMatchers
                .withErrorText(Matchers.containsString(activity.getString(R.string.error_login_invalid_email)))))
    }

    /**
     * Check if the right email is validated in email edit text.
     */
    @Test
    @Throws(Exception::class)
    fun testValidEmailEt() {
        //Check with valid email address
        onView(withId(R.id.login_email_et))
                .perform(clearText(), typeText("text@example.com"))

        //Perform validation
        onView(withId(R.id.btn_login)).perform(ViewActions.closeSoftKeyboard()).perform(ViewActions.click())

        //check weather error is generated?
        onView(withId(R.id.login_email_et)).check(matches(not(CustomMatchers.hasError())))
    }

    /**
     * Test if only 16 characters are allowed in the password.
     */
    @Test
    @Throws(Exception::class)
    fun testSortPassword() {
        onView(withId(R.id.login_email_et)).perform(clearText(), typeText("text@example.com"))
        //Check with invalid email address
        onView(withId(R.id.login_password_et)).perform(clearText(), typeText("12345"))

        //Perform validation
        onView(withId(R.id.btn_login)).perform(ViewActions.closeSoftKeyboard()).perform(ViewActions.click())

        //check if the length is ok
        onView(withId(R.id.login_password_et)).check(matches(CustomMatchers.hasError()))
    }

    /**
     * Check if confirm password edit text is being displayed while switching from login to sign up.
     */
    @Test
    @Throws(Exception::class)
    fun switchLoginToSignUp() {
        //Switch form login to sign up by clicking go to button
        onView(withId(R.id.btn_login_toggle))
                .perform(click())
                .perform(ViewActions.closeSoftKeyboard())

        onView(withId(R.id.tiv_confirm_password)).check(matches(CustomMatchers.matchAlpha(1f)))
        onView(withId(R.id.tiv_name)).check(matches(CustomMatchers.matchAlpha(1f)))
    }

    /**
     * Check if confirm password edit text is being invisible when switching from sign up to login.
     */
    @Test
    @Throws(Exception::class)
    fun switchSignUpToLogin() {
        //Switch form sign up to login by clicking go to button twice
        onView(withId(R.id.btn_login_toggle)).perform(click())
        onView(withId(R.id.btn_login_toggle)).perform(click())
                .perform(ViewActions.closeSoftKeyboard())

        //check if the confirm password invisible?
        onView(withId(R.id.tiv_confirm_password)).check(matches(CustomMatchers.matchAlpha(0f)))
        onView(withId(R.id.tiv_name)).check(matches(CustomMatchers.matchAlpha(0f)))
    }

    /**
     * Check if confirm password edit text is being displayed while switching from login to sign up.
     */
    @Test
    @Throws(Exception::class)
    fun switchLoginToSignUpOrientationChange() {
        //Switch form login to sign up by clicking go to button
        onView(withId(R.id.btn_login_toggle)).perform(click()).perform(ViewActions.closeSoftKeyboard())

        //switch to the landscape
        switchToLandscape()
        onView(isRoot()).perform(ViewActions.closeSoftKeyboard())

        //check if the confirm password visible?
        onView(withId(R.id.tiv_confirm_password)).check(matches(CustomMatchers.matchAlpha(1f)))
        onView(withId(R.id.tiv_name)).check(matches(CustomMatchers.matchAlpha(1f)))
    }

    /**
     * Check if confirm password edit text is being invisible when switching from sign up to login.
     */
    @Test
    @Throws(Exception::class)
    fun switchSignUpToLoginOrientationChange() {
        //Switch form sign up to login by clicking go to button twice
        onView(withId(R.id.btn_login_toggle))
                .perform(click())
                .perform(click())
                .perform(ViewActions.closeSoftKeyboard())

        //switch to the landscape
        switchToLandscape()
        onView(isRoot()).perform(ViewActions.closeSoftKeyboard())

        //check if the confirm password invisible?
        onView(withId(R.id.tiv_confirm_password))
                .check(matches(not(isDisplayed())))

        //check if the name is invisible?
        onView(withId(R.id.tiv_name))
                .check(matches(not(isDisplayed())))
    }


    /**
     * Test if long name is invalid.
     */
    @Test
    @Throws(Exception::class)
    fun testSortName() {
        //Switch form login to sign up by clicking go to button
        onView(withId(R.id.btn_login_toggle)).perform(click()).perform(ViewActions.closeSoftKeyboard())

        onView(withId(R.id.login_email_et))
                .perform(clearText(), typeText("test@example.com"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_password_et))
                .perform(clearText(), typeText("123456789"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_scroll)).perform(swipeUp())
        onView(withId(R.id.login_confirm_password_et))
                .perform(clearText(), typeText("123456789"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_name_et))
                .perform(clearText(), typeText("123456"))
                .perform(ViewActions.closeSoftKeyboard())

        //Perform validation
        onView(withId(R.id.btn_login)).perform(ViewActions.closeSoftKeyboard()).perform(ViewActions.click())

        //check if the length is ok
        onView(withId(R.id.login_name_et)).check(matches(CustomMatchers.hasError()))
    }

    /**
     * Test if long name is invalid.
     */
    @Test
    @Throws(Exception::class)
    fun testInvalidName() {
        //Switch form login to sign up by clicking go to button
        onView(withId(R.id.btn_login_toggle))
                .perform(click())
                .perform(ViewActions.closeSoftKeyboard())

        //Check with invalid email address
        onView(withId(R.id.login_email_et))
                .perform(clearText(), typeText("test@example.com"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_password_et))
                .perform(clearText(), typeText("123456789"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_scroll)).perform(swipeUp())
        onView(withId(R.id.login_confirm_password_et))
                .perform(clearText(), typeText("123456789"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_name_et))
                .perform(clearText(), typeText("             "))
                .perform(ViewActions.closeSoftKeyboard())

        //Perform validation
        onView(withId(R.id.btn_login)).perform(ViewActions.closeSoftKeyboard()).perform(ViewActions.click())

        //check if the length is ok
        onView(withId(R.id.login_name_et)).check(matches(CustomMatchers.hasError()))
    }

    /**
     * Test if password and confirm password matches.
     */
    @Test
    @Throws(Exception::class)
    fun testConfirmPasswordNotMatch() {
        //Switch form login to sign up by clicking go to button
        onView(withId(R.id.btn_login_toggle))
                .perform(click())
                .perform(ViewActions.closeSoftKeyboard())

        //Check with invalid email address
        onView(withId(R.id.login_email_et))
                .perform(clearText(), typeText("test@example.com"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_password_et))
                .perform(clearText(), typeText("123456789"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_scroll)).perform(swipeUp())
        onView(withId(R.id.login_confirm_password_et))
                .perform(clearText(), typeText("1234567"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_name_et))
                .perform(clearText(), typeText("123456789"))
                .perform(ViewActions.closeSoftKeyboard())

        //Perform validation
        onView(withId(R.id.btn_login))
                .perform(ViewActions.closeSoftKeyboard())
                .perform(ViewActions.click())

        //check if the length is ok
        onView(withId(R.id.login_confirm_password_et)).check(matches(CustomMatchers.hasError()))
    }

    /**
     * Test if password and confirm password matches.
     */
    @Test
    @Throws(Exception::class)
    fun testOrientationChangeState() {
        //Switch form login to sign up by clicking go to button
        onView(withId(R.id.btn_login_toggle))
                .perform(click())
                .perform(ViewActions.closeSoftKeyboard())

        //Check with invalid email address
        onView(withId(R.id.login_email_et))
                .perform(clearText(), typeText("test@example.com"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_password_et))
                .perform(clearText(), typeText("123456789"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_scroll)).perform(swipeUp())
        onView(withId(R.id.login_confirm_password_et))
                .perform(clearText(), typeText("1234567"))
                .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.login_name_et))
                .perform(clearText(), typeText("123456789"))
                .perform(ViewActions.closeSoftKeyboard())

        //Perform validation
        switchToLandscape()

        //check if the length is ok
        onView(withId(R.id.login_password_et)).perform(swipeDown())
        onView(withId(R.id.login_email_et))
                .check(matches(withText("test@example.com")))
        onView(withId(R.id.login_password_et))
                .check(matches(withText("123456789")))
        onView(withId(R.id.login_confirm_password_et))
                .check(matches(withText("1234567")))
        onView(withId(R.id.login_scroll)).perform(swipeUp())
        onView(withId(R.id.login_name_et))
                .check(matches(withText("123456789")))
    }

    /**
     * Test if in login flow everything is working correctly?
     */
//    @Test
//    @Throws(Exception::class)
//    fun testFullLogin() {
//        //Enter valid email
//        onView(withId(R.id.login_email_et)).perform(clearText(), typeText("test@example.com"))
//
//        //Enter valid password
//        onView(withId(R.id.login_password_et)).perform(clearText(), typeText("123456789"))
//
//        //Check that there are no errors
//        onView(withId(R.id.login_password_et)).check(matches(not(CustomMatchers.hasError())))
//        onView(withId(R.id.login_email_et)).check(matches(not(CustomMatchers.hasError())))
//    }

    /**
     * Test if in sign in flow everything is working correctly?
     */
//    @Test
//    @Throws(Exception::class)
//    fun testFullSignUp() {
//        //Switch form login to sign up by clicking go to button
//        onView(withId(R.id.btn_login_toggle))
//                .perform(click())
//                .perform(ViewActions.closeSoftKeyboard())
//
//        //Enter valid email
//        onView(withId(R.id.login_email_et))
//                .perform(clearText(), typeText("test@example.com"))
//
//        //Enter valid password
//        onView(withId(R.id.login_password_et))
//                .perform(clearText(), typeText("123456789"))
//
//        //Enter same password
//        onView(withId(R.id.login_confirm_password_et))
//                .perform(clearText(), typeText("123456789"))
//
//        //Enter valid name
//        onView(withId(R.id.login_name_et))
//                .perform(clearText(), typeText("12345 6789"))
//
//        // Perform click
//        onView(withId(R.id.login_name_et)).perform(click())
//
//        //Check that there are no errors
//        onView(withId(R.id.login_name_et)).check(matches(not(CustomMatchers.hasError())))
//        onView(withId(R.id.login_confirm_password_et)).check(matches(not(CustomMatchers.hasError())))
//        onView(withId(R.id.login_password_et)).check(matches(not(CustomMatchers.hasError())))
//        onView(withId(R.id.login_email_et)).check(matches(not(CustomMatchers.hasError())))
//    }
}