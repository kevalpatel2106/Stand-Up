package com.kevalpatel2106.facebookauth

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import com.facebook.FacebookSdk
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.TestActivity
import org.json.JSONObject
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * Created by Kevalpatel2106 on 16-Nov-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
class FacebookHelperTest : BaseTestClass() {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(TestActivity::class.java)

    override fun getActivity(): Activity = activityRule.activity

    companion object {
        @Suppress("DEPRECATION")
        @JvmStatic
        @BeforeClass
        fun setSdk() {
            FacebookSdk.setApplicationId("72467826426")
            if (!FacebookSdk.isInitialized())
                FacebookSdk.sdkInitialize(InstrumentationRegistry.getContext().applicationContext)
        }
    }

    @Test
    fun checkSdiInitialize() {
        FacebookHelper(object : FacebookResponse {
            override fun onFbSignInFail() {
            }

            override fun onFbProfileReceived(facebookUser: FacebookUser) {
            }

            override fun onFBSignOut() {
            }
        }, "")

        Assert.assertTrue(FacebookSdk.isInitialized())
    }

    @Test
    fun checkParseFbUser() {
        val fbHelper = FacebookHelper(object : FacebookResponse {
            override fun onFbSignInFail() {
                //Do nothing
            }

            override fun onFbProfileReceived(facebookUser: FacebookUser) {
                //Do nothing
            }

            override fun onFBSignOut() {
                //Do nothing
            }
        }, "")

        val response = JSONObject(MockServerManager().getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.facebookauth.test.R.raw.fb_response))
        val fbUser = fbHelper.parseResponse(response)

        Assert.assertEquals(fbUser.facebookID, "398475894375893")
        Assert.assertEquals(fbUser.name, "John Doe")
        Assert.assertEquals(fbUser.email, "john@example.com")
        Assert.assertEquals(fbUser.gender, "female")
        Assert.assertEquals(fbUser.profilePic, "https://scontent.xx.fbcdn.net/v/t1.0-1/c154.33.413.413/s50x50/427144_108142642648116_878107670_n.jpg?oh=a0dd9353b55c083bab97c5a46dd4ce04&oe=5A94F71A")
        Assert.assertEquals(fbUser.coverPicUrl, "https://scontent.xx.fbcdn.net/v/t1.0-1/c154.33.413.413/s50x50/427144_108142642648116_878107670_n.jpg?oh=a0dd9353b55c083bab97c5a46dd4ce04&oe=5A94F71A")
        Assert.assertEquals(fbUser.about, "This is the test about.")
        Assert.assertEquals(fbUser.bio, "This is the test bio.")
    }

}