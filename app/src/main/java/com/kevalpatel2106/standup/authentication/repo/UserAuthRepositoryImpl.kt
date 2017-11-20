package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.Response
import com.kevalpatel2106.standup.authentication.login.LoginRequest
import com.kevalpatel2106.standup.authentication.login.LoginResponseData
import com.kevalpatel2106.standup.authentication.signUp.SignUpRequest
import com.kevalpatel2106.standup.authentication.signUp.SignUpResponseData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kevalpatel2106 on 20-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class UserAuthRepositoryImpl : UserAuthRepository {

    override fun login(loginRequest: LoginRequest): Observable<Response<LoginResponseData>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signUp(signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun socialSignUp(signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>> {
        return ApiProvider.getRetrofitClient(UserAuthRepository.baseUrl())
                .create(UserAuthRepository::class.java)
                .socialSignUp(signUpRequest)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
    }

}