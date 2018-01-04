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

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Created by Kevalpatel2106 on 29-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class BaseResponseJsonDeserializer : JsonDeserializer<BaseResponse> {

    override fun deserialize(json: JsonElement?,
                             typeOfT: Type?,
                             context: JsonDeserializationContext?): BaseResponse? {

        json?.let {
            val obj = json.asJsonObject

            val baseResponse = BaseResponse()
            if (obj.has("s")) {
                baseResponse.status = BaseResponse.Status(
                        obj.getAsJsonObject("s").get("c").asInt,
                        if (obj.getAsJsonObject("s").has("m"))
                            obj.getAsJsonObject("s").get("m").asString
                        else
                            null)
            } else {
                baseResponse.status = BaseResponse.Status(APIStatusCodes.ERROR_CODE_UNKNOWN_ERROR)
            }

            if (obj.has("d") && !obj.get("d").isJsonNull) {
                baseResponse.d = obj.get("d").asJsonObject.toString()
            }
            return baseResponse
        }

        return null
    }

}