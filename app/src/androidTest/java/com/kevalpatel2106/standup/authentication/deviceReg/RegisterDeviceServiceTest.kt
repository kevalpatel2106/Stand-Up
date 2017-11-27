package com.kevalpatel2106.standup.authentication.deviceReg

import android.app.Activity
import android.support.test.InstrumentationRegistry
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.authentication.repo.MockUiUserAuthRepository
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.MockWebserverUtils
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class RegisterDeviceServiceTest : BaseTestClass() {
    override fun getActivity(): Activity? = null

    private val mRegisterDeviceService = RegisterDeviceService()
    private val mMockUserAuthRepository = MockUiUserAuthRepository()

    @Before
    fun setUp() {
        UserSessionManager.clearUserSession()
        ApiProvider.init(InstrumentationRegistry.getContext())
        mRegisterDeviceService.mUserAuthRepository = mMockUserAuthRepository
    }


    @After
    fun tearUp() {
        mMockUserAuthRepository.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkRegisterDeviceIdSuccess() {
        mMockUserAuthRepository.enqueueResponse(MockWebserverUtils
                .getStringFromFile(InstrumentationRegistry.getContext(),
                        com.kevalpatel2106.standup.test.R.raw.device_reg_success))
        mRegisterDeviceService.sendDeviceDataToServer("test.reg.id", "test.device.id")

        Thread.sleep(2000)
        assertTrue(SharedPrefsProvider.getBoolFromPreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED))
        assertNotNull(UserSessionManager.token)
    }

    @Test
    @Throws(IOException::class)
    fun checkRegisterDeviceIdFail() {
        mMockUserAuthRepository.enqueueResponse(MockWebserverUtils
                .getStringFromFile(InstrumentationRegistry.getContext(),
                        com.kevalpatel2106.standup.test.R.raw.authentication_field_missing))
        mRegisterDeviceService.sendDeviceDataToServer("test.reg.id", "test.device.id")

        Thread.sleep(2000)
        assertFalse(SharedPrefsProvider.getBoolFromPreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED))
    }


}