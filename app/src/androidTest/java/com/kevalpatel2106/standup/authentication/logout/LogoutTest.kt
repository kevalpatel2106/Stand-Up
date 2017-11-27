package com.kevalpatel2106.standup.authentication.logout

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.standup.authentication.repo.MockUiUserAuthRepository
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.MockWebserverUtils
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import junit.framework.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 27-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(AndroidJUnit4::class)
class LogoutTest : BaseTestClass() {

    override fun getActivity(): Activity? = null

    @Test
    @Throws(IOException::class)
    fun checkLogoutSuccess() {
        prepareUserSession()

        val mockAutRepo = MockUiUserAuthRepository()
        mockAutRepo.enqueueResponse(MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.logout_success))

        Assert.assertNotNull(Logout.logout(InstrumentationRegistry.getContext(), mockAutRepo))
        verifyClearUserSession()
    }


    @Test
    @Throws(IOException::class)
    fun checkLogoutFail() {
        prepareUserSession()

        val mockAutRepo = MockUiUserAuthRepository()
        mockAutRepo.enqueueResponse(MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.authentication_field_missing))

        Assert.assertNotNull(Logout.logout(InstrumentationRegistry.getContext(), mockAutRepo))

        verifyClearUserSession()
    }

    @Test
    @Throws(IOException::class)
    fun checkClearSession() {
        prepareUserSession()
        Logout.clearSession(InstrumentationRegistry.getContext())
        verifyClearUserSession()
    }

    private fun prepareUserSession() {
        UserSessionManager.setNewSession(3248678, "Test User",
                "test@example.com", "234435664536", "http://example.com", true)
        SharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, true)
    }

    private fun verifyClearUserSession() {
        Assert.assertEquals(UserSessionManager.userId, -1)
        Assert.assertNull(UserSessionManager.email)
        Assert.assertNull(UserSessionManager.token)
        Assert.assertNull(UserSessionManager.displayName)
        Assert.assertNull(UserSessionManager.photo)
        Assert.assertFalse(UserSessionManager.isUserVerified)
        Assert.assertFalse(SharedPrefsProvider.getBoolFromPreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED))
    }
}