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

import com.google.gson.GsonBuilder
import org.junit.Assert
import org.junit.Test

/**
 * Created by Keval on 26/12/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class BaseResponseJsonDeserializerTest {

    val ONLY_STATUS_CODE = "{\"s\":{\"c\":-1}}"
    val ONLY_STATUS = "{\"s\":{\"c\":-1,\"m\":\"Cannot access database.\"}}"
    val STATUS_AND_DATA = "{\"s\":{\"c\":0,\"m\":\"OK\"},\"d\":{\"uid\":1023}}"

    private val gson = GsonBuilder()
            .registerTypeAdapter(BaseResponse::class.java, BaseResponseJsonDeserializer())
            .create()

    @Test
    fun checkNullJsonElement() {
        val baseResponse = gson.fromJson("", BaseResponse::class.java)
        Assert.assertNull(baseResponse)
    }

    @Test
    fun checkWithJsonElementWithoutStatus() {
        val baseResponse = gson.fromJson("{}", BaseResponse::class.java)
        Assert.assertEquals(baseResponse.status.statusCode, APIStatusCodes.ERROR_CODE_UNKNOWN_ERROR)
    }

    @Test
    fun checkWithJsonElementWithOnlyStatusCode() {
        val baseResponse = gson.fromJson(ONLY_STATUS_CODE, BaseResponse::class.java)
        Assert.assertEquals(baseResponse.status.statusCode, -1)
    }

    @Test
    fun checkWithJsonElementWithOnlyStatus() {
        val baseResponse = gson.fromJson(ONLY_STATUS, BaseResponse::class.java)
        Assert.assertEquals(baseResponse.status.statusCode, -1)
        Assert.assertEquals(baseResponse.status.message, "Cannot access database.")
    }

    @Test
    fun checkWithJsonElementWithStatusAndData() {
        val baseResponse = gson.fromJson(STATUS_AND_DATA, BaseResponse::class.java)
        Assert.assertEquals(baseResponse.status.statusCode, 0)
        Assert.assertEquals(baseResponse.status.message, "OK")
        Assert.assertEquals(baseResponse.d, "{\"uid\":1023}")
    }
}
