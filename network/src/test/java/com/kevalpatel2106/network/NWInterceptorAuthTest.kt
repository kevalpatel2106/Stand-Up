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

import android.content.SharedPreferences
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import okhttp3.Request
import org.apache.commons.codec.binary.Base64
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
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
class NWInterceptorAuthTest {
    private val path = Paths.get("").toAbsolutePath().toString().let {
        return@let if (it.endsWith("network")) it else it.plus("/network")
    }
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/kevalpatel2106/network/responses", path)


    private val TEST_PREF_STRING = "TestValue"
    private val TEST_PREF_LONG = 100L

    private val mockWebServer = MockServerManager()
    private lateinit var apiProvider: ApiProvider
    private lateinit var userSessionManager: UserSessionManager

    @Before
    fun setUp() {
        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(sharedPrefs.getString(anyString(), anyString())).thenReturn(TEST_PREF_STRING)
        Mockito.`when`(sharedPrefs.getString(anyString(), ArgumentMatchers.isNull())).thenReturn(TEST_PREF_STRING)
        Mockito.`when`(sharedPrefs.getLong(anyString(), anyLong())).thenReturn(TEST_PREF_LONG)

        mockWebServer.startMockWebServer()

        userSessionManager = UserSessionManager(SharedPrefsProvider(sharedPrefs))
        apiProvider = ApiProvider(null, userSessionManager)
    }

    @After
    fun tearUp() {
        mockWebServer.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkAddAuthHeader() {
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
        mockWebServer.enqueueResponse(File(RESPONSE_DIR_PATH + "/sucess_sample.json"))

        val response = apiProvider.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithAuthHeader()
                .execute()

        Assert.assertNotNull(response.raw().request().headers().get("Authorization"))
        Assert.assertEquals(response.raw().request().headers().get("Authorization"),
                "Basic " + String(Base64.encodeBase64((TEST_PREF_LONG.toString()
                        + ":" + TEST_PREF_STRING).toByteArray())))
    }

    @Test
    @Throws(IOException::class)
    fun checkApiRequestWithoutAuthHeader() {
        mockWebServer.enqueueResponse(File(RESPONSE_DIR_PATH + "/sucess_sample.json"))

        val response = apiProvider.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithoutAuthHeader()
                .execute()

        Assert.assertNull(response.raw().request().headers().get("Authorization"))
    }

    @Test
    @Throws(IOException::class)
    fun checkApiWithNullUserSession() {
        mockWebServer.enqueueResponse(File(RESPONSE_DIR_PATH + "/sucess_sample.json"))

        apiProvider = ApiProvider(null, null)
        val response = apiProvider.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithAuthHeader()
                .execute()

        Assert.assertNull(response.raw().request().headers().get("Authorization"))
    }
}
