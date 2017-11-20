package com.kevalpatel2106.standup.authentication.intro

import com.kevalpatel2106.standup.authentication.repo.MockUserAuthRepository
import com.kevalpatel2106.standup.authentication.signUp.SignUpRequest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
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
    private lateinit var introViewModel: IntroViewModel
    private lateinit var mTestRepoMock: MockUserAuthRepository

    @Before
    fun setUp() {
        mTestRepoMock = MockUserAuthRepository()
        introViewModel = IntroViewModel()
    }

    @Test
    fun checkInitialization() {
        Assert.assertFalse(introViewModel.mIsAuthenticationRunning.value!!)
    }

    @Test
    fun checkAuthenticateSocialUserSuccess() {
        mTestRepoMock.enqueueResponse(File(File("").absolutePath + "/social_user_login_success.json"))

        //Make the api call to the mock server
        introViewModel.authenticateSocialUser(SignUpRequest("test@example.com", "Test User", null, null))

        //There should be success.
        Assert.assertFalse(introViewModel.mIsAuthenticationRunning.value!!)
        Assert.assertTrue(introViewModel.mIntroApiResponse.value!!.isSuccess)
        Assert.assertEquals(introViewModel.mIntroApiResponse.value!!.name, "Test User")
    }

    @Test
    fun checkAuthenticateSocialUserFieldMissing() {
        mTestRepoMock.enqueueResponse(File(File("").absolutePath + "/social_user_field_missing.json"))

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