package com.kevalpatel2106.standup.authentication.intro

import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryTestImpl
import org.junit.After
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
    private lateinit var testRepo: UserAuthRepositoryTestImpl

    @Before
    fun setUp() {
        testRepo = UserAuthRepositoryTestImpl()
        introViewModel = IntroViewModel()
    }

    @Test
    fun checkAuthenticateSocialUser() {
        testRepo.enqueueResponse(File(File("").absolutePath + "/social_user_login_success.json"))

        //TODO write test
    }

    @After
    fun tearUp() {
        testRepo.close()
    }
}