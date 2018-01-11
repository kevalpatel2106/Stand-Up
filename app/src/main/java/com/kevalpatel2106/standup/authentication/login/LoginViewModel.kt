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

package com.kevalpatel2106.standup.authentication.login

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.base.arch.SingleLiveEvent
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthUser
import com.kevalpatel2106.standup.BaseApplication
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.di.DaggerUserAuthComponent
import com.kevalpatel2106.standup.authentication.repo.LoginRequest
import com.kevalpatel2106.standup.authentication.repo.SignUpRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.misc.Validator
import com.kevalpatel2106.utils.UserSessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Keval on 19/11/17.
 * This is the [ViewModel] class for the [LoginActivity]. This will hold all the data for the activity
 * regardless of the lifecycle.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(LoginActivity::class)
class LoginViewModel : BaseViewModel {

    @Inject lateinit var userAuthRepo: UserAuthRepository
    @Inject lateinit var userSessionManager: UserSessionManager

    @Suppress("unused")
    constructor() {
        DaggerUserAuthComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@LoginViewModel)
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
     * [LoginUiModel] to hold the response form the user authentication. UI element can
     * observe this [MutableLiveData] to change the state when user authentication succeed or fails.
     */
    internal val mLoginUiModel: MutableLiveData<LoginUiModel> = MutableLiveData()

    internal val mEmailError: SingleLiveEvent<ErrorMessage> = SingleLiveEvent()

    internal val mPasswordError: SingleLiveEvent<ErrorMessage> = SingleLiveEvent()

    internal val mNameError: SingleLiveEvent<ErrorMessage> = SingleLiveEvent()

    internal val isGoogleLoginProgress = MutableLiveData<Boolean>()

    internal val isFacebookLoginProgress = MutableLiveData<Boolean>()

    internal val isEmailLoginProgress = MutableLiveData<Boolean>()

    fun init() {
        isEmailLoginProgress.value = false
        isFacebookLoginProgress.value = false
        isGoogleLoginProgress.value = false
    }

    /**
     * Authenticate new user while sign up.
     *
     * @param email Email of the new user.
     * @param password Password fo the user to send.
     * @param name Name of the user.
     */
    fun performSignUp(email: String, password: String, name: String, confirmPassword: String) {
        //validate the email
        if (Validator.isValidEmail(email)) {

            //validate password
            if (Validator.isValidPassword(password)) {

                //Validate the user name
                if (Validator.isValidName(name)) {

                    //Validate confirm password.
                    if (password == confirmPassword) {

                        userAuthRepo.signUp(SignUpRequest(email, name, password, null))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe({
                                    isEmailLoginProgress.value = true
                                    blockUi.postValue(true)
                                })
                                .doAfterTerminate({
                                    isEmailLoginProgress.value = false
                                    blockUi.value = false
                                })
                                .subscribe({
                                    //Save into the user session
                                    userSessionManager.setNewSession(userId = it.uid,
                                            displayName = name,
                                            token = null,
                                            email = email,
                                            photoUrl = it.photoUrl,
                                            isVerified = it.isVerified)

                                    val loginUiModel = LoginUiModel(true)
                                    loginUiModel.isNewUser = it.isNewUser
                                    loginUiModel.isVerify = it.isVerified
                                    mLoginUiModel.value = loginUiModel
                                }, {
                                    mLoginUiModel.value = LoginUiModel(false)
                                    errorMessage.value = ErrorMessage(it.message)
                                })
                    } else {
                        mPasswordError.value = ErrorMessage(R.string.login_error_password_did_not_match)
                    }
                } else {
                    mNameError.value = ErrorMessage(R.string.error_login_invalid_name)
                }
            } else {
                mPasswordError.value = ErrorMessage(R.string.error_login_invalid_password)
            }
        } else {
            mEmailError.value = ErrorMessage(R.string.error_login_invalid_email)
        }
    }

    /**
     * Authenticate user while login.
     *
     * @param email Email of the new user.
     * @param password Password fo the user to send.
     */
    fun performSignIn(email: String, password: String) {
        //validate the email
        if (Validator.isValidEmail(email)) {

            //validate password
            if (Validator.isValidPassword(password)) {

                userAuthRepo.login(LoginRequest(email, password))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe({
                            isEmailLoginProgress.value = true
                            blockUi.postValue(true)
                        })
                        .doAfterTerminate({
                            isEmailLoginProgress.value = false
                            blockUi.value = false
                        })
                        .subscribe({
                            //Save into the user session
                            userSessionManager.setNewSession(userId = it.uid,
                                    displayName = it.name,
                                    token = null,
                                    email = it.email,
                                    photoUrl = it.photoUrl,
                                    isVerified = it.isVerified)

                            val loginUiModel = LoginUiModel(true)
                            loginUiModel.isNewUser = false
                            loginUiModel.isVerify = it.isVerified
                            mLoginUiModel.value = loginUiModel
                        }, {
                            mLoginUiModel.value = LoginUiModel(false)
                            errorMessage.value = ErrorMessage(it.message)
                        })
            } else {
                mPasswordError.value = ErrorMessage(R.string.error_login_invalid_password)
            }
        } else {
            mEmailError.value = ErrorMessage(R.string.error_login_invalid_email)
        }
    }

    /**
     * Start the authentication flow for the user signed in using facebook.
     */
    @SuppressLint("VisibleForTests")
    fun authenticateSocialUser(fbUser: FacebookUser) {
        if (fbUser.email.isNullOrEmpty() || fbUser.name.isNullOrEmpty()) {
            errorMessage.value = ErrorMessage(R.string.error_fb_login_email_not_found)
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
     * Authenticate user with google or facebook login.
     *
     * @param requestData Sign up request data.
     */
    @VisibleForTesting
    fun authenticateSocialUser(requestData: SignUpRequest) {
        userAuthRepo.socialSignUp(requestData)
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
                .subscribe({
                    //Save into the user session
                    userSessionManager.setNewSession(userId = it.uid,
                            displayName = requestData.displayName,
                            token = null,
                            email = requestData.email,
                            photoUrl = it.photoUrl,
                            isVerified = it.isVerified)

                    val loginUiModel = LoginUiModel(true)
                    loginUiModel.isNewUser = it.isNewUser
                    mLoginUiModel.value = loginUiModel
                }, {
                    mLoginUiModel.value = LoginUiModel(false)
                    errorMessage.value = ErrorMessage(it.message)
                })
    }
}
