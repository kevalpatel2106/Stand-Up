package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.testutils.MockWebserverUtils
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Keval on 20/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class UserAuthRepositoryTest {
    private lateinit var introViewModel: UserAuthRepositoryTestImpl
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebserverUtils.startMockWebServer()
        introViewModel = UserAuthRepositoryTestImpl(MockWebserverUtils.getBaseUrl(mockWebServer))
    }

    @Test
    fun checkAuthSocialUser() {
        //TODO write test
    }

    @After
    fun tearUp() {
        mockWebServer.close()
    }
}