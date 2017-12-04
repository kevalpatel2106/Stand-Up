package com.kevalpatel2106.standup.profile

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.Validator
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.profile.repo.GetProfileResponse
import com.kevalpatel2106.standup.profile.repo.SaveProfileResponse
import com.kevalpatel2106.standup.profile.repo.UserProfileRepo
import com.kevalpatel2106.standup.profile.repo.UserProfileRepoImpl
import com.kevalpatel2106.utils.UserSessionManager

/**
 * Created by Keval on 29/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@com.kevalpatel2106.base.annotations.ViewModel(EditProfileActivity::class)
class EditProfileModel : BaseViewModel {

    /**
     * Repository to provide user authentications.
     */
    private val mUserProfileRepo: UserProfileRepo

    /**
     * Private constructor to add the custom [UserAuthRepository] for testing.
     *
     * @param userProfileRepo Add your own [UserAuthRepository].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(userProfileRepo: UserProfileRepo) : super() {
        mUserProfileRepo = userProfileRepo
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : super() {
        //This is the original user authentication repo.
        mUserProfileRepo = UserProfileRepoImpl()
    }

    /**
     * Current user profile information.
     */
    internal val mUserProfile: MutableLiveData<GetProfileResponse> = MutableLiveData()

    /**
     * Status of the save profile request.
     */
    internal val mProfileUpdateStatus: MutableLiveData<SaveProfileResponse> = MutableLiveData()

    internal val isLoadingProfile: MutableLiveData<Boolean> = MutableLiveData()

    internal val isSavingProfile: MutableLiveData<Boolean> = MutableLiveData()

    internal fun loadMyProfile() {
        mUserProfileRepo.getUserProfile(UserSessionManager.userId)
                .doOnSubscribe({
                    isLoadingProfile.postValue(true)
                })
                .doAfterTerminate({
                    isLoadingProfile.value = false
                })
                .subscribe({
                    mUserProfile.value = it
                }, {
                    errorMessage.value = ErrorMessage(it.message)
                })
    }

    internal fun saveMyProfile(name: String,
                               photo: String?,
                               height: Float,
                               weight: Float,
                               isMale: Boolean) {
        if (Validator.isValidName(name)) {
            errorMessage.value = ErrorMessage(R.string.error_login_invalid_email)
            return
        }

        if (Validator.isValidHeight(height)) {
            errorMessage.value = ErrorMessage(R.string.error_invalid_height)
            return
        }

        if (Validator.isValidWeight(weight)) {
            errorMessage.value = ErrorMessage(R.string.error_invalid_weight)
            return
        }

        mUserProfileRepo.saveUserProfile(name, photo, height, weight, isMale)
                .doOnSubscribe({
                    isSavingProfile.postValue(true)
                })
                .doAfterTerminate({
                    isSavingProfile.value = false
                })
                .subscribe({
                    mProfileUpdateStatus.value = it
                }, {
                    errorMessage.value = ErrorMessage(it.message)
                })
    }
}