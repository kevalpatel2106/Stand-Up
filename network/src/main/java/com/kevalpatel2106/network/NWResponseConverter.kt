package com.kevalpatel2106.network

import com.google.gson.Gson
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.Type

/**
 * Created by Keval on 12/11/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

internal class NWResponseConverter(private val gson: Gson) : Converter.Factory() {

    companion object {

        @JvmStatic
        fun create(gson: Gson): NWResponseConverter = NWResponseConverter(gson)
    }

    override fun responseBodyConverter(type: Type,
                                       annotations: Array<Annotation>,
                                       retrofit: Retrofit): Converter<ResponseBody, *>? {
        return try {

            //Response is the string type.
            //No need for the GSON converter
            if (type === String::class.java) {
                StringResponseConverter()
            } else GsonConverterFactory.create(gson).responseBodyConverter(type, annotations, retrofit)
        } catch (ignored: OutOfMemoryError) {
            null
        }

    }

    override fun requestBodyConverter(type: Type,
                                      parameterAnnotations: Array<Annotation>,
                                      methodAnnotations: Array<Annotation>,
                                      retrofit: Retrofit): Converter<*, RequestBody>? {
        return GsonConverterFactory.create(gson)
                .requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
    }

    private class StringResponseConverter : Converter<ResponseBody, String> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): String = value.string()
    }
}
