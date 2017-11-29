package com.kevalpatel2106.network

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.kevalpatel2106.testutils.MockWebserverUtils
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection


/**
 * Created by Keval on 12/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

@RunWith(JUnit4::class)
class ApiProviderTest {
    private val RESPONSE_DIR_PATH = String.format("%s/network/src/test/java/com/kevalpatel2106/network/responses", File(File("").absolutePath))

    @Before
    fun setUp() {
        ApiProvider.init()
    }

    @Test
    @Throws(IOException::class)
    fun checkBaseUrl() {
        val retrofit = ApiProvider.getRetrofitClient("http://google.com")
        Assert.assertEquals(retrofit.baseUrl().toString(), "http://google.com/")
    }

    @Test
    @Throws(IOException::class)
    fun checkOkHttpClient() {
        val context = Mockito.mock(Context::class.java)
        ApiProvider.init(context)
        val okHttpClient = ApiProvider.sOkHttpClient

        if (BuildConfig.DEBUG) {
            /**
             * For Debug there will be three interceptors.
             *
             * 1. [HttpLoggingInterceptor]
             * 2. [StethoInterceptor]
             * 3. [NWInterceptor]
             */
            Assert.assertEquals(okHttpClient.interceptors().size, 4)
        } else {
            /**
             * For Release there will be three interceptors.
             *
             * 1. [NWInterceptor]
             */
            Assert.assertEquals(okHttpClient.interceptors().size, 2)
        }

        Assert.assertEquals(okHttpClient.readTimeoutMillis().toLong(), NetworkConfig.READ_TIMEOUT * 60 * 1000)
        Assert.assertEquals(okHttpClient.writeTimeoutMillis().toLong(), NetworkConfig.WRITE_TIMEOUT * 60 * 1000)
        Assert.assertEquals(okHttpClient.connectTimeoutMillis().toLong(), NetworkConfig.CONNECTION_TIMEOUT * 60 * 1000)
    }

    @Test
    @Throws(IOException::class)
    fun checkPageNotFound() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        //404 response
        mockWebServer.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()
        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_NOT_FOUND)
        Assert.assertEquals(response.message(), NetworkConfig.ERROR_MESSAGE_NOT_FOUND)
        mockWebServer.shutdown()
    }

    @Test
    @Throws(IOException::class)
    fun checkUnAuthorise() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        //404 response
        mockWebServer.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()
        mockWebServer.shutdown()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_UNAUTHORIZED)
    }

    @Test
    @Throws(IOException::class)
    fun checkBadRequest() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        //400 response
        mockWebServer.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()
        mockWebServer.shutdown()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_BAD_REQUEST)
        Assert.assertEquals(response.message(), NetworkConfig.ERROR_MESSAGE_BAD_REQUEST)
    }

    @Suppress("DEPRECATION")
    @Test
    @Throws(IOException::class)
    fun checkServerBusy() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        //500 response
        mockWebServer.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_SERVER_ERROR))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()
        mockWebServer.shutdown()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_SERVER_ERROR)
        Assert.assertEquals(response.message(), NetworkConfig.ERROR_MESSAGE_SERVER_BUSY)
    }

    @Test
    @Throws(IOException::class)
    fun checkUnknownResponseCode() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse().setResponseCode(103))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()
        mockWebServer.shutdown()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), 103)
        Assert.assertEquals(response.message(), NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
    }

    @Test
    @Throws(IOException::class)
    fun checkSuccessResponse() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(File(RESPONSE_DIR_PATH + "/sucess_sample.json"))))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()
        mockWebServer.shutdown()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_OK)
        Assert.assertNull(response.message())
    }

    @Test
    @Throws(IOException::class)
    fun checkFieldMissingResponse() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(File(RESPONSE_DIR_PATH + "/required_field_missing_sample.json"))))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()
        mockWebServer.shutdown()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), APIStatusCodes.ERROR_CODE_REQUIRED_FIELD_MISSING)
        Assert.assertEquals(response.message(), "Username is missing.")
    }

    @Test
    @Throws(IOException::class)
    fun checkServerExceptionResponse() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(File(RESPONSE_DIR_PATH + "/exception_sample.json"))))

        val response = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBase()
                .execute()
        mockWebServer.shutdown()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), APIStatusCodes.ERROR_CODE_EXCEPTION)
        Assert.assertEquals(response.message(), NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
    }
}
