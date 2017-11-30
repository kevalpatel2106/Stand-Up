package com.kevalpatel2106.network

import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 30-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class RetrofitClientTest {

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

    @Test
    @Throws(IOException::class)
    fun checkBaseUrl() {
        val retrofit = ApiProvider.getRetrofitClient("http://google.com")
        Assert.assertEquals(retrofit.baseUrl().toString(), "http://google.com/")
    }

    @Test
    @Throws(IOException::class)
    fun checkGsonAdapter() {
        val retrofit = ApiProvider.getRetrofitClient("http://google.com")
        Assert.assertEquals(retrofit.converterFactories().size, 2)  //This should be custom converter
    }
}