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

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.GsonBuilder
import com.kevalpatel2106.network.NetworkHook
import okhttp3.*
import org.apache.commons.codec.binary.Base64
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection

/**
 * Created by Keval on 16-Jun-17.
 * Network interceptor that will handle authentication headers and caching in the request/response.
 *
 * @author [&#39;https://github.com/kevalpatel2106&#39;]['https://github.com/kevalpatel2106']
 */
internal class NWInterceptor(private val networkHook: NetworkHook?,
                             private val userId: String?,
                             private val token: String?) : Interceptor {

    private val gson = GsonBuilder()
            .registerTypeAdapter(BaseResponse::class.java, BaseResponseJsonDeserializer())
            .create()

    companion object {
        internal val CACHE_SIZE = 5242880L          //5 MB //Cache size.

        /**
         * Initialize the cache directory.
         *
         * @param context Instance of caller.
         * @return [Cache].
         */
        @JvmStatic
        fun getCache(context: Context): Cache {
            //Define mCache
            val httpCacheDirectory = File(context.cacheDir, "responses")
            return Cache(httpCacheDirectory, CACHE_SIZE)
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()

        //Set the authorization header
        request = addAuthHeader(request)

        var response = chain.proceed(request)

        //Enable caching if you need.
        response = addCachingHeaders(request, response)

        //Prepossess the response to find out errors based on the server response/status code.
        return progressResponse(response)
    }

    @Suppress("DEPRECATION")
    private fun progressResponse(response: Response): Response {

        //HTTP OK
        //Code 200 and there is some data in the response
        return if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {

            //Check if the json response.
            if (response.header("Content-type").equals("application/json")) {

                return processJsonResponse(response, networkHook)
            } else {

                //String response. Cannot do anything.
                response
            }
        } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED
                || response.code() == HttpURLConnection.HTTP_FORBIDDEN) {  //Unauthorized or forbidden

            //Broadcast to the app module that user is not authorized.
            //Log out.
            networkHook?.onAuthenticationFailed()

            return response.newBuilder()
                    .message(NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
                    .code(response.code())
                    .build()
        } else if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST
                || response.code() == HttpURLConnection.HTTP_BAD_METHOD) {

            //Bad request
            return response.newBuilder()
                    .message(NetworkConfig.ERROR_MESSAGE_BAD_REQUEST)
                    .code(response.code())
                    .build()
        } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND
                || response.code() == HttpURLConnection.HTTP_NOT_IMPLEMENTED) {

            //404. Not found
            return response.newBuilder()
                    .message(NetworkConfig.ERROR_MESSAGE_NOT_FOUND)
                    .code(response.code())
                    .build()
        } else if (response.code() == HttpURLConnection.HTTP_SERVER_ERROR
                || response.code() == HttpURLConnection.HTTP_UNAVAILABLE) {

            //500. Server is busy.
            return response.newBuilder()
                    .message(NetworkConfig.ERROR_MESSAGE_SERVER_BUSY)
                    .code(response.code())
                    .build()
        } else {

            //No specific error
            return response.newBuilder()
                    .message(NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
                    .code(response.code())
                    .build()
        }
    }

    private fun processJsonResponse(response: Response, networkHook: NetworkHook?): Response {
        //Read the response.
        val responseStr = response.body()?.string() ?: return createEmptyResponse(response)
        val baseResponse = gson.fromJson(responseStr, BaseResponse::class.java)

        when {
            baseResponse.status.statusCode == APIStatusCodes.SUCCESS_CODE -> {
                //Success
                //Nothing to do. Go ahead.
                //We consumed the response body once so we need to build it again.
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                return if (baseResponse.d.isNullOrEmpty())
                    createEmptyResponse(response)
                else
                    response.newBuilder()
                            .body(ResponseBody.create(MediaType.parse("application/json"), baseResponse.d))
                            .build()
            }
            baseResponse.status.statusCode == APIStatusCodes.ERROR_CODE_UNAUTHORIZED -> {
                //You are unauthorized.
                //Broadcast to the app module that user is not authorized.
                //Log out.
                networkHook?.onAuthenticationFailed()

                return response.newBuilder()
                        .body(ResponseBody.create(MediaType.parse("application/json"), responseStr))
                        .message(NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
                        .code(baseResponse.status.statusCode)
                        .build()
            }
            baseResponse.status.statusCode < APIStatusCodes.SUCCESS_CODE -> {

                //Exception occurred on the server
                return response.newBuilder()
                        .body(ResponseBody.create(MediaType.parse("application/json"), responseStr))
                        .message(NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
                        .code(APIStatusCodes.ERROR_CODE_EXCEPTION)
                        .build()
            }
            else -> {

                //Some recoverable error occurred on the server
                return response.newBuilder()
                        .body(ResponseBody.create(MediaType.parse("application/json"), responseStr))
                        .message(baseResponse.status.message
                                ?: NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
                        .code(baseResponse.status.statusCode)
                        .build()
            }
        }
    }

    private fun createEmptyResponse(response: Response): Response {
        return response.newBuilder()
                .body(ResponseBody.create(MediaType.parse("application/json"), "{}"))
                .build()
    }

    /**
     * This will add cache control header to the response base on the time provided in "Cache-Time"
     * header value.
     */
    fun addCachingHeaders(request: Request, response: Response): Response {
        var response1 = response
        request.header("Cache-Time")?.let {
            response1 = response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + request.header("Cache-Time"))
                    .build()
        }
        return response1
    }

    @SuppressLint("BinaryOperationInTimber")
            /**
             * Adds the auth header if the username and passwords are provided for the authentication and
             * there is no "No-Authorization" header.
             */
    fun addAuthHeader(request: Request): Request {
        var request1 = request
        if (request1.header("Add-Auth") != null && userId != null && token != null) {

            Timber.i("Authorization: Basic " + String(
                    Base64.encodeBase64((userId + ":" + token).toByteArray())))
            request1 = request1
                    .newBuilder()
                    .header("Authorization", "Basic " + String(
                            Base64.encodeBase64((userId + ":" + token).toByteArray())))
                    .removeHeader("Add-Auth")
                    .build()
        }
        return request1
    }
}
