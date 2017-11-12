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
 *
 * @author 'https://github.com/kevalpatel2106'
 */
class BaseApiWrapper(private val context: Context) {

    companion object {
        /**
         * OkHttp instance. New instances will be shallow copy of this instance.
         *
         * @see .getOkHttpClientBuilder
         */
        private var sOkHttpClient: OkHttpClient

        /**
         * Gson instance with custom gson deserializers.
         */
        val sGson: Gson = GsonBuilder()
                .setLenient()
                .create()

        init {
            //Log interceptor
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

            sOkHttpClient = OkHttpClient.Builder()
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(loggingInterceptor)         //Add interceptor for logging the request
                    .build()
        }
    }

    /**
     * Make in api call on the separate thread.
     *
     * @param observable           [Observable]
     * @param isRetryOnNetworkFail If set true, this method will try for the api call on network not
     * available situation for three times with the period of 10 seconds.
     * @return [Observable]
     * @see [How to handle exceptions and errors?](https://github.com/ReactiveX/RxJava/issues/4942)
     */
    fun makeApiCall(observable: Observable<*>,
                    isRetryOnNetworkFail: Boolean): Observable<*> {
        //noinspection unchecked,Convert2Lambda
        return observable
                .retryWhen(RetryWithDelay(if (isRetryOnNetworkFail) 3 else 0, 10000))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
    }

    @JvmOverloads
    fun getRetrofitClient(baseUrl: String,
                          cacheTimeInMills: Long = 0,
                          username: String? = null,
                          token: String? = null): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClientBuilder(cacheTimeInMills, username, token))
                .addConverterFactory(NWResponseConverter.create(sGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    /**
     * Build the OkHTTP client.
     *  * Logging : Enabled for Debuggable builds.
     *  * Caching : Enabled.
     *  * Authentication header : Enabled. Basic authentication header will be Base64 encoded from
     * username and token provided.
     *
     * @param cacheTimeInMills Response cache timing. If 0, caching will be disabled.
     * @param username         Username
     * @param token         Password
     * @return [OkHttpClient]
     */
    internal fun getOkHttpClientBuilder(cacheTimeInMills: Long,
                                        username: String?,
                                        token: String?): OkHttpClient {
        val builder = sOkHttpClient.newBuilder()

        // Add the interceptor and cache
        builder.cache(NWInterceptor.getCache(context))
                .addInterceptor(NWInterceptor(context, cacheTimeInMills, username, token))
        return builder.build()
    }
}