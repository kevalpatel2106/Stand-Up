package com.kevalpatel2106.standup.authentication.intro

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthUser
import com.kevalpatel2106.standup.authentication.repo.SignUpRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.utils.UserSessionManager
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Keval on 19/11/17.
 * This is the [ViewModel] class for the [IntroActivity]. This will hold all the data for the activity
 * regardless of the lifecycle.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(IntroActivity::class)
internal class IntroViewModel : android.arch.lifecycle.ViewModel {

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

    /**
     * [IntroUiModel] to hold the response form the user authentication. UI element can
     * observe this [MutableLiveData] to change the state when user authentication succeed or fails.
     */
    internal val mIntroUiModel: MutableLiveData<IntroUiModel> = MutableLiveData()

    init {
        mIsAuthenticationRunning.value = false
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
     * Authenticate the user using the social signin.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun authenticateSocialUser(requestData: SignUpRequest) {
        mIsAuthenticationRunning.value = true

        mUserAuthRepo.socialSignUp(requestData)
                .subscribe({ data ->
                    if (data != null) {
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
                    }

                    mIsAuthenticationRunning.value = false
                }, { t ->
                    val apiResponse = IntroUiModel(false)
                    apiResponse.errorMsg = t.message
                    mIntroUiModel.value = apiResponse

                    mIsAuthenticationRunning.value = false
                })
    }

    override fun onCleared() {
        super.onCleared()

        //Delete all the API connections.
        mCompositeDisposable.dispose()
    }
}
