package com.kevalpatel2106.standup.authentication.deviceReg

import android.app.Activity
import android.support.test.InstrumentationRegistry
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.MockServerManager
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
    private val mockServerManager = MockServerManager()

    @Before
    fun setUp() {
        UserSessionManager.clearUserSession()
        ApiProvider.init(InstrumentationRegistry.getContext())

        //Set the repo
        mockServerManager.startMockWebServer()
        mRegisterDeviceService.mModel = DeviceRegViewModel(UserAuthRepositoryImpl(mockServerManager.getBaseUrl()))
    }

    @After
    fun tearUp() {
        mockServerManager.close()
        ApiProvider.init()
    }

    @Test
    @Throws(IOException::class)
    fun checkRegisterDeviceIdSuccess() {
        mockServerManager.enqueueResponse(mockServerManager.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.device_reg_success))
        mRegisterDeviceService.mModel.sendDeviceDataToServer("test.reg.id", "test.device.id")

        Thread.sleep(5000)
        assertTrue(SharedPrefsProvider.getBoolFromPreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED))
        assertNotNull(UserSessionManager.token)
    }

    @Test
    @Throws(IOException::class)
    fun checkRegisterDeviceIdFail() {
        mockServerManager.enqueueResponse(mockServerManager.getStringFromFile(InstrumentationRegistry.getContext(),
                com.kevalpatel2106.standup.test.R.raw.authentication_field_missing))
        mRegisterDeviceService.mModel.sendDeviceDataToServer("test.reg.id", "test.device.id")

        Thread.sleep(5000)
        assertFalse(SharedPrefsProvider.getBoolFromPreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED))
    }
}