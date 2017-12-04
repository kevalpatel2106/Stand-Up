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
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.Validator
import com.kevalpatel2106.standup.authentication.repo.LoginRequest
import com.kevalpatel2106.standup.authentication.repo.SignUpRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.utils.UserSessionManager

/**
 * Created by Keval on 19/11/17.
 * This is the [ViewModel] class for the [LoginActivity]. This will hold all the data for the activity
 * regardless of the lifecycle.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(LoginActivity::class)
internal class LoginViewModel : BaseViewModel {

    /**
     * Repository to provide user authentications.
     */
    @VisibleForTesting
    var mUserAuthRepo: UserAuthRepository

    /**
     * Private constructor to add the custom [UserAuthRepository] for testing.
     *
     * @param userAuthRepo Add your own [UserAuthRepository].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(userAuthRepo: UserAuthRepository) : super() {
        mUserAuthRepo = userAuthRepo
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : super() {
        //This is the original user authentication repo.
        mUserAuthRepo = UserAuthRepositoryImpl()
    }

    /**
     * [LoginUiModel] to hold the response form the user authentication. UI element can
     * observe this [MutableLiveData] to change the state when user authentication succeed or fails.
     */
    internal val mLoginUiModel: MutableLiveData<LoginUiModel> = MutableLiveData()

    internal val mEmailError: SingleLiveEvent<ErrorMessage> = SingleLiveEvent()

    internal val mPasswordError: SingleLiveEvent<ErrorMessage> = SingleLiveEvent()

    internal val mNameError: SingleLiveEvent<ErrorMessage> = SingleLiveEvent()

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

                        mUserAuthRepo.signUp(SignUpRequest(email, name, password, null))
                                .doOnSubscribe({
                                    blockUi.postValue(true)
                                })
                                .doAfterTerminate({
                                    blockUi.value = false
                                })
                                .subscribe({
                                    //Save into the user session
                                    UserSessionManager.setNewSession(userId = it.uid,
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

                mUserAuthRepo.login(LoginRequest(email, password))
                        .doOnSubscribe({
                            blockUi.postValue(true)
                        })
                        .doAfterTerminate({
                            blockUi.value = false
                        })
                        .subscribe({
                            //Save into the user session
                            UserSessionManager.setNewSession(userId = it.uid,
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
        mUserAuthRepo.socialSignUp(requestData)
                .doOnSubscribe({
                    blockUi.postValue(true)
                })
                .doAfterTerminate({
                    blockUi.value = false
                })
                .subscribe({
                    //Save into the user session
                    UserSessionManager.setNewSession(userId = it.uid,
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
