package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.Response
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kevalpatel2106 on 20-Nov-17.
 * This is the implementation of the [UserAuthRepository].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class UserAuthRepositoryImpl : UserAuthRepository {
    override fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Observable<Response<ForgotPasswordResponseData>> {
        return ApiProvider.getRetrofitClient(UserAuthRepository.baseUrl())
                .create(UserAuthRepository::class.java)
                .forgotPassword(forgotPasswordRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun resendVerifyEmail(request: ResendVerificationRequest): Observable<Response<ResendVerificationResponseData>> {
        return ApiProvider.getRetrofitClient(UserAuthRepository.baseUrl())
                .create(UserAuthRepository::class.java)
                .resendVerifyEmail(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun registerDevice(request: DeviceRegisterRequest): Observable<Response<DeviceRegisterData>> {
        return ApiProvider.getRetrofitClient(UserAuthRepository.baseUrl())
                .create(UserAuthRepository::class.java)
                .registerDevice(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun login(loginRequest: LoginRequest): Observable<Response<LoginResponseData>> {
        return ApiProvider.getRetrofitClient(UserAuthRepository.baseUrl())
                .create(UserAuthRepository::class.java)
                .login(loginRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun signUp(signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>> {
        return ApiProvider.getRetrofitClient(UserAuthRepository.baseUrl())
                .create(UserAuthRepository::class.java)
                .signUp(signUpRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun socialSignUp(signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>> {
        return ApiProvider.getRetrofitClient(UserAuthRepository.baseUrl())
                .create(UserAuthRepository::class.java)
                .socialSignUp(signUpRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }
}