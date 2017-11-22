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
import android.support.annotation.VisibleForTesting
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

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
    internal lateinit var sOkHttpClient: OkHttpClient

    private val httpClientBuilder = OkHttpClient.Builder()
            .readTimeout(NetworkConfig.READ_TIMEOUT, TimeUnit.MINUTES)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT, TimeUnit.MINUTES)
            .connectTimeout(NetworkConfig.CONNECTION_TIMEOUT, TimeUnit.MINUTES)

    init {
        //Add debug interceptors
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            httpClientBuilder.addInterceptor(StethoInterceptor())
                    .addInterceptor(loggingInterceptor)
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
    @JvmStatic
    fun init(context: Context) {
        sOkHttpClient = httpClientBuilder
                .cache(NWInterceptor.getCache(context)) /* Add caching */
                .addInterceptor(NWInterceptor(context))  /* Add the interceptor. */
                .build()
    }

    /**
     * Get the singleton instance of the shared preference provider.
     */
    @VisibleForTesting
    @JvmStatic
    fun init() {
        sOkHttpClient = httpClientBuilder
                .addInterceptor(NWInterceptor(null))  /* Add the interceptor. */
                .build()
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
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
}