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

            if (obj.has("d")) {
                baseResponse.d = obj.get("d").asJsonObject.toString()
            }
            return baseResponse
        }

        return null
    }

}