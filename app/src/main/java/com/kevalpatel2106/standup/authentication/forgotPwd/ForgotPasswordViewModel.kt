package com.kevalpatel2106.standup.authentication.forgotPwd

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.standup.authentication.repo.ForgotPasswordRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    mIsAuthenticationRunning.value = false

                    val model = ForgotPasswordUiModel(true)
                    mUiModel.value = model
                }, { t ->
                    mIsAuthenticationRunning.value = false

                    val model = ForgotPasswordUiModel(false)
                    model.errorMsg = t.message
                    mUiModel.value = model
                })
    }
}