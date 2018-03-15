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

package com.kevalpatel2106.network.repository.refresher

import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.network.SuccessSampleData
import com.kevalpatel2106.network.TestApiService
import com.kevalpatel2106.network.repository.SourceType
import com.kevalpatel2106.network.retrofit.NetworkConfig
import com.kevalpatel2106.testutils.MockServerManager
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Call
import java.io.File

/**
 * Created by Kevalpatel2106 on 15-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class RetrofitNetworkRefresherTest {

    private lateinit var networkApi: NetworkApi
    private lateinit var mockServerManager: MockServerManager
    private lateinit var call: Call<SuccessSampleData>

    @Before
    fun setUp() {
        mockServerManager = MockServerManager()
        mockServerManager.startMockWebServer()

        networkApi = NetworkApi()

        call = networkApi.getRetrofitClient(mockServerManager.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithSuccessSample()
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    @Throws(Exception::class)
    fun checkRead_WithSuccessResponse() {
        mockServerManager.enqueueResponse(File(mockServerManager.getResponsesPath()
                + "/sucess_sample.json"))

        val repoData = RetrofitNetworkRefresher(call).read()

        Assert.assertEquals(SourceType.NETWORK, repoData.source)
        Assert.assertFalse(repoData.isError)
        Assert.assertNull(repoData.errorMessage)
        Assert.assertNotNull(repoData.data)
        Assert.assertEquals(1023 /* From the response json */, repoData.data!!.uid)
    }


    @Test
    @Throws(Exception::class)
    fun checkRead_WithErrorResponse() {
        mockServerManager.enqueueResponse(File(mockServerManager.getResponsesPath()
                + "/authentication_field_missing.json"))

        val repoData = RetrofitNetworkRefresher(call).read()

        Assert.assertEquals(SourceType.NETWORK, repoData.source)

        Assert.assertTrue(repoData.isError)
        Assert.assertEquals("Required field missing." /* From json response */, repoData.errorMessage)

        Assert.assertNull(repoData.data)
    }

    @Test
    @Throws(Exception::class)
    fun checkRead_WithInvalidResponse() {
        mockServerManager.enqueueResponse(File(mockServerManager.getResponsesPath()
                + "/empty_sample.json"))

        val repoData = RetrofitNetworkRefresher(call).read()

        Assert.assertEquals(SourceType.NETWORK, repoData.source)

        Assert.assertTrue(repoData.isError)
        Assert.assertEquals(NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG, repoData.errorMessage)

        Assert.assertNull(repoData.data)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareError_WithErrorMessageInResponse() {
        mockServerManager.enqueueResponse(File(mockServerManager.getResponsesPath()
                + "/authentication_field_missing.json"))

        val retrofitNetworkRefresher = RetrofitNetworkRefresher(call)
        val errorMsg = retrofitNetworkRefresher.parseErrorBody(500, call.execute())

        Assert.assertEquals("Required field missing." /* From json response */, errorMsg)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareError_WithoutErrorMessageInResponse() {
        mockServerManager.enqueueResponse(File(mockServerManager.getResponsesPath()
                + "/empty_sample.json"))

        val retrofitNetworkRefresher = RetrofitNetworkRefresher(call)
        val errorMsg = retrofitNetworkRefresher.parseErrorBody(0, call.execute())

        Assert.assertEquals(NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG, errorMsg)
    }
}
