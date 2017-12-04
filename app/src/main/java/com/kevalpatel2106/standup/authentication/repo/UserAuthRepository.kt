package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.base.annotations.Repository
import io.reactivex.Flowable
import retrofit2.http.*


/**
 * Created by Keval on 19/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
interface UserAuthRepository {

    //Login/Register apis
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Flowable<LoginResponse>

    @POST("registerDevice")
    fun registerDevice(@Body request: DeviceRegisterRequest): Flowable<DeviceRegisterResponse>

    @POST("signUp")
    fun signUp(@Body signUpRequest: SignUpRequest): Flowable<SignUpResponse>

    @POST("forgotPassword")
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Flowable<ForgotPasswordResponse>

    @POST("socialLogin")
    fun socialSignUp(@Body signUpRequest: SignUpRequest): Flowable<SignUpResponse>

    @Headers("Add-Auth: true")
    @POST("resendVerifyMail")
    fun resendVerifyEmail(@Body request: ResendVerificationRequest): Flowable<ResendVerificationResponse>

    @GET
    fun verifyEmailLink(@Url url: String): Flowable<String>

    @Headers("Add-Auth: true")
    @POST("logout")
    fun logout(@Body logoutRequest: LogoutRequest): Flowable<LogoutResponse>
}