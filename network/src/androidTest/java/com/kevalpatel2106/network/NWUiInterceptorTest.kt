package com.kevalpatel2106.network

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.network.consumer.NWErrorConsumer
import com.kevalpatel2106.testutils.MockWebserverUtils
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import io.reactivex.functions.Consumer
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import org.apache.commons.codec.binary.Base64
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.net.HttpURLConnection

/**
 * Created by Keval on 12/11/17.
 * Test class for [NWInterceptor]. Make sure you are connected to the internet before runnning this
 * tests.
 *
 * @author []https://github.com/kevalpatel2106]
 */
@RunWith(AndroidJUnit4::class)
class NWUiInterceptorTest {
    @Before
    fun setUp() {
        ApiProvider.init(InstrumentationRegistry.getContext())

        SharedPrefsProvider.init(InstrumentationRegistry.getContext().applicationContext)
        UserSessionManager.clearUserSession()
        UserSessionManager.setNewSession(124,
                "TestUser",
                "test@example.com",
                "TestToken",
                null,
                true)
    }

    @Test
    @Throws(IOException::class)
    fun checkAddAuthHeader() {
        val request = Request.Builder()
                .url("http://example.com")
                .addHeader("Add-Auth", "true")
                .build()

        val modifiedRequest = NWInterceptor(null).addAuthHeader(request)

        Assert.assertNull(modifiedRequest.headers().get("Add-Auth"))

        Assert.assertNotNull(modifiedRequest.headers().get("Authorization"))
        Assert.assertEquals(modifiedRequest.headers().get("Authorization"),
                "Basic " + String(
                        Base64.encodeBase64((UserSessionManager.userId.toString()
                                + ":" + UserSessionManager.token).toByteArray())))
    }


    @Test
    @Throws(IOException::class)
    fun checkAuthHeader() {
        val mockWebServer = MockWebserverUtils.startMockWebServer()
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setHeader("Content-Type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(InstrumentationRegistry.getContext(),
                        com.kevalpatel2106.network.test.R.raw.sucess_sample)))

        ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UiTestApiService::class.java)
                .callBaseWithAuthHeader()
                .subscribe(Consumer<retrofit2.Response<TestData>> {

                    Assert.assertNotNull(it.raw().request().headers().get("Authorization"))
                    Assert.assertEquals(it.raw().request().headers().get("Authorization"),
                            "Basic " + String(
                                    Base64.encodeBase64((UserSessionManager.userId.toString()
                                            + ":" + UserSessionManager.token).toByteArray())))
                }, object : NWErrorConsumer() {
                    override fun onError(code: Int, message: String) {
                        Assert.fail("There shouldn't be error. Error code: " + code)
                    }

                    override fun onInternetUnavailable(message: String) {
                        Assert.fail("Internet is there")
                    }
                })

    }
}