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
internal class ForgotPasswordViewModel @VisibleForTesting constructor(private val userAuthRepo: UserAuthRepository)
    : BaseViewModel() {

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : this(UserAuthRepositoryImpl())

    internal val isRequesting = MutableLiveData<Boolean>()

    internal val isForgotRequestSuccessful = MutableLiveData<Boolean>()

    internal val emailError: SingleLiveEvent<ErrorMessage> = SingleLiveEvent()

    init {
        isForgotRequestSuccessful.value = false
        isRequesting.value = false
    }

    fun forgotPasswordRequest(email: String) {
        if (Validator.isValidEmail(email)) {
            addDisposable(userAuthRepo
                    .forgotPassword(ForgotPasswordRequest(email))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe({
                        isForgotRequestSuccessful.value = false
                        blockUi.postValue(true)
                        isRequesting.value = true
                    })
                    .doAfterTerminate({
                        blockUi.value = false
                        isRequesting.value = false
                    })
                    .subscribe({
                        isForgotRequestSuccessful.value = true
                    }, { t ->
                        errorMessage.value = ErrorMessage(t.message)
                    }))
        } else {
            emailError.value = ErrorMessage(R.string.error_login_invalid_email)
        }
    }
}