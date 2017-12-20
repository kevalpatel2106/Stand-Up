package com.kevalpatel2106.standup.authentication.verification

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.authentication.repo.ResendVerificationRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@com.kevalpatel2106.base.annotations.ViewModel(VerifyEmailActivity::class)
internal class VerifyEmailViewModel : BaseViewModel {

    /**
     * Repository to provide user authentications.
     */
    @VisibleForTesting
    internal var mUserAuthRepo: UserAuthRepository

    /**
     * [VerifyEmailUiModel] to hold the response form the user resend api calls. UI element can
     * observe this [MutableLiveData] to change the state when user authentication succeed or fails.
     */
    @VisibleForTesting
    internal val mUiModel = MutableLiveData<VerifyEmailUiModel>()

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
     * Resend the verification email to the given [userId].
     */
    fun resendEmail(userId: Long) {
        mUserAuthRepo.resendVerifyEmail(ResendVerificationRequest(userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({
                    blockUi.postValue(true)
                })
                .doAfterTerminate({
                    blockUi.value = false
                })
                .subscribe({
                    mUiModel.value = VerifyEmailUiModel(true)
                }, {
                    mUiModel.value = VerifyEmailUiModel(false)
                    errorMessage.value = ErrorMessage(it.message)
                })
    }
}
