package com.kevalpatel2106.standup.authentication.login

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.authentication.repo.MockUserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.SignUpRequest
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
class LoginViewModelTest {
    private val RESPONSE_DIR_PATH = String.format("%s/app/src/test/java/com/kevalpatel2106/standup/authentication/repo", File(File("").absolutePath))

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mTestRepoMock: MockUserAuthRepository

    @Before
    fun setUp() {
        //Init server
        ApiProvider.init()

        mTestRepoMock = MockUserAuthRepository()
        loginViewModel = LoginViewModel(mTestRepoMock)
    }

    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(loginViewModel.mIsAuthenticationRunning.value!!)
    }

    ///////////////// Social Sign Up ////////////////////
    @Test
    @Throws(IOException::class)
    fun checkAuthenticateSocialUserSignUpSuccess() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/social_user_sign_up_success.json"))

        //Make the api call to the mock server
        val signInRequest = SignUpRequest("test@example.com", "Test User", null, null);
        loginViewModel.authenticateSocialUser(signInRequest)

        //There should be success.
        Assert.assertFalse(loginViewModel.mIsAuthenticationRunning.value!!)
        Assert.assertTrue(loginViewModel.mLoginUiModel.value!!.isSuccess)
        Assert.assertTrue(loginViewModel.mLoginUiModel.value!!.isNewUser)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateSocialUserLoginSuccess() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/social_user_login_success.json"))

        //Make the api call to the mock server
        val signInRequest = SignUpRequest("test@example.com", "Test User", null, null);
        loginViewModel.authenticateSocialUser(signInRequest)

        //There should be success.
        Assert.assertFalse(loginViewModel.mIsAuthenticationRunning.value!!)
        Assert.assertTrue(loginViewModel.mLoginUiModel.value!!.isSuccess)
        Assert.assertFalse(loginViewModel.mLoginUiModel.value!!.isNewUser)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateSocialUserFieldMissing() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        //Make the api call to the mock server
        loginViewModel.authenticateSocialUser(SignUpRequest("test@example.com", "Test User", null, null))

        //There should be success.
        Assert.assertFalse(loginViewModel.mIsAuthenticationRunning.value!!)
        Assert.assertFalse(loginViewModel.mLoginUiModel.value!!.isSuccess)
        Assert.assertEquals(loginViewModel.mLoginUiModel.value!!.errorMsg, "Required field missing.")
    }


    ///////////////// Sign Up ////////////////////
    @Test
    @Throws(IOException::class)
    fun checkAuthenticateSignUpSuccess() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/user_sign_up_success.json"))

        //Make the api call to the mock server
        loginViewModel.performSignUp("test@example.com", "1234567989", "Test User")

        //There should be success.
        Assert.assertFalse(loginViewModel.mIsAuthenticationRunning.value!!)
        Assert.assertTrue(loginViewModel.mLoginUiModel.value!!.isSuccess)
        Assert.assertTrue(loginViewModel.mLoginUiModel.value!!.isNewUser)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateSignUpFieldMissing() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        //Make the api call to the mock server
        loginViewModel.performSignUp("test@example.com", "1234567989", "Test User")

        //There should be success.
        Assert.assertFalse(loginViewModel.mIsAuthenticationRunning.value!!)
        Assert.assertFalse(loginViewModel.mLoginUiModel.value!!.isSuccess)
        Assert.assertEquals(loginViewModel.mLoginUiModel.value!!.errorMsg, "Required field missing.")
    }

    ///////////////// Login ////////////////////
    @Test
    @Throws(IOException::class)
    fun checkAuthenticateLoginSuccess() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/user_login_success.json"))

        //Make the api call to the mock server
        loginViewModel.performSignIn("test@example.com", "1234567989")

        //There should be success.
        Assert.assertFalse(loginViewModel.mIsAuthenticationRunning.value!!)
        Assert.assertTrue(loginViewModel.mLoginUiModel.value!!.isSuccess)
        Assert.assertFalse(loginViewModel.mLoginUiModel.value!!.isNewUser)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateLoginFieldMissing() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        //Make the api call to the mock server
        loginViewModel.performSignIn("test@example.com", "1234567989")

        //There should be success.
        Assert.assertFalse(loginViewModel.mIsAuthenticationRunning.value!!)
        Assert.assertFalse(loginViewModel.mLoginUiModel.value!!.isSuccess)
        Assert.assertEquals(loginViewModel.mLoginUiModel.value!!.errorMsg, "Required field missing.")
    }

    @After
    fun tearUp() {
        mTestRepoMock.close()
    }
}