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

package com.kevalpatel2106.common

import android.app.Activity
import android.support.test.espresso.Espresso
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withContentDescription
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.CustomMatchers
import io.reactivex.Observable
import org.junit.*
import org.junit.Assert.fail
import org.junit.runner.RunWith

/**
 * Created by Keval on 26-Jul-17.
 */
@RunWith(AndroidJUnit4::class)
class BaseActivityTest : BaseTestClass() {
    private val ACTIVITY_TITLE = "Test Activity"

    @JvmField
    @Rule
    var mActivityTestRule = ActivityTestRule(TestActivity::class.java)

    private lateinit var mTestActivity: TestActivity

    @Before
    fun init() {
        mTestActivity = mActivityTestRule.activity
    }

    @Test
    @Throws(Exception::class)
    fun checkToolbar() {
        //Test with the string title
        mTestActivity.runOnUiThread { mTestActivity.setToolbar(R.id.toolbar, ACTIVITY_TITLE, true) }
        CustomMatchers.matchToolbarTitle(ACTIVITY_TITLE).check(matches(isDisplayed()))
        Espresso.onView(withContentDescription("Navigate up")).check(matches(isDisplayed()))

        //Test with the string resource title
        mTestActivity.runOnUiThread { mTestActivity.setToolbar(R.id.toolbar, R.string.test, true) }
        CustomMatchers.matchToolbarTitle(mTestActivity.getString(R.string.test)).check(matches(isDisplayed()))
        Espresso.onView(withContentDescription("Navigate up")).check(matches(isDisplayed()))

        //Hide home button
        mTestActivity.runOnUiThread { mTestActivity.setToolbar(R.id.toolbar, ACTIVITY_TITLE, false) }
        try {
            Espresso.onView(withContentDescription("Navigate up")).perform(click())
            fail()
        } catch (e: NoMatchingViewException) {
            //Pass
        }

    }

    @Test
    @Throws(Exception::class)
    fun checkAddDisposable() {
        Assert.assertNotNull(mTestActivity.disposables)

        mTestActivity.addSubscription(null)
        Assert.assertEquals(mTestActivity.disposables.size().toLong(), 0)

        mTestActivity.addSubscription(Observable.just("1").subscribe())
        Assert.assertEquals(mTestActivity.disposables.size().toLong(), 1)
    }

    @After
    fun tearUp() {
        mTestActivity.finish()
    }

    override fun getActivity(): Activity {
        return mActivityTestRule.activity
    }
}
