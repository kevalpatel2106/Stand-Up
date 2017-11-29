package com.kevalpatel2106.standup.profile

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.profile.repo.GetProfileResponse
import com.kevalpatel2106.standup.profile.repo.UserProfileRepo
import com.kevalpatel2106.standup.profile.repo.UserProfileRepoImpl
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Keval on 29/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@com.kevalpatel2106.base.annotations.ViewModel(EditProfileActivity::class)
class EditProfileModel : ViewModel {


    /**
     * Repository to provide user authentications.
     */
    private val mUserAuthRepo: UserProfileRepo

    /**
     * Private constructor to add the custom [UserAuthRepository] for testing.
     *
     * @param userAuthRepo Add your own [UserAuthRepository].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(userAuthRepo: UserProfileRepo) : super() {
        mUserAuthRepo = userAuthRepo
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : super() {
        //This is the original user authentication repo.
        mUserAuthRepo = UserProfileRepoImpl()
    }

    /**
     * [CompositeDisposable] to hold all the disposables from Rx and repository.
     */
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    /**
     * Boolean to change the value when API call starts/ends. So that UI can change or enable/disable views.
     */
    internal val mIsApiRunning: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Current user profile information.
     */
    internal val mUserProfile: MutableLiveData<GetProfileResponse> = MutableLiveData()

    /**
     * Status of the save profile request.
     */
    internal val mProfileUpdateStatus: MutableLiveData<GetProfileResponse> = MutableLiveData()

    init {
        mIsApiRunning.value = false
    }

    internal fun loadMyProfile() {
        //TODO Call the repo and load the current user profile.
    }

    override fun onCleared() {
        super.onCleared()

        //Delete all the API connections.
        mCompositeDisposable.dispose()
    }
}