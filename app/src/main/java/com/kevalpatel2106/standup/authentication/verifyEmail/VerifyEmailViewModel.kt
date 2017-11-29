package com.kevalpatel2106.standup.authentication.verifyEmail

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.standup.authentication.repo.ResendVerificationRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import io.reactivex.disposables.CompositeDisposable

@com.kevalpatel2106.base.annotations.ViewModel(VerifyEmailActivity::class)
internal class VerifyEmailViewModel : ViewModel {
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
     * [VerifyEmailUiModel] to hold the response form the user resend api calls. UI element can
     * observe this [MutableLiveData] to change the state when user authentication succeed or fails.
     */
    @VisibleForTesting
    internal val mUiModel = MutableLiveData<VerifyEmailUiModel>()

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

    fun resendEmail(userId: Long) {
        mIsAuthenticationRunning.value = true
        mUserAuthRepo.resendVerifyEmail(ResendVerificationRequest(userId))
                .subscribe({
                    mIsAuthenticationRunning.value = false

                    val model = VerifyEmailUiModel(true)
                    mUiModel.value = model
                }, { t ->
                    mIsAuthenticationRunning.value = false

                    val model = VerifyEmailUiModel(false)
                    model.errorMsg = t.message
                    mUiModel.value = model
                })
    }
}
