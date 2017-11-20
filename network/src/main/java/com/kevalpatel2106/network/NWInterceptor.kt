/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the GNU General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kevalpatel2106.network

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Base64
import okhttp3.*
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection

/**
 * Created by Keval on 16-Jun-17.
 * Network interceptor that will handle authentication headers and caching in the request/response.
 *
 * @property context Instance of the caller.
 *
 * @author [&#39;https://github.com/kevalpatel2106&#39;]['https://github.com/kevalpatel2106']
 */
internal class NWInterceptor(private val context: Context) : Interceptor {

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
        return preprocessedResponse(response)
    }

    @Suppress("DEPRECATION")
    private fun preprocessedResponse(response: Response): Response {
        return if (response.code() == HttpURLConnection.HTTP_OK) {   //HTTP OK

            if (response.header("Content-type").equals("application/json")) {   //Check if the json resposne.
                //Check for the error code other than the success.
                val responseStr = response.body()?.string()
                val status = JSONObject(responseStr).getJSONObject("s")

                val code = status.getInt("c")
                when {
                    code == APIStatusCodes.SUCCESS_CODE && responseStr != null -> {
                        //Success
                        //Nothing to do. Go ahead.
                        //We consumed the response body once so we need to build it again.
                        response.newBuilder()
                                .body(ResponseBody.create(MediaType.parse("application/json"), responseStr))
                                .build()
                    }
                    code == APIStatusCodes.ERROR_CODE_UNAUTHORIZED -> {     //You are unauthorized.

                        //Broadcast to the app module that user is not authorized.
                        //Log out.
                        LocalBroadcastManager.getInstance(context)
                                .sendBroadcast(Intent(NetworkConfig.BROADCAST_UNAUTHORIZED))
                        response
                    }
                    code < APIStatusCodes.SUCCESS_CODE -> {       //Exception occurred on the server
                        throw NWException(code, NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
                    }
                    else -> {   //Some recoverable error occurred on the server
                        val message = status.getString("m")
                        throw NWException(code, message)
                    }
                }
            } else {
                //String response. Cannot do anything.
                response
            }
        } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED
                || response.code() == HttpURLConnection.HTTP_FORBIDDEN) {  //Unauthorized or forbidden

            //Broadcast to the app module that user is not authorized.
            //Log out.
            LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(Intent(NetworkConfig.BROADCAST_UNAUTHORIZED))

            throw NWException(response.code(), NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
        } else if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST
                || response.code() == HttpURLConnection.HTTP_BAD_METHOD) {  //Bad request

            throw NWException(response.code(), NetworkConfig.ERROR_MESSAGE_BAD_REQUEST)
        } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND
                || response.code() == HttpURLConnection.HTTP_NOT_IMPLEMENTED) {  //404. Not found

            throw NWException(response.code(), NetworkConfig.ERROR_MESSAGE_NOT_FOUND)
        } else if (response.code() == HttpURLConnection.HTTP_SERVER_ERROR
                || response.code() == HttpURLConnection.HTTP_UNAVAILABLE) {  //500. Server is busy.

            throw NWException(response.code(), NetworkConfig.ERROR_MESSAGE_SERVER_BUSY)
        } else {
            //No specific error
            throw NWException(response.code(), NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG)
        }
    }

    /**
     * This will add cache control header to the response base on the time provided in "Cache-Time"
     * header value.
     */
    fun addCachingHeaders(request: Request, response: Response): Response {
        var response1 = response
        request.header("Cache-Time")?.let {
            response1 = if (isNetworkAvailable(context)) {
                response1.newBuilder()
                        .header("Cache-Control", "public, max-age=" + request.header("Cache-Time"))
                        .build()
            } else {
                val maxStale = 2419200 // tolerate 4-weeks stale
                response1.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build()
            }
        }
        return response1
    }

    /**
     * Adds the auth header if the username and passwords are provided for the authentication and
     * there is no "No-Authorization" header.
     */
    fun addAuthHeader(request: Request): Request {
        var request1 = request
        if (request1.header("Username") != null
                && request1.header("Token") != null) {
            request1 = request1
                    .newBuilder()
                    .header("Authorization", "Basic " + Base64
                            .encodeToString((request1.header("Username")
                                    + ":"
                                    + request1.header("Token")).toByteArray(),
                                    Base64.NO_WRAP))
                    .removeHeader("Token")
                    .removeHeader("Username")
                    .build()
        }
        return request1
    }

    /**
     * Check if the internet is available?
     *
     * @param context instance.
     * @return True if the internet is available else false.
     */
    private fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
