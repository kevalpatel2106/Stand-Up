package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.network.Response
import com.kevalpatel2106.standup.BuildConfig
import com.kevalpatel2106.standup.authentication.login.LoginRequest
import com.kevalpatel2106.standup.authentication.login.LoginResponseData
import com.kevalpatel2106.standup.authentication.signUp.SignUpRequest
import com.kevalpatel2106.standup.authentication.signUp.SignUpResponseData
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Created by Keval on 19/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface AuthApiService {

    companion object {

        fun baseUrl() = BuildConfig.BASE_URL
    }

    //Login/Register apis
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Observable<Response<LoginResponseData>>


//    @POST("registerDevice")
//    fun registerDevice(@Body request: DeviceRegisterRequest): Observable<Response<DeviceRegisterData>>

    @POST("socialLogin")
    fun signUp(@Body signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>>

    @POST("socialLogin")
    fun socialSignUp(@Body signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>>

//    @POST("logout")
//    fun logout(@Body logoutRequest: LogoutRequest): Observable<Response<LogoutData>>
}