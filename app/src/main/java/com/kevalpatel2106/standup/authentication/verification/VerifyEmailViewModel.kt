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

package com.kevalpatel2106.standup.authentication.verification

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.base.arch.SingleLiveEvent
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
    internal var userAuthRepo: UserAuthRepository

    internal val resendInProgress = MutableLiveData<Boolean>()

    internal val resendSuccessful = SingleLiveEvent<Boolean>()

    /**
     * Private constructor to add the custom [UserAuthRepository] for testing.
     *
     * @param userAuthRepo Add your own [UserAuthRepository].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(userAuthRepo: UserAuthRepository) : super() {
        this.userAuthRepo = userAuthRepo
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : super() {
        //This is the original user authentication repo.
        userAuthRepo = UserAuthRepositoryImpl()
    }

    /**
     * Resend the verification email to the given [userId].
     */
    fun resendEmail(userId: Long) {
        userAuthRepo.resendVerifyEmail(ResendVerificationRequest(userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({
                    blockUi.postValue(true)
                    resendInProgress.value = true
                })
                .doAfterTerminate({
                    blockUi.value = false
                    resendInProgress.value = false
                })
                .subscribe({
                    resendSuccessful.value = true
                }, {
                    errorMessage.value = ErrorMessage(it.message)
                })
    }
}
