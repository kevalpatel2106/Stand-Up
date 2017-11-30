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

    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
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
        forgotPasswordViewModel = ForgotPasswordViewModel(mTestRepoMock)
    }

    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(forgotPasswordViewModel.blockUi.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun checkForgotPasswordInvalidEmail() {
        forgotPasswordViewModel.forgotPasswordRequest("test@example")

        Assert.assertFalse(forgotPasswordViewModel.blockUi.value!!)
        Assert.assertEquals(forgotPasswordViewModel.mEmailError.value!!.errorRes, com.kevalpatel2106.standup.R.string.error_login_invalid_email)
    }

    @Test
    @Throws(IOException::class)
    fun checkForgotPasswordSuccess() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/forgot_password_success.json"))

        //Make the api call to the mock server
        forgotPasswordViewModel.forgotPasswordRequest("test@example.com")

        //There should be success.
        Assert.assertFalse(forgotPasswordViewModel.blockUi.value!!)
        Assert.assertTrue(forgotPasswordViewModel.mUiModel.value!!.isSuccess)
        Assert.assertNull(forgotPasswordViewModel.errorMessage.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkForgotPasswordFieldMissing() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        //Make the api call to the mock server
        forgotPasswordViewModel.forgotPasswordRequest("test@example.com")

        //There should be success.
        Assert.assertFalse(forgotPasswordViewModel.blockUi.value!!)
        Assert.assertFalse(forgotPasswordViewModel.mUiModel.value!!.isSuccess)
        Assert.assertEquals(forgotPasswordViewModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }


    @After
    fun tearUp() {
        mTestRepoMock.close()
    }
}