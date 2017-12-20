package com.kevalpatel2106.network

import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import okhttp3.Request
import org.apache.commons.codec.binary.Base64
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import retrofit2.Call
import retrofit2.Response
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
    private val RESPONSE_DIR_PATH = String.format("%s/network/src/test/java/com/kevalpatel2106/network/responses", Paths.get(".").toAbsolutePath().toString())

    private val TEST_PREF_STRING = "TestValue"
    private val TEST_PREF_LONG = 100L

    private val mockWebServer = MockServerManager()

    companion object {

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            ApiProvider.init()
        }

        @JvmStatic
        @AfterClass
        fun tearUpClass() {
            ApiProvider.close()
        }
    }

    @Before
    fun setUp() {
        val context = Mockito.mock(Context::class.java)
        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        val sharedPrefsEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPrefs)
        Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)
        Mockito.`when`(sharedPrefs.getString(anyString(), anyString())).thenReturn(TEST_PREF_STRING)
        Mockito.`when`(sharedPrefs.getString(anyString(), ArgumentMatchers.isNull())).thenReturn(TEST_PREF_STRING)
        Mockito.`when`(sharedPrefs.getLong(anyString(), anyLong())).thenReturn(TEST_PREF_LONG)
        SharedPrefsProvider.init(context)

        mockWebServer.startMockWebServer()
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

        val modifiedRequest = NWInterceptor(null).addAuthHeader(request)

        Assert.assertNull(modifiedRequest.headers().get("Add-Auth"))

        Assert.assertNotNull(modifiedRequest.headers().get("Authorization"))
        Assert.assertEquals(modifiedRequest.headers().get("Authorization"),
                "Basic " + String(Base64.encodeBase64((UserSessionManager.userId.toString()
                        + ":" + UserSessionManager.token).toByteArray())))
    }

    @Test
    @Throws(IOException::class)
    fun checkApiRequestWithAuthHeader() {
        mockWebServer.enqueueResponse(mockWebServer
                .getStringFromFile(File(RESPONSE_DIR_PATH + "/sucess_sample.json")))

        ApiProvider.getRetrofitClient(mockWebServer.getBaseUrl())
                .create(TestApiService::class.java)
                .callBaseWithAuthHeader()
                .enqueue(object : retrofit2.Callback<TestData> {
                    override fun onFailure(call: Call<TestData>?, t: Throwable?) {
                        Assert.fail("There shouldn't be error. Message : " + t?.message)
                    }

                    override fun onResponse(call: Call<TestData>?, response: Response<TestData>) {
                        Assert.assertNotNull(response.raw().request().headers().get("Authorization"))
                        Assert.assertEquals(response.raw().request().headers().get("Authorization"),
                                "Basic " + String(
                                        Base64.encodeBase64((UserSessionManager.userId.toString()
                                                + ":" + UserSessionManager.token).toByteArray())))
                    }

                })

    }
}
