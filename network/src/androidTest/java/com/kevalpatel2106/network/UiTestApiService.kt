package com.kevalpatel2106.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Created by Keval on 12/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal interface UiTestApiService {

    @Headers("Add-Auth: true")
    @GET("test")
    fun callBaseWithAuthHeader(): Call<TestData>
}