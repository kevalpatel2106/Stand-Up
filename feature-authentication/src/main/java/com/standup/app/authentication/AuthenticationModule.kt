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

package com.standup.app.authentication

import android.content.Context
import android.net.Uri
import com.standup.app.authentication.deviceReg.DeviceRegisterActivity
import com.standup.app.authentication.deviceReg.RegisterDeviceService
import com.standup.app.authentication.intro.IntroActivity
import com.standup.app.authentication.logout.Logout
import com.standup.app.authentication.verification.EmailLinkVerificationActivity
import com.standup.app.authentication.verification.EmailVerifiedNotification
import com.standup.app.authentication.verification.VerifyEmailActivity

/**
 * Created by Kevalpatel2106 on 20-Feb-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object AuthenticationModule {

    internal lateinit var authenticationHook: AuthenticationHook

    fun init(authenticationHook: AuthenticationHook) {
        this.authenticationHook = authenticationHook
    }

    fun openIntroScreen(context: Context, isNewTask: Boolean = false) {
        IntroActivity.launch(context, isNewTask)
    }

    fun registerDevice(context: Context, isNewUser: Boolean, isVerified: Boolean) {
        DeviceRegisterActivity.launch(context, isNewUser, isVerified)
    }

    fun registerDeviceSilently(context: Context) {
        RegisterDeviceService.start(context)
    }

    fun openVerifyEmailScreen(context: Context) {
        VerifyEmailActivity.launch(context)
    }

    fun verifyEmailLink(context: Context, verificationLink: Uri) {
        EmailLinkVerificationActivity.launch(context, verificationLink)
    }

    fun fireEmailVerifiedNotification(context: Context, message: String?) {
        EmailVerifiedNotification.notify(context, message)
    }

    fun logout() {
        Logout().logout()
    }
}
