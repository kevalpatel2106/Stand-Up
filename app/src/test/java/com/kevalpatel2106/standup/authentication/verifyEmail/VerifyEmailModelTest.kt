package com.kevalpatel2106.standup.authentication.verifyEmail

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.standup.UnitTestUtils
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException


/**
 * Created by Keval on 20/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class VerifyEmailModelTest {
    private val RESPONSE_DIR_PATH = String.format("%s/app/src/test/java/com/kevalpatel2106/standup/authentication/repo", File(File("").absolutePath))

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var verifyEmailViewModel: VerifyEmailViewModel
    private val mockServerManager = MockServerManager()

    companion object {

        @JvmStatic
        @BeforeClass
        fun setGlobal() = UnitTestUtils.initApp()
    }

    @Before
    fun setUp() {
        //Set the repo
        mockServerManager.startMockWebServer()
        verifyEmailViewModel = VerifyEmailViewModel(UserAuthRepositoryImpl(mockServerManager.getBaseUrl()))
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }


    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun checkResendVerificationEmailSuccess() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/resend_verification_email_success.json"))

        //Make the api call to the mock server
        verifyEmailViewModel.resendEmail(123)

        //There should be success.
        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)
        Assert.assertTrue(verifyEmailViewModel.mUiModel.value!!.isSuccess)
        Assert.assertNull(verifyEmailViewModel.errorMessage.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkResendVerificationEmailFieldMissing() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        //Make the api call to the mock server
        verifyEmailViewModel.resendEmail(123)

        //There should be success.
        Assert.assertFalse(verifyEmailViewModel.blockUi.value!!)
        Assert.assertFalse(verifyEmailViewModel.mUiModel.value!!.isSuccess)
        Assert.assertEquals(verifyEmailViewModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }
}