/*
 *  Copyright 2018 Keval Patel.
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

package com.standup.app.authentication.repo

import com.kevalpatel2106.common.di.AppModule
import com.kevalpatel2106.network.repository.RepoBuilder
import com.kevalpatel2106.network.repository.refresher.RetrofitNetworkRefresher
import com.kevalpatel2106.utils.annotations.Repository
import io.reactivex.Flowable
import retrofit2.Retrofit
import javax.inject.Named

/**
 * Created by Kevalpatel2106 on 20-Nov-17.
 * This is the implementation of the [UserAuthRepository].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
internal class UserAuthRepositoryImpl(@Named(AppModule.WITH_TOKEN) private val retrofit: Retrofit) : UserAuthRepository {

    override fun logout(logoutRequest: LogoutRequest): Flowable<LogoutResponse> {
        val call = retrofit.create(UserApiService::class.java).logout(logoutRequest)

        return RepoBuilder<LogoutResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun verifyEmailLink(url: String): Flowable<String> {
        val call = retrofit.create(UserApiService::class.java).verifyEmailLink(url)

        return RepoBuilder<String>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Flowable<ForgotPasswordResponse> {
        val call = retrofit.create(UserApiService::class.java).forgotPassword(forgotPasswordRequest)

        return RepoBuilder<ForgotPasswordResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun resendVerifyEmail(request: ResendVerificationRequest): Flowable<ResendVerificationResponse> {
        val call = retrofit.create(UserApiService::class.java).resendVerifyEmail(request)

        return RepoBuilder<ResendVerificationResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun registerDevice(request: DeviceRegisterRequest): Flowable<DeviceRegisterResponse> {
        val call = retrofit.create(UserApiService::class.java).registerDevice(request)

        return RepoBuilder<DeviceRegisterResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun login(loginRequest: LoginRequest): Flowable<LoginResponse> {
        val call = retrofit.create(UserApiService::class.java).login(loginRequest)

        return RepoBuilder<LoginResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun signUp(signUpRequest: SignUpRequest): Flowable<SignUpResponse> {
        val call = retrofit.create(UserApiService::class.java).signUp(signUpRequest)

        return RepoBuilder<SignUpResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun socialSignUp(signUpRequest: SignUpRequest): Flowable<SignUpResponse> {
        val call = retrofit.create(UserApiService::class.java).socialSignUp(signUpRequest)

        return RepoBuilder<SignUpResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }
}
