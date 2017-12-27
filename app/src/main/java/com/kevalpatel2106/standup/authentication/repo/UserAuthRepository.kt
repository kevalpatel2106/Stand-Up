/*
 *  Copyright 2017 Keval Patel.
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