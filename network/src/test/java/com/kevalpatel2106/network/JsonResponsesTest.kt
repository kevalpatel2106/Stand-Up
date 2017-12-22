package com.kevalpatel2106.network

import com.kevalpatel2106.testutils.MockServerManager
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.nio.file.Paths


/**
 * Created by Keval on 12/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

@RunWith(JUnit4::class)
class JsonResponsesTest {
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/kevalpatel2106/network/responses", Paths.get("").toAbsolutePath().toString())

    companion object {

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            ApiProvider.init()
        }


        @JvmStatic
        @AfterClass
        fun tearUpClass() {
            ApiProvider.close()
        }
    }

    private val mockWebServer = MockServerManager()

    @Before
    fun setUp() {
        mockWebServer.startMockWebServer()
    }

    @After
    fun tearUp() {
        mockWebServer.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkFieldMissingResponse() {
        mockWebServer.enqueueResponse(mockWebServer
                .getStringFromFile(File(RESPONSE_DIR_PATH + "/required_field_missing_sample.json")))

        val response = ApiProvider.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBase()
                .execute()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), APIStatusCodes.ERROR_CODE_REQUIRED_FIELD_MISSING)
        Assert.assertEquals(response.message(), "Username is missing.")
    }

    @Test
    @Throws(IOException::class)
    fun checkServerExceptionResponse() {
        mockWebServer.enqueueResponse(mockWebServer
                .getStringFromFile(File(RESPONSE_DIR_PATH + "/exception_sample.json")))

        val response = ApiProvider.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBase()
                .execute()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), APIStatusCodes.ERROR_CODE_EXCEPTION)
        Assert.assertEquals(response.message(), NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
    }


    @Test
    @Throws(IOException::class)
    fun checkSuccessResponse() {
        mockWebServer.enqueueResponse(mockWebServer
                .getStringFromFile(File(RESPONSE_DIR_PATH + "/sucess_sample.json")))

        val response = ApiProvider.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBase()
                .execute()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_OK)
        Assert.assertEquals(response.message(), "OK")
    }
}
