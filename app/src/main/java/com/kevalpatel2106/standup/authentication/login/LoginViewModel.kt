package com.kevalpatel2106.standup.authentication.login

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.network.consumer.NWErrorConsumer
import com.kevalpatel2106.network.consumer.NWSuccessConsumer
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.standup.authentication.signUp.SignUpRequest
import com.kevalpatel2106.standup.authentication.signUp.SignUpResponseData
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
    private val mUserAuthRepo: UserAuthRepository

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

    init {
        mIsAuthenticationRunning.value = false
    }

    override fun onCleared() {
        super.onCleared()

        //Delete all the API connections.
        mCompositeDisposable.dispose()
    }

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
                        mIsAuthenticationRunning.value = false
                    }

                }, object : NWErrorConsumer() {

                    /**
                     * Implement this method the get the error when application cannot connect to the server or the
                     * internet is down.
                     */
                    override fun onInternetUnavailable(message: String) {
                        mIsAuthenticationRunning.value = false
                    }

                    /**
                     * Implement this method the get the error code for the request and get the message to show to
                     * to the user.
                     */
                    override fun onError(code: Int, message: String) {
                        mIsAuthenticationRunning.value = false
                    }

                })
    }

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
                        mIsAuthenticationRunning.value = false
                    }

                }, object : NWErrorConsumer() {

                    /**
                     * Implement this method the get the error when application cannot connect to the server or the
                     * internet is down.
                     */
                    override fun onInternetUnavailable(message: String) {
                        mIsAuthenticationRunning.value = false
                    }

                    /**
                     * Implement this method the get the error code for the request and get the message to show to
                     * to the user.
                     */
                    override fun onError(code: Int, message: String) {
                        mIsAuthenticationRunning.value = false
                    }

                })
    }
}
