package com.kevalpatel2106.network

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Keval on 26/12/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class BaseResponseTest {

    @Test
    @Throws(IOException::class)
    fun checkStatus() {
        val baseResponse = BaseResponse()

        val status = BaseResponse.Status(0, "This is test.")
        baseResponse.status = status

        Assert.assertEquals(baseResponse.status, status)
        Assert.assertNull(baseResponse.d)
    }
}