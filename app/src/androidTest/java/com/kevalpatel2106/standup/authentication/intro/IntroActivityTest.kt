package com.kevalpatel2106.standup.authentication.intro

import android.Manifest
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.login.LoginActivity
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.InstrumentationTestUtils
import kotlinx.android.synthetic.main.activity_intro.*
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Kevalpatel2106 on 21-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(AndroidJUnit4::class)
class IntroActivityTest : BaseTestClass() {

    @JvmField
    @Rule
    var mIntroActivityTestRule = ActivityTestRule(IntroActivity::class.java)

    @JvmField
    @Rule
    var mGetAccountPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.GET_ACCOUNTS)

    override fun getActivity(): IntroActivity = mIntroActivityTestRule.activity

    @Test
    @Throws(Exception::class)
    fun checkApiRunningStateChange() {
        checkAllButtonsEnable()
        activity.runOnUiThread { activity.mModel.mIsAuthenticationRunning.value = true }
        checkAllButtonsDisable()

        //switch to the landscape
        switchToLandscape()
        checkAllButtonsDisable()
    }

    @Test
    @Throws(Exception::class)
    fun checkButtonsWhenOrientationsChange() {
        //There should be three buttons
        //Login with FB should be disabled
        onView(withId(R.id.btn_create_account)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login_google_signin)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login_using_email)).check(matches(isDisplayed()))
        assertNull(activity.btn_login_fb_signin)
        checkAllButtonsEnable()

        //switch to the landscape
        switchToLandscape()

        //There should be three buttons
        //Login with FB should be visible in land scape
        onView(withId(R.id.btn_create_account)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login_google_signin)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login_using_email)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login_fb_signin)).check(matches(isDisplayed()))
        checkAllButtonsEnable()
    }

    @Test
    @Throws(Exception::class)
    fun checkManageButtons() {
        //Test enable all views
        activity.runOnUiThread { activity.manageButtons(false) }

        checkAllButtonsDisable()

        //Test disable all views
        activity.runOnUiThread { activity.manageButtons(true) }

        checkAllButtonsEnable()
    }

    @Test
    @Throws(Exception::class)
    fun checkViewPager() {
        val previousPos = activity.intro_view_pager.currentItem
        assertEquals(previousPos, 0)

        //Test enable all views
        onView(withId(R.id.intro_view_pager)).perform(ViewActions.swipeLeft())
        assertEquals(activity.intro_view_pager.currentItem, previousPos + 1)

        //switch to the landscape
        switchToLandscape()
        assertEquals(activity.intro_view_pager.currentItem, previousPos + 1)
    }

    private fun checkAllButtonsDisable() {
        activity.btn_login_fb_signin?.let {
            onView(withId(R.id.btn_login_fb_signin)).check(matches(not(isEnabled())))
        }
        onView(withId(R.id.btn_create_account)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_login_google_signin)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_login_using_email)).check(matches(not(isEnabled())))
    }

    private fun checkAllButtonsEnable() {
        activity.btn_login_fb_signin?.let {
            onView(withId(R.id.btn_login_fb_signin)).check(matches(isEnabled()))
        }
        onView(withId(R.id.btn_create_account)).check(matches(isEnabled()))
        onView(withId(R.id.btn_login_google_signin)).check(matches(isEnabled()))
        onView(withId(R.id.btn_login_using_email)).check(matches(isEnabled()))
    }

    /**
     * Check if the login with the email button opens [LoginActivity]?
     */
    @Test
    @Throws(Exception::class)
    fun testLoginWithEmailButton() {
        InstrumentationTestUtils.checkActivityOnViewClicked(mIntroActivityTestRule.activity,
                R.id.btn_login_using_email,
                LoginActivity::class.java.name)
    }

    /**
     * Check if the login with the email button opens [LoginActivity]?
     */
    @Test
    @Throws(Exception::class)
    fun testCreateAccountButton() {
        InstrumentationTestUtils.checkActivityOnViewClicked(mIntroActivityTestRule.activity,
                R.id.btn_create_account,
                LoginActivity::class.java.name)
    }
}