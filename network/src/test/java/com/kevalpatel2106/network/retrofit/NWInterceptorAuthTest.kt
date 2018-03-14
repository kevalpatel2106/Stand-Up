/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.kevalpatel2106.network.retrofit

import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import okhttp3.Request
import org.apache.commons.codec.binary.Base64
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException

/**
 * Created by Keval on 12/11/17.
 * Test class for [NWInterceptor]. Make sure you are connected to the internet before runnning this
 * tests.
 *
 * @author []https://github.com/kevalpatel2106]
 */
@RunWith(JUnit4::class)
class NWInterceptorAuthTest {
    private val TEST_PREF_STRING = "TestValue"
    private val TEST_PREF_STRING_2 = "TestValue2"

    private val mockWebServer = MockServerManager()
    private lateinit var mNetworkApi: NetworkApi

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
    fun checkAddAuthHeader() {
        mNetworkApi = NetworkApi(TEST_PREF_STRING, TEST_PREF_STRING_2)

        val request = Request.Builder()
                .url("http://example.com")
                .addHeader("Add-Auth", "true")
                .build()

        val modifiedRequest = NWInterceptor(null, "test-user-id", "test-token")
                .addAuthHeader(request)

        Assert.assertNull(modifiedRequest.headers().get("Add-Auth"))

        Assert.assertNotNull(modifiedRequest.headers().get("Authorization"))
        Assert.assertEquals(modifiedRequest.headers().get("Authorization"),
                "Basic " + String(Base64.encodeBase64(("test-user-id"
                        + ":" + "test-token").toByteArray())))
    }

    @Test
    @Throws(IOException::class)
    fun checkApiRequestWithAuthHeader() {
        mNetworkApi = NetworkApi(TEST_PREF_STRING, TEST_PREF_STRING_2)

        mockWebServer.enqueueResponse(File(mockWebServer.getResponsesPath() + "/sucess_sample.json"))

        val response = mNetworkApi.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithAuthHeader()
                .execute()

        Assert.assertNotNull(response.raw().request().headers().get("Authorization"))
        Assert.assertEquals(response.raw().request().headers().get("Authorization"),
                "Basic " + String(Base64.encodeBase64((TEST_PREF_STRING
                        + ":" + TEST_PREF_STRING_2).toByteArray())))
    }

    @Test
    @Throws(IOException::class)
    fun checkApiRequestWithoutUserId() {
        mNetworkApi = NetworkApi(null, TEST_PREF_STRING_2)

        mockWebServer.enqueueResponse(File(mockWebServer.getResponsesPath() + "/sucess_sample.json"))

        val response = mNetworkApi.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithAuthHeader()
                .execute()

        Assert.assertNull(response.raw().request().headers().get("Authorization"))
    }

    @Test
    @Throws(IOException::class)
    fun checkApiRequestWithoutToken() {
        mNetworkApi = NetworkApi(TEST_PREF_STRING_2, null)

        mockWebServer.enqueueResponse(File(mockWebServer.getResponsesPath() + "/sucess_sample.json"))

        val response = mNetworkApi.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithAuthHeader()
                .execute()

        Assert.assertNull(response.raw().request().headers().get("Authorization"))
    }

    @Test
    @Throws(IOException::class)
    fun checkApiRequestWithoutAuthHeader() {
        mNetworkApi = NetworkApi(TEST_PREF_STRING, TEST_PREF_STRING_2)

        mockWebServer.enqueueResponse(File(mockWebServer.getResponsesPath() + "/sucess_sample.json"))

        val response = mNetworkApi.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithoutAuthHeader()
                .execute()

        Assert.assertNull(response.raw().request().headers().get("Authorization"))
    }

    @Test
    @Throws(IOException::class)
    fun checkApiWithNullUserSession() {
        mNetworkApi = NetworkApi(TEST_PREF_STRING, TEST_PREF_STRING_2)

        mockWebServer.enqueueResponse(File(mockWebServer.getResponsesPath() + "/sucess_sample.json"))

        mNetworkApi = NetworkApi(null, null)
        val response = mNetworkApi.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithAuthHeader()
                .execute()

        Assert.assertNull(response.raw().request().headers().get("Authorization"))
    }
}
