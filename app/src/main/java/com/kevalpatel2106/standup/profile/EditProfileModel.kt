/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.kevalpatel2106.standup.profile

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.BaseApplication
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.misc.Validator
import com.kevalpatel2106.standup.profile.di.DaggerProfileComponent
import com.kevalpatel2106.standup.profile.repo.GetProfileResponse
import com.kevalpatel2106.standup.profile.repo.SaveProfileResponse
import com.kevalpatel2106.standup.profile.repo.UserProfileRepo
import com.kevalpatel2106.standup.profile.repo.UserProfileRepoImpl
import com.kevalpatel2106.utils.UserSessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Keval on 29/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@com.kevalpatel2106.base.annotations.ViewModel(EditProfileActivity::class)
class EditProfileModel : BaseViewModel {

    /**
     * Repository to provide user profile information.
     *
     * @see UserProfileRepo
     */
    @Inject lateinit var userProfileRepo: UserProfileRepo

    @Inject lateinit var userSessionManager: UserSessionManager

    /**
     * Private constructor to add the custom [UserProfileRepo] for testing.
     *
     * @param userProfileRepo Add your own [UserProfileRepo].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(userProfileRepo: UserProfileRepo, userSessionManager: UserSessionManager) : super() {
        this.userProfileRepo = userProfileRepo
        this.userSessionManager = userSessionManager

        init()
    }

    /**
     * Zero parameter constructor. This will set the [UserProfileRepoImpl] as the [UserProfileRepo].
     */
    @Suppress("unused")
    constructor() {
        DaggerProfileComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@EditProfileModel)

        init()
    }

    /**
     * Current user profile information. This information will be loaded while initiating this
     * class by calling [loadMyProfile].
     */
    internal val userProfile: MutableLiveData<GetProfileResponse> = MutableLiveData()

    /**
     * The response from received from the [saveMyProfile] call. Activity can monitor this to get
     * buildNotification when the profile saved on the server.
     */
    internal val profileUpdateStatus: MutableLiveData<SaveProfileResponse> = MutableLiveData()

    /**
     * Boolean wil be true if the user profile is currently loading.
     *
     * @see loadMyProfile
     */
    internal val isLoadingProfile: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Boolean to e true if the user profile is being saved on the server and cache.
     *
     * @see saveMyProfile
     */
    internal val isSavingProfile: MutableLiveData<Boolean> = MutableLiveData()

    @SuppressLint("VisibleForTests")
    private fun init() {
        isSavingProfile.value = false
        isLoadingProfile.value = false

        //Start loading user profile.
        loadMyProfile()
    }

    /**
     * Load the user profile of the current user from the shared prefrance cache and refresh the cache
     * from te network.
     */
    @VisibleForTesting
    internal fun loadMyProfile() {
        addDisposable(userProfileRepo.getUserProfile(userSessionManager.userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({
                    isLoadingProfile.postValue(true)
                })
                .doAfterTerminate({
                    isLoadingProfile.value = false
                })
                .subscribe({
                    isLoadingProfile.value = false
                    userProfile.value = it
                }, {
                    //Error occurred.
                    errorMessage.value = ErrorMessage(it.message)
                }))
    }

    /**
     * Save the current user information such as [name], [photo], [height], [weight] and [isMale]
     * to the repository.
     *
     * @param name Name of the user
     * @param photo Url of the user picture.
     * @param height Height of the user in cms.
     * @param weight Weight if the user in kgs.
     * @param isMale True if the user is male.
     */
    internal fun saveMyProfile(name: String,
                               photo: String?,
                               height: Float,
                               weight: Float,
                               isMale: Boolean) {
        if (isSavingProfile.value!!) return

        //Validate the user name
        if (!Validator.isValidName(name)) {
            errorMessage.value = ErrorMessage(R.string.error_login_invalid_name)
            return
        }

        //Validate the height
        if (!Validator.isValidHeight(height)) {
            errorMessage.value = ErrorMessage(R.string.error_invalid_height)
            return
        }

        //Validate the weight
        if (!Validator.isValidWeight(weight)) {
            errorMessage.value = ErrorMessage(R.string.error_invalid_weight)
            return
        }

        //Make server call to save the data.
        //NOTE: Don't add this disposable to composite one. We need to keep this api call running
        //even if the activity is destroyed.
        userProfileRepo.saveUserProfile(name, photo, height, weight, isMale)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({
                    isSavingProfile.postValue(true)
                })
                .doAfterTerminate({
                    isSavingProfile.value = false
                })
                .subscribe({
                    //Update the save profile status
                    profileUpdateStatus.value = it
                }, {
                    //Error occurred.
                    profileUpdateStatus.value = null
                    errorMessage.value = ErrorMessage(it.message)
                })
    }
}