package com.kevalpatel2106.standup.authentication.login

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthUser
import com.kevalpatel2106.network.consumer.NWErrorConsumer
import com.kevalpatel2106.network.consumer.NWSuccessConsumer
import com.kevalpatel2106.standup.authentication.repo.*
import com.kevalpatel2106.utils.UserSessionManager
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Keval on 19/11/17.
 * This is the [ViewModel] class for the [LoginActivity]. This will hold all the data for the activity
 * regardless of the lifecycle.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(LoginActivity::class)
internal class LoginViewModel : android.arch.lifecycle.ViewModel {

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
     * [CompositeDisposable] to hold all the disposables from Rx and repository.
     */
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    /**
     * Boolean to change the value when authentication API call starts/ends. So that UI can change
     * or enable/disable views.
     */
    internal val mIsAuthenticationRunning: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * [LoginUiModel] to hold the response form the user authentication. UI element can
     * observe this [MutableLiveData] to change the state when user authentication succeed or fails.
     */
    internal val mLoginUiModel: MutableLiveData<LoginUiModel> = MutableLiveData()

    init {
        mIsAuthenticationRunning.value = false
    }

    override fun onCleared() {
        super.onCleared()

        //Delete all the API connections.
        mCompositeDisposable.dispose()
    }

    /**
     * Authenticate new user while sign up.
     *
     * @param email Email of the new user.
     * @param password Password fo the user to send.
     * @param name Name of the user.
     */
    fun performSignUp(email: String, password: String, name: String) {
        mIsAuthenticationRunning.value = true
        mUserAuthRepo.signUp(SignUpRequest(email, name, password, null))
                .subscribe(object : NWSuccessConsumer<SignUpResponseData>() {

                    /**
                     * Success api response.
                     *
                     * @param data [SignUpResponseData]
                     */
                    override fun onSuccess(data: SignUpResponseData?) {
                        data?.let {
                            //Save into the user session
                            UserSessionManager.setNewSession(userId = data.uid,
                                    displayName = name,
                                    token = null,
                                    email = email,
                                    photoUrl = data.photoUrl,
                                    isVerified = it.isVerified)

                            val loginUiModel = LoginUiModel(true)
                            loginUiModel.isNewUser = data.isNewUser
                            loginUiModel.isVerify = data.isVerified
                            mLoginUiModel.value = loginUiModel
                        }

                        mIsAuthenticationRunning.value = false
                    }

                }, object : NWErrorConsumer() {

                    /**
                     * Implement this method the get the error when application cannot connect to the server or the
                     * internet is down.
                     */
                    override fun onInternetUnavailable(message: String) {
                        val loginUiModel = LoginUiModel(false)
                        loginUiModel.errorMsg = message
                        mLoginUiModel.value = loginUiModel

                        mIsAuthenticationRunning.value = false
                    }

                    /**
                     * Implement this method the get the error code for the request and get the message to show to
                     * to the user.
                     */
                    override fun onError(code: Int, message: String) {
                        val loginUiModel = LoginUiModel(false)
                        loginUiModel.errorMsg = message
                        mLoginUiModel.value = loginUiModel

                        mIsAuthenticationRunning.value = false
                    }
                })
    }

    /**
     * Authenticate user while login.
     *
     * @param email Email of the new user.
     * @param password Password fo the user to send.
     */
    fun performSignIn(email: String, password: String) {
        mIsAuthenticationRunning.value = true
        mUserAuthRepo.login(LoginRequest(email, password))
                .subscribe(object : NWSuccessConsumer<LoginResponseData>() {

                    /**
                     * Success api response.
                     *
                     * @param data [LoginResponseData]
                     */
                    override fun onSuccess(data: LoginResponseData?) {
                        data?.let {
                            //Save into the user session
                            UserSessionManager.setNewSession(userId = data.uid,
                                    displayName = data.name,
                                    token = null,
                                    email = data.email,
                                    photoUrl = data.photoUrl,
                                    isVerified = it.isVerified)

                            val loginUiModel = LoginUiModel(true)
                            mLoginUiModel.value = loginUiModel
                        }

                        mIsAuthenticationRunning.value = false
                    }

                }, object : NWErrorConsumer() {

                    /**
                     * Implement this method the get the error when application cannot connect to the server or the
                     * internet is down.
                     */
                    override fun onInternetUnavailable(message: String) {
                        val loginUiModel = LoginUiModel(false)
                        loginUiModel.errorMsg = message
                        mLoginUiModel.value = loginUiModel

                        mIsAuthenticationRunning.value = false
                    }

                    /**
                     * Implement this method the get the error code for the request and get the message to show to
                     * to the user.
                     */
                    override fun onError(code: Int, message: String) {
                        val loginUiModel = LoginUiModel(false)
                        loginUiModel.errorMsg = message
                        mLoginUiModel.value = loginUiModel

                        mIsAuthenticationRunning.value = false
                    }

                })
    }


    /**
     * Start the authentication flow for the user signed in using facebook.
     */
    @SuppressLint("VisibleForTests")
    fun authenticateSocialUser(fbUser: FacebookUser) = authenticateSocialUser(SignUpRequest(fbUser = fbUser))

    /**
     * Start the authentication flow for the user signed in using google.
     */
    @SuppressLint("VisibleForTests")
    fun authenticateSocialUser(googleUser: GoogleAuthUser) = authenticateSocialUser(SignUpRequest(googleUser = googleUser))

    /**
     * Authenticate user with google or facebook login.
     *
     * @param requestData Sign up request data.
     */
    @VisibleForTesting
    fun authenticateSocialUser(requestData: SignUpRequest) {
        mIsAuthenticationRunning.value = true

        mUserAuthRepo.socialSignUp(requestData)
                .subscribe(object : NWSuccessConsumer<SignUpResponseData>() {

                    /**
                     * Success api response.
                     *
                     * @param data [SignUpResponseData]
                     */
                    override fun onSuccess(data: SignUpResponseData?) {

                        data?.let {
                            //Save into the user session
                            UserSessionManager.setNewSession(userId = data.uid,
                                    displayName = requestData.displayName,
                                    token = null,
                                    email = requestData.email,
                                    photoUrl = data.photoUrl,
                                    isVerified = it.isVerified)

                            val loginUiModel = LoginUiModel(true)
                            loginUiModel.isNewUser = data.isNewUser
                            mLoginUiModel.value = loginUiModel
                        }

                        mIsAuthenticationRunning.value = false
                    }

                }, object : NWErrorConsumer() {

                    /**
                     * Implement this method the get the error when application cannot connect to the server or the
                     * internet is down.
                     */
                    override fun onInternetUnavailable(message: String) {
                        val loginUiModel = LoginUiModel(false)
                        loginUiModel.errorMsg = message
                        mLoginUiModel.value = loginUiModel

                        mIsAuthenticationRunning.value = false
                    }

                    /**
                     * Implement this method the get the error code for the request and get the message to show to
                     * to the user.
                     */
                    override fun onError(code: Int, message: String) {
                        val loginUiModel = LoginUiModel(false)
                        loginUiModel.errorMsg = message
                        mLoginUiModel.value = loginUiModel

                        mIsAuthenticationRunning.value = false
                    }
                })
    }
}
