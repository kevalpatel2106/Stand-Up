package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.network.Response
import com.kevalpatel2106.standup.authentication.signUp.SignUpRequest
import com.kevalpatel2106.standup.authentication.signUp.SignUpResponseData
import io.reactivex.Observable

/**
 * Created by Keval on 20/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface UserAuthRepository {

    /**
     * This method provides [Observable] to login/sign up user using the social accounts.
     */
    fun authSocialUser(requestData: SignUpRequest): Observable<Response<SignUpResponseData>>
}