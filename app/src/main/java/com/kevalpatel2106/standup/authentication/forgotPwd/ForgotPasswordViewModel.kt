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
import com.kevalpatel2106.standup.BaseApplication
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.di.DaggerUserAuthComponent
import com.kevalpatel2106.standup.authentication.repo.ForgotPasswordRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.misc.Validator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 23-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(ForgotPasswordActivity::class)
class ForgotPasswordViewModel : BaseViewModel {

    @Inject lateinit var userAuthRepo: UserAuthRepository

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() {
        DaggerUserAuthComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@ForgotPasswordViewModel)
    }

    @VisibleForTesting
    constructor(userAuthRepo: UserAuthRepository) {
        this.userAuthRepo = userAuthRepo
    }

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
