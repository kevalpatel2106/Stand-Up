package com.kevalpatel2106.standup.authentication.intro

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthUser
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.repo.SignUpRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.utils.UserSessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Keval on 19/11/17.
 * This is the [ViewModel] class for the [IntroActivity]. This will hold all the data for the activity
 * regardless of the lifecycle.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(IntroActivity::class)
internal class IntroViewModel : BaseViewModel {

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
     * [IntroUiModel] to hold the response form the user authentication. UI element can
     * observe this [MutableLiveData] to change the state when user authentication succeed or fails.
     */
    internal val mIntroUiModel: MutableLiveData<IntroUiModel> = MutableLiveData()

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
     * Authenticate the user using the social signin.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun authenticateSocialUser(requestData: SignUpRequest) {
        addDisposable(mUserAuthRepo.socialSignUp(requestData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({
                    blockUi.postValue(true)
                })
                .doAfterTerminate({
                    blockUi.value = false
                })
                .subscribe({ data ->
                    //Save into the user session
                    UserSessionManager.setNewSession(userId = data.uid,
                            displayName = requestData.displayName,
                            token = null,
                            email = requestData.email,
                            photoUrl = data.photoUrl,
                            isVerified = data.isVerified)

                    val introUiModel = IntroUiModel(true)
                    introUiModel.isNewUser = data.isNewUser
                    mIntroUiModel.value = introUiModel
                }, { t ->
                    //Update the activity
                    mIntroUiModel.value = IntroUiModel(false)
                    errorMessage.value = ErrorMessage(t.message)
                }))
    }
}