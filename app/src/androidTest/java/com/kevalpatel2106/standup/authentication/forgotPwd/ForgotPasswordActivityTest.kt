package com.kevalpatel2106.standup.authentication.forgotPwd

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import com.kevalpatel2106.base.annotations.EndToEndTest
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.CustomMatchers
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.UserSessionManager
import org.hamcrest.Matchers
import org.junit.*
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

    private val mockServerManager = MockServerManager()
    private lateinit var mMockUserAuthRepository: UserAuthRepositoryImpl

    override fun getActivity(): ForgotPasswordActivity = rule.activity

    @Before
    fun setUp() {
        UserSessionManager.clearUserSession()
        ApiProvider.init(InstrumentationRegistry.getContext())

        mockServerManager.startMockWebServer()
        mMockUserAuthRepository = UserAuthRepositoryImpl(mockServerManager.getBaseUrl())
        activity.mModel.mUserAuthRepo = mMockUserAuthRepository
    }

    @After
    fun tearUp() {
        mockServerManager.close()
        ApiProvider.init()
    }


    @Test
    @Throws(Exception::class)
    fun checkApiRunningStateChange() {
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn))
                .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        activity.runOnUiThread { activity.mModel.blockUi.value = true }

        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn))
                .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))

        //switch to the landscape
        switchToLandscape()
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn))
                .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
        activity.runOnUiThread { activity.mModel.blockUi.value = false }
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
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn)).check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et)).check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_logo_iv)).check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        switchToLandscape()

        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_submit_btn)).check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et)).check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_logo_iv)).check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    /**
     * Test if in forgot password flow everything is working correctly?
     */
    @Test
    @EndToEndTest
    @Throws(Exception::class)
    fun testForgotPasswordSuccess() {
        mockServerManager.enqueueResponse(mockServerManager.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.forgot_password_success))

        //Check with valid email address
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .perform(ViewActions.clearText(),
                        ViewActions.typeText("text@example.com"),
                        ViewActions.closeSoftKeyboard())

        //Perform validation
        activity.submit()

        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .check(ViewAssertions.matches(Matchers.not(CustomMatchers.hasError())))
    }

    /**
     * Test if in forgot password flow everything is working correctly?
     */
    @Test
    @EndToEndTest
    @Throws(Exception::class)
    fun testForgotPasswordError() {
        mockServerManager.enqueueResponse(mockServerManager.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.authentication_field_missing))

        //Check with valid email address
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .perform(ViewActions.clearText(), ViewActions.typeText("text@example.com"), ViewActions.closeSoftKeyboard())

        activity.submit()

        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password_email_et))
                .check(ViewAssertions.matches(Matchers.not(CustomMatchers.hasError())))
        Assert.assertFalse(activity.isFinishing)
    }
}