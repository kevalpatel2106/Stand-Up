package com.kevalpatel2106.standup.authentication.forgotPwd

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.standup.UnitTestUtils
import com.kevalpatel2106.standup.authentication.repo.MockUserAuthRepository
import org.junit.*
import org.junit.rules.TestRule
import java.io.File
import java.io.IOException

/**
 * Created by Keval on 26/11/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
class ForgotPasswordViewModelTest {
    private val RESPONSE_DIR_PATH = String.format("%s/app/src/test/java/com/kevalpatel2106/standup/authentication/repo", File(File("").absolutePath))

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var verifyEmailUiModel: ForgotPasswordViewModel
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
        verifyEmailUiModel = ForgotPasswordViewModel(mTestRepoMock)
    }

    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(verifyEmailUiModel.mIsAuthenticationRunning.value!!)
    }

    ///////////////// Social Sign Up ////////////////////
    @Test
    @Throws(IOException::class)
    fun checkResendVerificationEmailSuccess() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/forgot_password_success.json"))

        //Make the api call to the mock server
        verifyEmailUiModel.forgotPasswordRequest("test@example.com")

        //There should be success.
        Assert.assertFalse(verifyEmailUiModel.mIsAuthenticationRunning.value!!)
        Assert.assertTrue(verifyEmailUiModel.mUiModel.value!!.isSuccess)
        Assert.assertNull(verifyEmailUiModel.mUiModel.value!!.errorMsg)
    }

    @Test
    @Throws(IOException::class)
    fun checkResendVerificationEmailFieldMissing() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        //Make the api call to the mock server
        verifyEmailUiModel.forgotPasswordRequest("test@example.com")

        //There should be success.
        Assert.assertFalse(verifyEmailUiModel.mIsAuthenticationRunning.value!!)
        Assert.assertFalse(verifyEmailUiModel.mUiModel.value!!.isSuccess)
        Assert.assertNotNull(verifyEmailUiModel.mUiModel.value!!.errorMsg)
    }


    @After
    fun tearUp() {
        mTestRepoMock.close()
    }
}