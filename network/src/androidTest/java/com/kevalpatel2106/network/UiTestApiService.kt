package com.kevalpatel2106.network

import io.reactivex.Observable
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
    fun callBaseWithAuthHeader(): Observable<retrofit2.Response<TestData>>
}