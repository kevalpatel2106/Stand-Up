package com.kevalpatel2106.standup.authentication.verifyEmail

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.standup.UnitTestUtils
import com.kevalpatel2106.standup.authentication.repo.MockUserAuthRepository
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

    private lateinit var verifyEmailUiModel: VerifyEmailViewModel
    private var mTestRepoMock = MockUserAuthRepository()

    companion object {

        @JvmStatic
        @BeforeClass
        fun setGlobal() = UnitTestUtils.initApp()
    }

    @Before
    fun setUp() {
        UnitTestUtils.initApp()

        //Swap the repo
        verifyEmailUiModel = VerifyEmailViewModel(mTestRepoMock)
    }

    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(verifyEmailUiModel.blockUi.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun checkResendVerificationEmailSuccess() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/resend_verification_email_success.json"))

        //Make the api call to the mock server
        verifyEmailUiModel.resendEmail(123)

        //There should be success.
        Assert.assertFalse(verifyEmailUiModel.blockUi.value!!)
        Assert.assertTrue(verifyEmailUiModel.mUiModel.value!!.isSuccess)
        Assert.assertNull(verifyEmailUiModel.errorMessage.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkResendVerificationEmailFieldMissing() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        //Make the api call to the mock server
        verifyEmailUiModel.resendEmail(123)

        //There should be success.
        Assert.assertFalse(verifyEmailUiModel.blockUi.value!!)
        Assert.assertFalse(verifyEmailUiModel.mUiModel.value!!.isSuccess)
        Assert.assertEquals(verifyEmailUiModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }


    @After
    fun tearUp() {
        mTestRepoMock.close()
    }
}