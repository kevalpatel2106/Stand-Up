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

package com.kevalpatel2106.googleauth

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.app.FragmentActivity
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.TestActivity
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class GoogleSignInHelperTest : BaseTestClass() {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(TestActivity::class.java)

    override fun getActivity(): Activity = activityRule.activity

    @Test
    fun checkGso() {
        val googleSignInHelper = GoogleSignInHelper(activity as FragmentActivity,
                "testServerClientId",
                object : GoogleAuthResponse {
                    override fun onGoogleAuthSignIn(user: GoogleAuthUser) {
                    }

                    override fun onGoogleAuthSignInFailed() {
                    }

                    override fun onGoogleAuthSignOut(isSuccess: Boolean) {
                    }
                })
        val gso = googleSignInHelper.buildSignInOptions("testServerClientId")
        Assert.assertTrue(gso.isIdTokenRequested)
        Assert.assertEquals(gso.serverClientId, "testServerClientId")
    }
}
