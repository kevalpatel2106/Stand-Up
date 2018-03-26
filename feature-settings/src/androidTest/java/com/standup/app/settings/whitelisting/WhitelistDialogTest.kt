package com.standup.app.settings.whitelisting

import android.app.Activity
import android.os.Build
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.CustomMatchers
import com.kevalpatel2106.testutils.TestActivity
import com.standup.app.settings.R
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Kevalpatel2106 on 24-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class WhitelistDialogTest : BaseTestClass() {

    override fun getActivity(): Activity {
        return rule.activity
    }

    @JvmField
    @Rule
    val rule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    @Before
    fun setUp() {
        WhitelistDialog().show(rule.activity.supportFragmentManager, WhitelistDialog::class.simpleName)
    }

    @Test
    @Throws(Exception::class)
    fun testCheckDialogDisplayed() {
        // Initialize UiDevice instance
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Search for correct button in the dialog.
        val button = uiDevice.findObject(UiSelector()
                .text(rule.activity.getString(R.string.whitelist_app_dialog_btn_title)))
        Assert.assertTrue(button.exists())
    }

    @Test
    @Throws(Exception::class)
    fun checkUpdateSettingsButtonText() {
        onView(withId(R.id.whitelist_app_dialog_btn))
                .check(matches(withText(R.string.whitelist_app_dialog_btn_title)))
    }

    @Test
    @Throws(Exception::class)
    fun checkMessage() {
        onView(withId(R.id.whitelist_app_dialog_subtitle_tv))
                .check(matches(withText(R.string.whitelist_app_dialog_subtitle_text)))
    }

    @Test
    @Throws(Exception::class)
    fun checkTitle() {
        onView(withId(R.id.whitelist_app_dialog_title_tv))
                .check(matches(withText(R.string.whitelist_app_dialog_title_text)))
    }

    @Test
    @Throws(Exception::class)
    fun checkImage() {
        onView(withId(R.id.whitelist_app_dialog_icon))
                .check(matches(CustomMatchers.hasImage()))
    }

    @Test
    @Throws(Exception::class)
    fun checkUpdateSettingsClick() {
        onView(withId(R.id.whitelist_app_dialog_btn)).perform(ViewActions.click())

        // Initialize UiDevice instance
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Search for correct button in the dialog.
        val button = uiDevice.findObject(UiSelector()
                .text(rule.activity.getString(R.string.whitelist_app_dialog_btn_title)))
        Assert.assertFalse(button.exists())

        //This test will only run if the locale is set to english.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val current = rule.activity.resources.configuration.locale
            if (current.toLanguageTag().startsWith("en")) {
                val settingsTitle = uiDevice.findObject(UiSelector().text("Battery optimisation"))
                Assert.assertTrue(settingsTitle.exists())
            }
        }
    }
}
