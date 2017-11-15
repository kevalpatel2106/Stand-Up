package com.kevalpatel2106.network

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Created by Keval on 12/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal interface TestApiService {

    @GET("test")
    fun callBase(): Observable<Response<TestData>>

    @Headers("No-Authorization: true")
    @GET("test")
    fun callBaseWithoutAuthHeader(): Observable<retrofit2.Response<TestData>>

    @GET("test")
    fun callBaseWithAuthHeader(): Observable<retrofit2.Response<TestData>>

    @GET("test")
    fun callBaseWithoutCache(): Observable<retrofit2.Response<TestData>>

    @Headers("Cache-Time: 5000")
    @GET("test")
    fun callBaseWithCache(): Observable<retrofit2.Response<TestData>>
}