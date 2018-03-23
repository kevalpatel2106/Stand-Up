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

package com.standup.app.settings

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Kevalpatel2106 on 23-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class BaseSettingsDetailActivityTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule<BaseSettingsDetailActivityImpl>(BaseSettingsDetailActivityImpl::class.java)

    @Test
    @Throws(Exception::class)
    fun checkIsFrameLayoutAdded() {
        onView(withId(R.id.container)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun checkIfActionbarSet() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).check(matches(isDisplayed()))
    }

}
