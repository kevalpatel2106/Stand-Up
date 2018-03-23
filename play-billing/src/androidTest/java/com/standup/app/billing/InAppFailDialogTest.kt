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

package com.standup.app.billing

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import com.android.billingclient.api.BillingClient
import com.kevalpatel2106.testutils.TestActivity
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Kevalpatel2106 on 23-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class InAppFailDialogTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getContext()

        //Launching dialog
        InAppFailDialog.launch(context, rule.activity.supportFragmentManager, BillingClient.BillingResponse.ERROR)
    }

    @Test
    @Throws(Exception::class)
    fun testCheckDialogDisplayed() {
        // Initialize UiDevice instance
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Search for correct button in the dialog.
        val button = uiDevice.findObject(UiSelector().text(context.getString(android.R.string.ok)))
        Assert.assertTrue(button.exists())
    }

    @Test
    @Throws(Exception::class)
    fun checkDialogNeutralText() {
        onView(withId(android.R.id.button3)).check(matches(withText(context.getString(R.string.btn_title_faq))))
    }

    @Test
    @Throws(Exception::class)
    fun checkDialogPositiveText() {
        onView(withId(android.R.id.button1)).check(matches(withText(context.getString(android.R.string.ok))))
    }

    @Test
    @Throws(Exception::class)
    fun checkDialogPositiveClick() {
        onView(withId(android.R.id.button1)).perform(click())

        // Initialize UiDevice instance
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Search for correct button in the dialog.
        val button = uiDevice.findObject(UiSelector().text(context.getString(android.R.string.ok)))
        Assert.assertFalse(button.exists())
    }

    @Test
    @Throws(Exception::class)
    fun checkDialogNeutralClick() {
        onView(withId(android.R.id.button3)).perform(click())

        // Initialize UiDevice instance
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Search for correct button in the dialog.
        val button = uiDevice.findObject(UiSelector().text(context.getString(R.string.btn_title_faq)))
        Assert.assertFalse(button.exists())
    }

    @Test
    @Throws(Exception::class)
    fun checkDialogText() {
        val messageId = rule.activity.resources.getIdentifier("message", "id", "android")
        onView(withId(messageId)).check(matches(withText(context.getString(InAppFailDialog
                .getErrorMessage(BillingClient.BillingResponse.ERROR)))))
    }

    @Test
    @Throws(Exception::class)
    fun checkDialogTitle() {
        val titleId = rule.activity.resources.getIdentifier("alertTitle", "id", "android")
        onView(withId(titleId)).check(matches(withText(context.getString(R.string.buy_pro_failed_error_message))))
    }
}
