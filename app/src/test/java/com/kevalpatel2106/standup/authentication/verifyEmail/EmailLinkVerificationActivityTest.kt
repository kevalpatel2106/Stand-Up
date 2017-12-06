package com.kevalpatel2106.standup.authentication.verifyEmail

import com.kevalpatel2106.standup.BuildConfig
import com.kevalpatel2106.standup.UnitTestUtils
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.testutils.MockServerManager
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 27-Nov-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class EmailLinkVerificationActivityTest {
    private val RESPONSE_DIR_PATH = String.format("%s/app/src/test/java/com/kevalpatel2106/standup/authentication/repo", File(File("").absolutePath))
    private val EMAIL_VERIFICATION_LINK = BuildConfig.BASE_URL + "/verifyEmailLink/32894723874/dskfhj-sdf-vcx-cx-vczx"

    private val mockServerManager = MockServerManager()
    private lateinit var mTestRepoMock: UserAuthRepositoryImpl

    companion object {

        @JvmStatic
        @BeforeClass
        fun setGlobal() = UnitTestUtils.initApp()
    }

    @Before
    fun setUp() {
        //Set the repo
        mockServerManager.startMockWebServer()
        mTestRepoMock = UserAuthRepositoryImpl(mockServerManager.getBaseUrl())
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkVerifyEmail() {
        mockServerManager.enqueueResponse(response = File(RESPONSE_DIR_PATH + "/email_verify_success.html"),
                type = "text/html")

        mTestRepoMock.verifyEmailLink(EMAIL_VERIFICATION_LINK)
                .subscribe({
                    //Success
                    Assert.assertTrue(it.contains("Your email has been verified."))
                }, {
                    //Error
                    Assert.fail(it.message)
                })
    }
}