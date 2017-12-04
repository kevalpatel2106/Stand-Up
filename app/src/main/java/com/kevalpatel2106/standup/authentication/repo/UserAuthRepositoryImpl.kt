package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.base.annotations.Repository
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.RetrofitNetworkRefresherImpl
import com.kevalpatel2106.vault.VaultBuilder
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kevalpatel2106 on 20-Nov-17.
 * This is the implementation of the [UserAuthRepository].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
internal class UserAuthRepositoryImpl : UserAuthRepository {
    override fun logout(logoutRequest: LogoutRequest): Flowable<LogoutResponse> {
        val call = ApiProvider.getRetrofitClient(UserApiService.baseUrl())
                .create(UserApiService::class.java)
                .logout(logoutRequest)

        return VaultBuilder<LogoutResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun verifyEmailLink(url: String): Flowable<String> {
        val call = ApiProvider.getRetrofitClient(UserApiService.baseUrl())
                .create(UserApiService::class.java)
                .verifyEmailLink(url)

        return VaultBuilder<String>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Flowable<ForgotPasswordResponse> {
        val call = ApiProvider.getRetrofitClient(UserApiService.baseUrl())
                .create(UserApiService::class.java)
                .forgotPassword(forgotPasswordRequest)

        return VaultBuilder<ForgotPasswordResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun resendVerifyEmail(request: ResendVerificationRequest): Flowable<ResendVerificationResponse> {
        val call = ApiProvider.getRetrofitClient(UserApiService.baseUrl())
                .create(UserApiService::class.java)
                .resendVerifyEmail(request)

        return VaultBuilder<ResendVerificationResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun registerDevice(request: DeviceRegisterRequest): Flowable<DeviceRegisterResponse> {
        val call = ApiProvider.getRetrofitClient(UserApiService.baseUrl())
                .create(UserApiService::class.java)
                .registerDevice(request)

        return VaultBuilder<DeviceRegisterResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun login(loginRequest: LoginRequest): Flowable<LoginResponse> {
        val call = ApiProvider.getRetrofitClient(UserApiService.baseUrl())
                .create(UserApiService::class.java)
                .login(loginRequest)

        return VaultBuilder<LoginResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun signUp(signUpRequest: SignUpRequest): Flowable<SignUpResponse> {
        val call = ApiProvider.getRetrofitClient(UserApiService.baseUrl())
                .create(UserApiService::class.java)
                .signUp(signUpRequest)

        return VaultBuilder<SignUpResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun socialSignUp(signUpRequest: SignUpRequest): Flowable<SignUpResponse> {
        val call = ApiProvider.getRetrofitClient(UserApiService.baseUrl())
                .create(UserApiService::class.java)
                .socialSignUp(signUpRequest)

        return VaultBuilder<SignUpResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }
}