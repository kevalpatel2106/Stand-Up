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
