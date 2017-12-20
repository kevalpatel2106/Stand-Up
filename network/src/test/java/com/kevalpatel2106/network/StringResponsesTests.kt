package com.kevalpatel2106.network

import com.kevalpatel2106.testutils.MockServerManager
import okhttp3.mockwebserver.MockResponse
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
class StringResponsesTests {
    private val RESPONSE_DIR_PATH = String.format("%s/network/src/test/java/com/kevalpatel2106/network/responses", Paths.get(".").toAbsolutePath().toString())

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

    private val mockServerManager = MockServerManager()

    @Before
    fun setUp() {
        mockServerManager.startMockWebServer()
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkForTheStringSuccessResponse() {
        mockServerManager.enqueueResponse(mockServerManager
                .getStringFromFile(File(RESPONSE_DIR_PATH + "/sucess_sample.json")), "text/plain")

        val response = ApiProvider.getRetrofitClient(mockServerManager.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseString()
                .execute()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_OK)
        Assert.assertEquals(response.body(),
                mockServerManager.getStringFromFile(File(RESPONSE_DIR_PATH + "/sucess_sample.json")))
        Assert.assertEquals(response.message(), "OK")
    }

    @Test
    @Throws(IOException::class)
    fun checkForTheStringFailResponse() {
        mockServerManager.mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
                .setHeader("Content-Type", "text/html")
                .setBody(mockServerManager.getStringFromFile(File(RESPONSE_DIR_PATH + "/sucess_sample.json"))))

        val response = ApiProvider.getRetrofitClient(mockServerManager.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseString()
                .execute()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_FORBIDDEN)
    }
}
