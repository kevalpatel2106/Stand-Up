package com.kevalpatel2106.standup.authentication.verifyEmail

import android.content.ComponentName
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.standup.dashboard.Dashboard
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.CustomMatchers
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.UserSessionManager
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/**
 * Created by Keval on 26/11/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
class VerifyEmailActivityTest : BaseTestClass() {

    @JvmField
    @Rule
    val rule = ActivityTestRule<VerifyEmailActivity>(VerifyEmailActivity::class.java)

    override fun getActivity(): VerifyEmailActivity = rule.activity

    private val mockServerManager = MockServerManager()
    private lateinit var mMockUserAuthRepository: UserAuthRepositoryImpl

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
    @Throws(IOException::class)
    fun checkSkip() {
        Intents.init()
        onView(withId(R.id.verify_btn_skip)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ComponentName(InstrumentationRegistry.getTargetContext(),
                Dashboard::class.java.name)))
        Intents.release()
    }

    @Test
    @Throws(Exception::class)
    fun checkApiRunningStateChange() {
        onView(withId(R.id.verify_btn_open_mail_btn)).check(matches(isEnabled()))
        onView(withId(R.id.verify_btn_skip)).check(matches(isEnabled()))
        onView(withId(R.id.verify_btn_resend)).check(matches(isEnabled()))

        activity.runOnUiThread { activity.mModel.blockUi.value = true }

        onView(withId(R.id.verify_btn_open_mail_btn)).check(matches(not(isEnabled())))
        onView(withId(R.id.verify_btn_skip)).check(matches(not(isEnabled())))
        onView(withId(R.id.verify_btn_resend)).check(matches(not(isEnabled())))

        //switch to the landscape
        switchToLandscape()
        onView(withId(R.id.verify_btn_open_mail_btn)).check(matches(not(isEnabled())))
        onView(withId(R.id.verify_btn_skip)).check(matches(not(isEnabled())))
        onView(withId(R.id.verify_btn_resend)).check(matches(not(isEnabled())))
    }

    @Test
    @Throws(IOException::class)
    fun checkResend() {
        mockServerManager.enqueueResponse(mockServerManager.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.resend_verification_email_success))

        onView(withId(R.id.verify_btn_resend)).perform(click())

        Thread.sleep(2000)
        onView(withText(R.string.message_verification_email_sent)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(IOException::class)
    fun checkUi() {
        onView(withId(R.id.verify_btn_resend)).check(matches(isClickable()))
        onView(withId(R.id.verify_btn_skip)).check(matches(isClickable()))
        onView(withId(R.id.verify_btn_open_mail_btn)).check(matches(isClickable()))
        onView(withId(R.id.verify_iv_email)).check(matches(CustomMatchers.hasImage()))
        onView(withId(R.id.verify_description_text))
                .check(matches(withText(String.format(activity.getString(R.string.verify_email_screen_message), UserSessionManager.email))))
        onView(withId(R.id.verify_title_text)).check(matches(withText(R.string.verify_email_send_screen_title)))

        switchToLandscape()

        onView(withId(R.id.verify_btn_resend)).check(matches(isClickable()))
        onView(withId(R.id.verify_btn_skip)).check(matches(isClickable()))
        onView(withId(R.id.verify_btn_open_mail_btn)).check(matches(isClickable()))
        onView(withId(R.id.verify_iv_email)).check(matches(CustomMatchers.hasImage()))
        onView(withId(R.id.verify_description_text))
                .check(matches(withText(String.format(activity.getString(R.string.verify_email_screen_message), UserSessionManager.email))))
        onView(withId(R.id.verify_title_text)).check(matches(withText(R.string.verify_email_send_screen_title)))
    }
}