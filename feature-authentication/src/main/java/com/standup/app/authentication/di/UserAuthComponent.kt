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

package com.standup.app.authentication.di

import com.kevalpatel2106.common.application.di.AppComponent
import com.kevalpatel2106.common.application.di.ApplicationScope
import com.standup.app.authentication.UnauthorizedReceiver
import com.standup.app.authentication.deviceReg.DeviceRegViewModel
import com.standup.app.authentication.deviceReg.DeviceRegisterActivity
import com.standup.app.authentication.forgotPwd.ForgotPasswordViewModel
import com.standup.app.authentication.intro.IntroViewModel
import com.standup.app.authentication.login.LoginViewModel
import com.standup.app.authentication.verification.EmailLinkVerificationActivity
import com.standup.app.authentication.verification.EmailLinkVerifyViewModel
import com.standup.app.authentication.verification.VerifyEmailActivity
import com.standup.app.authentication.verification.VerifyEmailViewModel
import dagger.Component

/**
 * Created by Kevalpatel2106 on 09-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ApplicationScope
@Component(dependencies = [AppComponent::class], modules = [UserAuthModule::class])
interface UserAuthComponent {

    fun inject(deviceRegViewModel: DeviceRegViewModel)

    fun inject(forgotPasswordViewModel: ForgotPasswordViewModel)

    fun inject(introViewModel: IntroViewModel)

    fun inject(loginViewModel: LoginViewModel)

    fun inject(verifyEmailViewModel: VerifyEmailViewModel)

    fun inject(emailLinkVerifyViewModel: EmailLinkVerifyViewModel)

    fun inject(unauthorizedReceiver: UnauthorizedReceiver)

    fun inject(verifyEmailActivity: VerifyEmailActivity)

    fun inject(deviceRegisterActivity: DeviceRegisterActivity)

    fun inject(emailLinkVerificationActivity: EmailLinkVerificationActivity)
}
