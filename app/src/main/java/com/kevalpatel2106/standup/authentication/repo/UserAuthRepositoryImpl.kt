package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.base.annotations.Repository
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.Response
import com.kevalpatel2106.standup.authentication.signUp.SignUpRequest
import com.kevalpatel2106.standup.authentication.signUp.SignUpResponseData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Keval on 19/11/17.
 * This repository with provide the data/interface to authenticate the user.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

@Repository
internal class UserAuthRepositoryImpl : UserAuthRepository {

    /**
     * This method provides [Observable] to login/sign up user using the social accounts.
     */
    override fun authenticateSocialUser(requestData: SignUpRequest): Observable<Response<SignUpResponseData>> =
            ApiProvider.getRetrofitClient(AuthApiService.baseUrl())
                    .create(AuthApiService::class.java)
                    .socialSignUp(requestData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}
