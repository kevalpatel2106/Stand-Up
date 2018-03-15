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
import com.kevalpatel2106.network.TestApiService
import com.kevalpatel2106.testutils.MockServerManager
import org.junit.After
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
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

@RunWith(JUnit4::class)
class JsonResponsesTest {
    private lateinit var mNetworkApi: NetworkApi
    private val mockWebServer = MockServerManager()

    @Before
    fun setUp() {
        mockWebServer.startMockWebServer()
        mNetworkApi = NetworkApi()
    }

    @After
    fun tearUp() {
        mockWebServer.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkFieldMissingResponse() {
        mockWebServer.enqueueResponse(File(mockWebServer.getResponsesPath() + "/required_field_missing_sample.json"))

        val response = mNetworkApi.getRetrofitClient(mockWebServer.getBaseUrl())
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
        mockWebServer.enqueueResponse(File(mockWebServer.getResponsesPath() + "/sample_network_exception.json"))

        val response = mNetworkApi.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBase()
                .execute()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), APIStatusCodes.ERROR_CODE_EXCEPTION)
        Assert.assertEquals(response.message(), NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
    }

    @Test
    @Throws(IOException::class)
    fun checkServerUnAuthorisedResponse() {
        mockWebServer.enqueueResponse(File(mockWebServer.getResponsesPath() + "/unauthorised_sample.json"))

        val response = mNetworkApi.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBase()
                .execute()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(response.code(), APIStatusCodes.ERROR_CODE_UNAUTHORIZED)
        Assert.assertEquals(response.message(), "Something went wrong.")
    }


    @Test
    @Throws(IOException::class)
    fun checkSuccessResponse() {
        mockWebServer.enqueueResponse(File(mockWebServer.getResponsesPath() + "/sucess_sample.json"))

        val response = mNetworkApi.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBase()
                .execute()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_OK)
        Assert.assertEquals(response.message(), "OK")
    }

    @Test
    @Throws(IOException::class)
    fun checkSuccessResponseWithoutData() {
        mockWebServer.enqueueResponse(File(mockWebServer.getResponsesPath() + "/success_sample_without_data.json"))

        val response = mNetworkApi.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBase()
                .execute()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_OK)
        Assert.assertEquals(response.message(), "OK")
    }
}
