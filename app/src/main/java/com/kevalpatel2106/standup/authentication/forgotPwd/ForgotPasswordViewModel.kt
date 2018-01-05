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

package com.kevalpatel2106.standup.authentication.forgotPwd

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.base.arch.SingleLiveEvent
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.repo.ForgotPasswordRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.standup.misc.Validator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kevalpatel2106 on 23-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(ForgotPasswordActivity::class)
class ForgotPasswordViewModel : BaseViewModel {
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

    internal val mEmailError: SingleLiveEvent<ErrorMessage> = SingleLiveEvent()

    fun forgotPasswordRequest(email: String) {
        if (Validator.isValidEmail(email)) {
            addDisposable(mUserAuthRepo
                    .forgotPassword(ForgotPasswordRequest(email))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe({
                        blockUi.postValue(true)
                    })
                    .doAfterTerminate({
                        blockUi.value = false
                    })
                    .subscribe({
                        mUiModel.value = ForgotPasswordUiModel(true)
                    }, { t ->
                        mUiModel.value = ForgotPasswordUiModel(false)
                        errorMessage.value = ErrorMessage(t.message)
                    }))
        } else {
            mEmailError.value = ErrorMessage(R.string.error_login_invalid_email)
        }
    }
}