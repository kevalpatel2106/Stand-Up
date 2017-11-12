package com.kevalpatel2106.network

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.network.consumer.NWErrorConsumer
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.MockWebserverUtils
import io.reactivex.functions.Consumer
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection

/**
 * Created by Keval on 12/11/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class NWInterceptorTest : BaseTestClass() {

    @Test
    fun checkNoCacheHeader() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                        com.kevalpatel2106.network.test.R.raw.sucess_sample)))

        BaseApiWrapper(InstrumentationRegistry.getContext())
                .getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer),
                        10000, "TestUserName", "TestToken")
                .create(TestApiService::class.java)
                .callBaseWithoutAuthHeader()
                .subscribe(Consumer<retrofit2.Response<TestData>> {
                    Assert.assertNotNull(it.headers().get("Cache-Control"))
                }, object : NWErrorConsumer() {

                    override fun onError(code: Int, message: String) {
                        Assert.assertEquals(code, HttpURLConnection.HTTP_NOT_FOUND)
                        Assert.assertEquals(message, APIStatusCodes.ERROR_MESSAGE_NOT_FOUND)
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

        BaseApiWrapper(InstrumentationRegistry.getContext())
                .getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer),
                        0, "TestUserName", "TestToken")
                .create(TestApiService::class.java)
                .callBaseWithoutAuthHeader()
                .subscribe(Consumer<retrofit2.Response<TestData>> {
                    Assert.assertNull(it.headers().get("Authentication"))
                }, object : NWErrorConsumer() {

                    override fun onError(code: Int, message: String) {
                        Assert.assertEquals(code, HttpURLConnection.HTTP_NOT_FOUND)
                        Assert.assertEquals(message, APIStatusCodes.ERROR_MESSAGE_NOT_FOUND)
                    }

                    override fun onInternetUnavailable(message: String) {
                        Assert.fail("Internet is there")
                    }
                })

    }

    override fun getActivity(): Activity? {
        return null
    }
}
