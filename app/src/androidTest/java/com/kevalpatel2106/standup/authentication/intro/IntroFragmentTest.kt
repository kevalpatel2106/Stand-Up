/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.authentication.intro

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.CustomMatchers
import com.kevalpatel2106.testutils.FragmentTestRule
import com.kevalpatel2106.utils.UserSessionManager
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(AndroidJUnit4::class)
class IntroFragmentTest : BaseTestClass(){
    @Before
    fun setUp() {
        UserSessionManager.clearUserSession()
        ApiProvider.init(InstrumentationRegistry.getContext())
    }

    @JvmField
    @Rule
    val fragmentRule : FragmentTestRule<IntroFragment> = FragmentTestRule(IntroFragment::class.java,
            IntroFragment.newInstance(R.string.sample_text, R.mipmap.ic_launcher))

    override fun getActivity(): Activity? = fragmentRule.activity

    @Test
    @Throws(IOException::class)
    fun checkView(){
        assertNotNull(fragmentRule.fragment.arguments)
        onView(withId(R.id.intro_iv)).check(ViewAssertions.matches(CustomMatchers.hasImage()))
        onView(withId(R.id.intro_tv)).check(ViewAssertions.matches(CustomMatchers.hasText()))
    }

}