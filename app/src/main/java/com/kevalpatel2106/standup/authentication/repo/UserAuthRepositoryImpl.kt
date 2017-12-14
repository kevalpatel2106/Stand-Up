package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.base.annotations.Repository
import com.kevalpatel2106.base.repository.RepoBuilder
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.RetrofitNetworkRefresher
import io.reactivex.Flowable

/**
 * Created by Kevalpatel2106 on 20-Nov-17.
 * This is the implementation of the [UserAuthRepository].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
internal class UserAuthRepositoryImpl(private val baseUrl: String) : UserAuthRepository {

    constructor() : this(UserApiService.baseUrl())

    override fun logout(logoutRequest: LogoutRequest): Flowable<LogoutResponse> {
        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(UserApiService::class.java)
                .logout(logoutRequest)

        return RepoBuilder<LogoutResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun verifyEmailLink(url: String): Flowable<String> {
        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(UserApiService::class.java)
                .verifyEmailLink(url)

        return RepoBuilder<String>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Flowable<ForgotPasswordResponse> {
        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(UserApiService::class.java)
                .forgotPassword(forgotPasswordRequest)

        return RepoBuilder<ForgotPasswordResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun resendVerifyEmail(request: ResendVerificationRequest): Flowable<ResendVerificationResponse> {
        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(UserApiService::class.java)
                .resendVerifyEmail(request)

        return RepoBuilder<ResendVerificationResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun registerDevice(request: DeviceRegisterRequest): Flowable<DeviceRegisterResponse> {
        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(UserApiService::class.java)
                .registerDevice(request)

        return RepoBuilder<DeviceRegisterResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun login(loginRequest: LoginRequest): Flowable<LoginResponse> {
        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(UserApiService::class.java)
                .login(loginRequest)

        return RepoBuilder<LoginResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun signUp(signUpRequest: SignUpRequest): Flowable<SignUpResponse> {
        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(UserApiService::class.java)
                .signUp(signUpRequest)

        return RepoBuilder<SignUpResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun socialSignUp(signUpRequest: SignUpRequest): Flowable<SignUpResponse> {
        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(UserApiService::class.java)
                .socialSignUp(signUpRequest)

        return RepoBuilder<SignUpResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }
}