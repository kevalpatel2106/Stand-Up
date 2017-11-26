package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.network.Response
import com.kevalpatel2106.standup.BuildConfig
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


/**
 * Created by Keval on 19/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface UserAuthRepository {

    companion object {
        fun baseUrl() = BuildConfig.BASE_URL
    }

    //Login/Register apis
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Observable<Response<LoginResponseData>>

    @POST("registerDevice")
    fun registerDevice(@Body request: DeviceRegisterRequest): Observable<Response<DeviceRegisterData>>

    @POST("signUp")
    fun signUp(@Body signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>>

    @POST("forgotPassword")
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Observable<Response<ForgotPasswordResponseData>>

    @POST("socialLogin")
    fun socialSignUp(@Body signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>>

    @Headers("Add-Auth: true")
    @POST("resendVerifyMail")
    fun resendVerifyEmail(@Body request: ResendVerificationRequest): Observable<Response<ResendVerificationResponseData>>

//    @POST("logout")
//    fun logout(@Body logoutRequest: LogoutRequest): Observable<Response<LogoutData>>
}