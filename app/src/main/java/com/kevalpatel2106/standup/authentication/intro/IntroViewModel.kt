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

package com.kevalpatel2106.standup.authentication.intro

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthUser
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.di.DaggerUserAuthComponent
import com.kevalpatel2106.standup.authentication.repo.SignUpRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.utils.annotations.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Keval on 19/11/17.
 * This is the [ViewModel] class for the [IntroActivity]. This will hold all the data for the activity
 * regardless of the lifecycle.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(IntroActivity::class)
class IntroViewModel : BaseViewModel {

    @Inject lateinit var userAuthRepo: UserAuthRepository

    @Inject lateinit var userSessionManager: UserSessionManager

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() {
        DaggerUserAuthComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@IntroViewModel)

        init()
    }

    @VisibleForTesting
    constructor(userAuthRepo: UserAuthRepository,
                                   userSessionManager: UserSessionManager) {
        this.userAuthRepo = userAuthRepo
        this.userSessionManager = userSessionManager

        init()
    }

    /**
     * [IntroUiModel] to hold the response form the user authentication. UI element can
     * observe this [MutableLiveData] to change the state when user authentication succeed or fails.
     */
    internal val introUiModel: MutableLiveData<IntroUiModel> = MutableLiveData()

    internal val isGoogleLoginProgress = MutableLiveData<Boolean>()

    internal val isFacebookLoginProgress = MutableLiveData<Boolean>()

    fun init() {
        isFacebookLoginProgress.value = false
        isGoogleLoginProgress.value = false
    }

    /**
     * Start the authentication flow for the user signed in using facebook.
     */
    @SuppressLint("VisibleForTests")
    fun authenticateSocialUser(fbUser: FacebookUser) {
        if (fbUser.email.isNullOrEmpty() || fbUser.name.isNullOrEmpty()) {

            errorMessage.value = ErrorMessage(R.string.error_fb_login_email_not_found)
            isFacebookLoginProgress.value = false
        } else {
            blockUi.value = true
            isFacebookLoginProgress.value = true

            authenticateSocialUser(SignUpRequest(fbUser = fbUser))
        }
    }

    /**
     * Start the authentication flow for the user signed in using google.
     */
    @SuppressLint("VisibleForTests")
    fun authenticateSocialUser(googleUser: GoogleAuthUser) {
        if (googleUser.email.isEmpty() || googleUser.name.isEmpty()) {

            errorMessage.value = ErrorMessage(R.string.error_google_login_email_not_found)
        } else {
            isGoogleLoginProgress.value = true
            blockUi.value = true

            authenticateSocialUser(SignUpRequest(googleUser = googleUser))
        }
    }

    /**
     * Authenticate the user using the social signin.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun authenticateSocialUser(requestData: SignUpRequest) {
        addDisposable(userAuthRepo.socialSignUp(requestData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({
                    blockUi.value = true
                })
                .doAfterTerminate({
                    blockUi.value = false
                    isGoogleLoginProgress.value = false
                    isFacebookLoginProgress.value = false
                })
                .subscribe({ data ->
                    //Save into the user session
                    userSessionManager.setNewSession(userId = data.uid,
                            displayName = requestData.displayName,
                            token = null,
                            email = requestData.email,
                            photoUrl = data.photoUrl,
                            isVerified = data.isVerified)

                    val model = IntroUiModel(true)
                    model.isNewUser = data.isNewUser
                    introUiModel.value = model
                }, { t ->
                    //Update the activity
                    introUiModel.value = IntroUiModel(false)
                    errorMessage.value = ErrorMessage(t.message)
                }))
    }
}
