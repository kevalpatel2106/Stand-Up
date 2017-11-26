package com.kevalpatel2106.standup.authentication.forgotPwd

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.network.consumer.NWErrorConsumer
import com.kevalpatel2106.network.consumer.NWSuccessConsumer
import com.kevalpatel2106.standup.authentication.repo.*
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Kevalpatel2106 on 23-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(ForgotPasswordActivity::class)
class ForgotPasswordViewModel : android.arch.lifecycle.ViewModel {

    /**
     * [CompositeDisposable] to hold all the disposables from Rx and repository.
     */
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    /**
     * Repository to provide user authentications.
     */
    @VisibleForTesting
    internal var mUserAuthRepo: UserAuthRepository

    /**
     * [ForgotPasswordUiModel] to hold the response form the user resend api calls. UI element can
     * observe this [MutableLiveData] to change the state when user authentication succeed or fails.
     */
    @VisibleForTesting
    internal val mUiModel = MutableLiveData<ForgotPasswordUiModel>()

    /**
     * Boolean to change the value when authentication API call starts/ends. So that UI can change
     * or enable/disable views.
     */
    @VisibleForTesting
    internal val mIsAuthenticationRunning: MutableLiveData<Boolean> = MutableLiveData()

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

    init {
        mIsAuthenticationRunning.value = false
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : super() {
        //This is the original user authentication repo.
        mUserAuthRepo = UserAuthRepositoryImpl()
    }


    override fun onCleared() {
        super.onCleared()

        //Delete all the API connections.
        mCompositeDisposable.dispose()
    }

    fun forgotPasswordRequest(email: String) {
        mIsAuthenticationRunning.value = true
        mUserAuthRepo.forgotPassword(ForgotPasswordRequest(email))
                .subscribe(object : NWSuccessConsumer<ForgotPasswordResponseData>() {

                    /**
                     * Success api response.
                     *
                     * @param data [ResendVerificationResponseData]
                     */
                    override fun onSuccess(data: ForgotPasswordResponseData?) {
                        mIsAuthenticationRunning.value = false

                        val model = ForgotPasswordUiModel(true)
                        mUiModel.value = model
                    }

                }, object : NWErrorConsumer() {
                    /**
                     * Implement this method the get the error when application cannot connect to the server or the
                     * internet is down.
                     */
                    override fun onInternetUnavailable(message: String) {
                        mIsAuthenticationRunning.value = false

                        val model = ForgotPasswordUiModel(false)
                        model.errorMsg = message
                        mUiModel.value = model
                    }

                    /**
                     * Implement this method the get the error code for the request and get the message to show to
                     * to the user.
                     */
                    override fun onError(code: Int, message: String) {
                        mIsAuthenticationRunning.value = false

                        val model = ForgotPasswordUiModel(false)
                        model.errorMsg = message
                        mUiModel.value = model
                    }
                })
    }
}