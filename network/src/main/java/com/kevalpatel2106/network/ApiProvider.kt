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

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.VisibleForTesting
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Created by Keval Patel on 10/09/17.
 * This class deals with the API and network calls using the RxJava and retrofit.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
object ApiProvider {

    /**
     * OkHttp instance. New instances will be shallow copy of this instance.
     *
     * @see .getOkHttpClientBuilder
     */
    @VisibleForTesting
    internal lateinit var sOkHttpClient: OkHttpClient

    @VisibleForTesting
    internal fun getOkHttpClient(context: Context?): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(NetworkConfig.READ_TIMEOUT, TimeUnit.MINUTES)
                .writeTimeout(NetworkConfig.WRITE_TIMEOUT, TimeUnit.MINUTES)
                .connectTimeout(NetworkConfig.CONNECTION_TIMEOUT, TimeUnit.MINUTES)

        //Add debug interceptors
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            httpClientBuilder.addInterceptor(StethoInterceptor())
                    .addInterceptor(loggingInterceptor)
        }

        return if (context == null) {
            httpClientBuilder
                    .addInterceptor(NWInterceptor(null))  /* Add the interceptor. */
                    .build()
        } else {
            httpClientBuilder
                    .cache(NWInterceptor.getCache(context)) /* Add caching */
                    .addInterceptor(NWInterceptor(context))  /* Add the interceptor. */
                    .build()
        }
    }

    /**
     * Gson instance with custom gson deserializers.
     */
    private val sGson: Gson = GsonBuilder()
            .setLenient()
            .create()

    /**
     * Get the singleton instance of the shared preference provider.
     *
     * @param context Application Context.
     */
    @SuppressLint("VisibleForTests")
    @JvmStatic
    fun init(context: Context) {
        sOkHttpClient = getOkHttpClient(context)
    }

    /**
     * Get the singleton instance of the shared preference provider.
     */
    @VisibleForTesting
    @JvmStatic
    fun init() {
        sOkHttpClient = getOkHttpClient(null)
    }

    @VisibleForTesting
    internal fun close() {
        init()
    }

    /**
     * Get the retrofit client instance for given base URL.
     *
     * @param baseUrl Base url of the api.
     */
    fun getRetrofitClient(baseUrl: String): Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(sOkHttpClient)
            .addConverterFactory(NWResponseConverter.create(sGson))
            .build()
}