package com.kevalpatel2106.network

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Base64
import com.kevalpatel2106.network.consumer.NWErrorConsumer
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.MockWebserverUtils
import io.reactivex.functions.Consumer
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection

/**
 * Created by Keval on 12/11/17.
 * Test class for [NWInterceptor]. Make sure you are connected to the internet before runnning this
 * tests.
 *
 * @author []https://github.com/kevalpatel2106]
 */
@RunWith(AndroidJUnit4::class)
class NWInterceptorTest : BaseTestClass() {

    @Test
    fun checkAddAuthHeader() {
        val request = Request.Builder()
                .url("http://example.com")
                .addHeader("Username", "TestUserName")
                .addHeader("Token", "TestToken")
                .build()

        val modifiedRequest = NWInterceptor(InstrumentationRegistry.getContext())
                .addAuthHeader(request)

        Assert.assertNull(modifiedRequest.headers().get("Username"))
        Assert.assertNull(modifiedRequest.headers().get("Token"))

        Assert.assertNotNull(modifiedRequest.headers().get("Authorization"))
        Assert.assertEquals(modifiedRequest.headers().get("Authorization"),
                "Basic " + Base64.encodeToString(("TestUserName" + ":" + "TestToken").toByteArray(),
                        Base64.NO_WRAP))
    }

    @Test
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
        val modifiedResponse = NWInterceptor(InstrumentationRegistry.getContext())
                .addCachingHeaders(request, response)

        Assert.assertNull(modifiedResponse.headers().get("Cache-Time"))
        Assert.assertNotNull(modifiedResponse.headers().get("Cache-Control"))
        Assert.assertEquals(modifiedResponse.headers().get("Cache-Control"), "public, max-age=5000")
    }

    @Test
    fun checkNoCacheHeader() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                        com.kevalpatel2106.network.test.R.raw.sucess_sample)))

        ApiProvider(InstrumentationRegistry.getContext())
                .getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBaseWithoutCache()
                .subscribe(Consumer<retrofit2.Response<TestData>> {
                    Assert.assertNull(it.headers().get("Cache-Control"))
                }, object : NWErrorConsumer() {

                    override fun onError(code: Int, message: String) {
                        Assert.fail("There shouldn't be error. Error code: " + code)
                    }

                    override fun onInternetUnavailable(message: String) {
                        Assert.fail("Internet is there")
                    }
                })

    }

    @Test
    fun checkCacheHeader() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                        com.kevalpatel2106.network.test.R.raw.sucess_sample)))

        ApiProvider(InstrumentationRegistry.getContext())
                .getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBaseWithCache()
                .subscribe(Consumer<retrofit2.Response<TestData>> {
                    Assert.assertNotNull(it.headers().get("Cache-Control"))
                    Assert.assertEquals(it.headers().get("Cache-Control"), "public, max-age=5000")
                }, object : NWErrorConsumer() {

                    override fun onError(code: Int, message: String) {
                        Assert.fail("There shouldn't be error. Error code: " + code)
                    }

                    override fun onInternetUnavailable(message: String) {
                        Assert.fail("Internet is there")
                    }
                })
    }

    @Test
    fun checkNoAuthHeader() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                        com.kevalpatel2106.network.test.R.raw.sucess_sample)))

        ApiProvider(InstrumentationRegistry.getContext())
                .getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBaseWithoutAuthHeader()
                .subscribe(Consumer<retrofit2.Response<TestData>> {
                    Assert.assertNull(it.raw().request().headers().get("Authorization"))
                }, object : NWErrorConsumer() {

                    override fun onError(code: Int, message: String) {
                        Assert.fail("There shouldn't be error. Error code: " + code)
                    }

                    override fun onInternetUnavailable(message: String) {
                        Assert.fail("Internet is there")
                    }
                })

    }

    @Test
    fun checkAuthHeader() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                        com.kevalpatel2106.network.test.R.raw.sucess_sample)))

        ApiProvider(InstrumentationRegistry.getContext())
                .getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(TestApiService::class.java)
                .callBaseWithAuthHeader()
                .subscribe(Consumer<retrofit2.Response<TestData>> {
                    Assert.assertNotNull(it.raw().request().headers().get("Authorization"))
                    Assert.assertEquals(it.raw().request().headers().get("Authorization"),
                            "Basic " + Base64.encodeToString(("TestUserName" + ":" + "TestToken").toByteArray(),
                                    Base64.NO_WRAP))
                }, object : NWErrorConsumer() {
                    override fun onError(code: Int, message: String) {
                        Assert.fail("There shouldn't be error. Error code: " + code)
                    }

                    override fun onInternetUnavailable(message: String) {
                        Assert.fail("Internet is there")
                    }
                })

    }

    override fun getActivity(): Activity? = null
}
