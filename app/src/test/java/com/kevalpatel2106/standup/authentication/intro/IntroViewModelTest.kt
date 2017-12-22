package com.kevalpatel2106.standup.authentication.intro

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthUser
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.UnitTestUtils
import com.kevalpatel2106.standup.authentication.repo.SignUpRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException
import java.nio.file.Paths


/**
 * Created by Keval on 20/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class IntroViewModelTest {
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/kevalpatel2106/standup/authentication/repo", Paths.get("").toAbsolutePath().toString())

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var introViewModel: IntroViewModel
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
        introViewModel = IntroViewModel(UserAuthRepositoryImpl(mockServerManager.getBaseUrl()))
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }


    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(introViewModel.blockUi.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateSocialUserSignUpSuccess() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/social_user_sign_up_success.json"))

        //Make the api call to the mock server
        val signInRequest = SignUpRequest("test@example.com", "Test User", null, null)
        introViewModel.authenticateSocialUser(signInRequest)

        //There should be success.
        Assert.assertFalse(introViewModel.blockUi.value!!)
        Assert.assertNull(introViewModel.errorMessage.value)
        Assert.assertTrue(introViewModel.mIntroUiModel.value!!.isSuccess)
        Assert.assertTrue(introViewModel.mIntroUiModel.value!!.isNewUser)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateSocialUserLoginSuccess() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/social_user_login_success.json"))

        //Make the api call to the mock server
        val signInRequest = SignUpRequest("test@example.com", "Test User", null, null)
        introViewModel.authenticateSocialUser(signInRequest)

        //There should be success.
        Assert.assertFalse(introViewModel.blockUi.value!!)
        Assert.assertNull(introViewModel.errorMessage.value)
        Assert.assertTrue(introViewModel.mIntroUiModel.value!!.isSuccess)
        Assert.assertFalse(introViewModel.mIntroUiModel.value!!.isNewUser)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateSocialUserFieldMissing() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        //Make the api call to the mock server
        introViewModel.authenticateSocialUser(SignUpRequest("test@example.com", "Test User", null, null))

        //There should be success.
        Thread.sleep(2000)
        Assert.assertFalse(introViewModel.blockUi.value!!)
        Assert.assertFalse(introViewModel.mIntroUiModel.value!!.isSuccess)
        Assert.assertEquals(introViewModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateGoogleUserWithoutEmail() {
        val googleAuthUser = GoogleAuthUser("46753782367943")
        googleAuthUser.email = ""
        googleAuthUser.name = ""
        introViewModel.authenticateSocialUser(googleAuthUser)

        //There should be success.
        Assert.assertEquals(introViewModel.errorMessage.value!!.errorRes, R.string.error_google_login_email_not_found)
        Assert.assertFalse(introViewModel.blockUi.value!!)
    }

    @Test
    @Throws(IOException::class)
    fun checkAuthenticateFbUserWithoutEmail() {
        val fbUser = FacebookUser("46753782367943")
        introViewModel.authenticateSocialUser(fbUser)

        //There should be success.
        Assert.assertEquals(introViewModel.errorMessage.value!!.errorRes, R.string.error_fb_login_email_not_found)
        Assert.assertFalse(introViewModel.blockUi.value!!)
    }
}