package com.kevalpatel2106.network

import com.kevalpatel2106.testutils.MockWebserverUtils
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection


/**
 * Created by Keval on 12/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

@RunWith(JUnit4::class)
class ServerErrorCodeTests {
    private val RESPONSE_DIR_PATH = String.format("%s/network/src/test/java/com/kevalpatel2106/network/responses", File(File("").absolutePath))

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

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebserverUtils.startMockWebServer()
    }

    @After
    fun tearUp() {
        mockWebServer.shutdown()
    }

    @Test
    @Throws(IOException::class)
    fun checkPageNotFound() {
        //404 response
        mockWebServer.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()
        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_NOT_FOUND)
        Assert.assertEquals(response.message(), NetworkConfig.ERROR_MESSAGE_NOT_FOUND)
    }

    @Test
    @Throws(IOException::class)
    fun checkUnAuthorise() {
        //404 response
        mockWebServer.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_UNAUTHORIZED)
    }

    @Test
    @Throws(IOException::class)
    fun checkBadRequest() {
        //400 response
        mockWebServer.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_BAD_REQUEST)
        Assert.assertEquals(response.message(), NetworkConfig.ERROR_MESSAGE_BAD_REQUEST)
    }

    @Suppress("DEPRECATION")
    @Test
    @Throws(IOException::class)
    fun checkServerBusy() {
        //500 response
        mockWebServer.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_SERVER_ERROR))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_SERVER_ERROR)
        Assert.assertEquals(response.message(), NetworkConfig.ERROR_MESSAGE_SERVER_BUSY)
    }

    @Test
    @Throws(IOException::class)
    fun checkUnknownResponseCode() {
        mockWebServer.enqueue(MockResponse().setResponseCode(103))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), 103)
        Assert.assertEquals(response.message(), NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
    }
}
