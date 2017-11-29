package com.kevalpatel2106.network

import com.kevalpatel2106.testutils.MockWebserverUtils
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection

/**
 * Created by Keval on 12/11/17.
 * Test class for [NWInterceptor]. Make sure you are connected to the internet before runnning this
 * tests.
 *
 * @author []https://github.com/kevalpatel2106]
 */
@RunWith(JUnit4::class)
class NWInterceptorTest {
    private val RESPONSE_DIR_PATH = String.format("%s/network/src/test/java/com/kevalpatel2106/network/responses", File(File("").absolutePath))

    @Before
    fun setUp() {
        ApiProvider.init()
    }

    @Test
    @Throws(IOException::class)
    fun checkAddCachingHeaders() {
        val request = Request.Builder()
                .url("http://example.com")
                .addHeader("Cache-Time", "5000")
                .build()
        val response = Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(0)
                .message("This is test request.")
                .build()
        val modifiedResponse = NWInterceptor(null)
                .addCachingHeaders(request, response)

        Assert.assertNull(modifiedResponse.headers().get("Cache-Time"))
        Assert.assertNotNull(modifiedResponse.headers().get("Cache-Control"))
        Assert.assertEquals(modifiedResponse.headers().get("Cache-Control"), "public, max-age=5000")
    }

    @Test
    @Throws(IOException::class)
    fun checkNoCacheHeader() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(File(RESPONSE_DIR_PATH + "/sucess_sample.json"))))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBaseWithoutCache()
                .execute()
        mockWebServer.shutdown()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertNull(response.headers().get("Cache-Control"))
    }

    @Test
    @Throws(IOException::class)
    fun checkCacheHeader() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(File(RESPONSE_DIR_PATH + "/sucess_sample.json"))))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBaseWithCache()
                .execute()
        mockWebServer.shutdown()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertNotNull(response.headers().get("Cache-Control"))
        Assert.assertEquals(response.headers().get("Cache-Control"), "public, max-age=5000")
    }

    @Test
    @Throws(IOException::class)
    fun checkNoAuthHeader() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(File(RESPONSE_DIR_PATH + "/sucess_sample.json"))))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBaseWithoutAuthHeader()
                .execute()
        mockWebServer.shutdown()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertNull(response.raw().request().headers().get("Authorization"))
    }
}
