package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.base.annotations.Repository
import com.kevalpatel2106.standup.BuildConfig
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by Keval on 19/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
interface UserApiService {

    companion object {
        fun baseUrl() = BuildConfig.BASE_URL
    }

    //Login/Register apis
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("registerDevice")
    fun registerDevice(@Body request: DeviceRegisterRequest): Call<DeviceRegisterResponse>

    @POST("signUp")
    fun signUp(@Body signUpRequest: SignUpRequest): Call<SignUpResponse>

    @POST("forgotPassword")
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    @POST("socialLogin")
    fun socialSignUp(@Body signUpRequest: SignUpRequest): Call<SignUpResponse>

    @Headers("Add-Auth: true")
    @POST("resendVerifyMail")
    fun resendVerifyEmail(@Body request: ResendVerificationRequest): Call<ResendVerificationResponse>

    @GET
    fun verifyEmailLink(@Url url: String): Call<String>

    @Headers("Add-Auth: true")
    @POST("logout")
    fun logout(@Body logoutRequest: LogoutRequest): Call<LogoutResponse>
}