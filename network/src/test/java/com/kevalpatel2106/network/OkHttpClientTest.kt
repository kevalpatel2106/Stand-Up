package com.kevalpatel2106.network

import android.content.Context
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 30-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class OkHttpClientTest {

    @Test
    @Throws(IOException::class)
    fun checkOkHttpClient() {
        val okHttpClient = ApiProvider.getOkHttpClient(Mockito.mock(Context::class.java))
        Assert.assertEquals(okHttpClient.readTimeoutMillis().toLong(), NetworkConfig.READ_TIMEOUT * 60 * 1000)
        Assert.assertEquals(okHttpClient.writeTimeoutMillis().toLong(), NetworkConfig.WRITE_TIMEOUT * 60 * 1000)
        Assert.assertEquals(okHttpClient.connectTimeoutMillis().toLong(), NetworkConfig.CONNECTION_TIMEOUT * 60 * 1000)
    }
}