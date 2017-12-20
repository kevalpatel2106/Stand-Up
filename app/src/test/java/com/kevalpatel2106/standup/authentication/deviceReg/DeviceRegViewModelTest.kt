package com.kevalpatel2106.standup.authentication.deviceReg

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.UnitTestUtils
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.io.File
import java.io.IOException
import java.nio.file.Paths


/**
 * Created by Kevalpatel2106 on 07-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DeviceRegViewModelTest {
    private val RESPONSE_DIR_PATH = String.format("%s/app/src/test/java/com/kevalpatel2106/standup/authentication/repo", Paths.get(".").toAbsolutePath().toString())

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var deviceRegViewModel: DeviceRegViewModel
    private val mockServerManager = MockServerManager()

    companion object {

        @JvmStatic
        @BeforeClass
        fun setGlobal() = UnitTestUtils.initApp()
    }

    @Before
    fun setUp() {
        val context = Mockito.mock(Context::class.java)
        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        val sharedPrefsEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPrefs)
        Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn("149.3")

        //Set the repo
        mockServerManager.startMockWebServer()
        deviceRegViewModel = DeviceRegViewModel(UserAuthRepositoryImpl(mockServerManager.getBaseUrl()))
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(deviceRegViewModel.blockUi.value!!)
        Assert.assertNull(deviceRegViewModel.errorMessage.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkInvalidDeviceId() {
        deviceRegViewModel.register("", "test-firebase-id")

        Assert.assertEquals(deviceRegViewModel.errorMessage.value!!.errorRes,
                R.string.device_reg_error_invalid_device_id)
    }

    @Test
    @Throws(IOException::class)
    fun checkInvalidFcmId() {
        deviceRegViewModel.register("test-device-id", null)

        Assert.assertEquals(deviceRegViewModel.errorMessage.value!!.errorRes, R.string.device_reg_error_invalid_fcm_id)
    }

    @Test
    @Throws(IOException::class)
    fun checkInvalidDeviceRegSuccess() {
        mockServerManager.enqueueResponse(mockServerManager
                .getStringFromFile(File(RESPONSE_DIR_PATH + "/device_reg_success.json")))

        deviceRegViewModel.register("test-device-id", "test-firebase-id")

        Assert.assertNull(deviceRegViewModel.errorMessage.value)
        Assert.assertEquals(deviceRegViewModel.token.value, "64df48e6-45de-4bb5-879d-4c1a722f23fd")
    }

    @Test
    @Throws(IOException::class)
    fun checkInvalidDeviceRegFieldMissing() {
        mockServerManager.enqueueResponse(mockServerManager
                .getStringFromFile(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json")))

        deviceRegViewModel.register("test-device-id", "test-firebase-id")

        Assert.assertEquals(deviceRegViewModel.errorMessage.value!!.getMessage(null), "Required field missing.")
        Assert.assertNull(deviceRegViewModel.token.value)
    }
}