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

package com.standup.app.deeplink

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.Validator
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.base.arch.SingleLiveEvent
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.utils.annotations.OnlyForTesting
import com.kevalpatel2106.utils.annotations.ViewModel
import com.standup.app.BuildConfig
import com.standup.app.R
import java.net.URL
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 28-Feb-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(DeepLinkActivity::class)
class DeepLinkViewModel : BaseViewModel {

    companion object {
        @VisibleForTesting
        internal const val VERIFY_EMAIL_ENDPOINT = "verifyEmailLink"

        @VisibleForTesting
        internal const val RESET_PASSWORD_ENDPOINT = "forgotPasswordLink"
    }

    @Inject
    internal lateinit var userSessionManager: UserSessionManager

    @SuppressLint("VisibleForTests")
    @Suppress("unused")
    constructor() {
        DaggerDeepLinkComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@DeepLinkViewModel)
        init()
    }

    @OnlyForTesting
    @VisibleForTesting
    constructor(userSessionManager: UserSessionManager) {
        this.userSessionManager = userSessionManager
        init()
    }

    internal val fromInvitationLink = SingleLiveEvent<Boolean>()

    internal val verifyEmailLink = SingleLiveEvent<String>()

    internal val forgotPasswordLink = SingleLiveEvent<String>()

    internal val otherLink = SingleLiveEvent<String>()

    @VisibleForTesting
    internal fun init() {
        fromInvitationLink.value = false
    }

    /**
     * Check if the [DeepLinkActivity] should process the deep link.
     */
    fun checkIfDeepLinkAllowed(): Boolean {
        return userSessionManager.isUserLoggedIn
    }

    fun processIncomingLink(context: Context, incomingLink: String) {
        when {
            incomingLink == context.getString(R.string.invitation_deep_link) -> {
                fromInvitationLink.value = true
            }
            incomingLink.startsWith(prefix = BuildConfig.BASE_URL, ignoreCase = false) -> {
                //This link is from our servers only
                with(URL(incomingLink).path) {
                    when {
                        this.startsWith("/" + VERIFY_EMAIL_ENDPOINT) -> {  //Verify the email link

                            //Validate the url
                            if (!Validator.isValidVerificationLink(incomingLink)) {
                                errorMessage.value = ErrorMessage(R.string.error_invalid_verification_link)
                                return
                            }

                            //Check if the user is verified.
                            if (userSessionManager.isUserVerified) {
                                errorMessage.value = ErrorMessage(R.string.error_user_already_verified)
                                return
                            }

                            verifyEmailLink.value = incomingLink
                        }
                        this.startsWith("/" + RESET_PASSWORD_ENDPOINT) -> {
                            //The password reset link
                            forgotPasswordLink.value = incomingLink
                        }
                        else -> {
                            otherLink.value = incomingLink
                        }
                    }
                }
            }
            else -> {
                //Don't know what's the link for
                otherLink.value = incomingLink
            }
        }
    }
}
