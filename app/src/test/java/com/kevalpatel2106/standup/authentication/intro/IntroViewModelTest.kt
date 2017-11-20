package com.kevalpatel2106.standup.authentication.intro

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.support.test.InstrumentationRegistry
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.authentication.repo.MockUserAuthRepository
import com.kevalpatel2106.standup.authentication.signUp.SignUpRequest
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File


/**
 * Created by Keval on 20/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class IntroViewModelTest {
    private val RESPONSE_DIR_PATH = String.format("%s/app/src/test/java/com/kevalpatel2106/standup/authentication/repo", File(File("").absolutePath))

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var introViewModel: IntroViewModel
    private lateinit var mTestRepoMock: MockUserAuthRepository

    @Before
    fun setUp() {
        //Init server
        ApiProvider.init()

        mTestRepoMock = MockUserAuthRepository()
        introViewModel = IntroViewModel(mTestRepoMock)
    }

    @Test
    fun checkInitialization() {
        Assert.assertFalse(introViewModel.mIsAuthenticationRunning.value!!)
    }

    @Test
    fun checkAuthenticateSocialUserSuccess() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/social_user_sign_up_success.json"))

        //Make the api call to the mock server
        val signInRequest = SignUpRequest("test@example.com", "Test User", null, null);
        introViewModel.authenticateSocialUser(signInRequest)

        //There should be success.
        Assert.assertFalse(introViewModel.mIsAuthenticationRunning.value!!)
        Assert.assertTrue(introViewModel.mIntroApiResponse.value!!.isSuccess)
        Assert.assertEquals(introViewModel.mIntroApiResponse.value!!.signUpResponseData!!.uid, 5629499534213120)
    }

    @Test
    fun checkAuthenticateSocialUserFieldMissing() {
        mTestRepoMock.enqueueResponse(File(RESPONSE_DIR_PATH + "/social_user_field_missing.json"))

        //Make the api call to the mock server
        introViewModel.authenticateSocialUser(SignUpRequest("test@example.com", "Test User", null, null))

        //There should be success.
        Assert.assertFalse(introViewModel.mIsAuthenticationRunning.value!!)
        Assert.assertFalse(introViewModel.mIntroApiResponse.value!!.isSuccess)
        Assert.assertEquals(introViewModel.mIntroApiResponse.value!!.errorMsg, "Required field missing.")
    }

    @After
    fun tearUp() {
        mTestRepoMock.close()
    }
}