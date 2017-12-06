package com.kevalpatel2106.standup.authentication.logout

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.authentication.deviceReg.RegisterDeviceService
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import junit.framework.Assert
import org.junit.After
import org.junit.Before
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

    private val mRegisterDeviceService = RegisterDeviceService()
    private val mockServerManager = MockServerManager()
    private lateinit var mMockUserAuthRepository: UserAuthRepositoryImpl

    @Before
    fun setUp() {
        SharedPrefsProvider.init(InstrumentationRegistry.getContext())
        UserSessionManager.clearUserSession()
        ApiProvider.init(InstrumentationRegistry.getContext())

        //Set the repo
        mockServerManager.startMockWebServer()
        mMockUserAuthRepository = UserAuthRepositoryImpl(mockServerManager.getBaseUrl())
        mRegisterDeviceService.mUserAuthRepository = mMockUserAuthRepository
    }

    @After
    fun tearUp() {
        mockServerManager.close()
        ApiProvider.init()
    }

    @Test
    @Throws(IOException::class)
    fun checkLogoutSuccess() {
        prepareUserSession()

        mockServerManager.enqueueResponse(mockServerManager.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.logout_success))

        Thread.sleep(5000)
        Assert.assertNotNull(Logout.logout(InstrumentationRegistry.getContext(), mMockUserAuthRepository))
        verifyClearUserSession()
    }


    @Test
    @Throws(IOException::class)
    fun checkLogoutFail() {
        prepareUserSession()

        mockServerManager.enqueueResponse(mockServerManager.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.authentication_field_missing))

        Thread.sleep(5000)
        Assert.assertNotNull(Logout.logout(InstrumentationRegistry.getContext(), mMockUserAuthRepository))
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
        UserSessionManager.setNewSession(3248678,
                "Test User",
                "test@example.com",
                "234435664536",
                "http://example.com",
                true)
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