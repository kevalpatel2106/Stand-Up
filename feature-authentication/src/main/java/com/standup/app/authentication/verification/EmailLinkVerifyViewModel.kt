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

package com.standup.app.authentication.verification

import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.CallbackEvent
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.misc.lottie.LottieJson
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.standup.app.authentication.di.DaggerUserAuthComponent
import com.standup.app.authentication.repo.UserAuthRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@com.kevalpatel2106.utils.annotations.ViewModel(VerifyEmailActivity::class)
internal class EmailLinkVerifyViewModel : BaseViewModel {

    /**
     * Repository to provide user authentications.
     */
    @Inject
    lateinit var userAuthRepo: UserAuthRepository

    @Inject
    lateinit var userSessionManager: UserSessionManager


    /**
     * Private constructor to add the custom [UserAuthRepository] for testing.
     *
     * @param userAuthRepo Add your own [UserAuthRepository].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(userAuthRepo: UserAuthRepository,
                userSessionManager: UserSessionManager) : super() {
        this.userAuthRepo = userAuthRepo
        this.userSessionManager = userSessionManager
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() {
        DaggerUserAuthComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@EmailLinkVerifyViewModel)
    }

    internal val emailVerified = CallbackEvent()

    internal fun verifyEmail(url: String) {
        addDisposable(userAuthRepo.verifyEmailLink(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate {
                    blockUi.value = false
                }
                .doOnSubscribe {
                    blockUi.value = true
                }
                .subscribe({
                    emailVerified.dispatch()

                    //Change the flag to true.
                    userSessionManager.isUserVerified = true
                }, {
                    val errorMsg = ErrorMessage(it.message)
                    errorMsg.errorImage = LottieJson.WARNING
                    this.errorMessage.value = errorMsg
                }))
    }
}
