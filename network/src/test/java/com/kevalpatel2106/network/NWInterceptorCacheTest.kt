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

package com.kevalpatel2106.network

import com.kevalpatel2106.testutils.MockServerManager
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException
import java.nio.file.Paths

/**
 * Created by Keval on 12/11/17.
 * Test class for [NWInterceptor]. Make sure you are connected to the internet before runnning this
 * tests.
 *
 * @author []https://github.com/kevalpatel2106]
 */
@RunWith(JUnit4::class)
class NWInterceptorCacheTest {
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/kevalpatel2106/network/responses", Paths.get("").toAbsolutePath().toString())

    private val mockWebServer = MockServerManager()
    private lateinit var apiProvider: ApiProvider

    @Before
    fun setUp() {
        mockWebServer.startMockWebServer()
        apiProvider = ApiProvider()
    }

    @After
    fun tearUp() {
        mockWebServer.close()
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
        val modifiedResponse = NWInterceptor(null, null, null)
                .addCachingHeaders(request, response)

        Assert.assertNull(modifiedResponse.headers().get("Cache-Time"))
        Assert.assertNotNull(modifiedResponse.headers().get("Cache-Control"))
        Assert.assertEquals(modifiedResponse.headers().get("Cache-Control"), "public, max-age=5000")
    }

    @Test
    @Throws(IOException::class)
    fun checkNoCacheHeader() {
        mockWebServer.enqueueResponse(File(RESPONSE_DIR_PATH + "/sucess_sample.json"))

        val response = apiProvider.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithoutCache()
                .execute()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertNull(response.headers().get("Cache-Control"))
    }

    @Test
    @Throws(IOException::class)
    fun checkCacheHeader() {
        mockWebServer.enqueueResponse(File(RESPONSE_DIR_PATH + "/sucess_sample.json"))

        val response = apiProvider.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithCache()
                .execute()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertNotNull(response.headers().get("Cache-Control"))
        Assert.assertEquals(response.headers().get("Cache-Control"), "public, max-age=5000")
    }
}
