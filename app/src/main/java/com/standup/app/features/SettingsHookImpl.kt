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

package com.standup.app.features

import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.utils.annotations.OnlyForTesting
import com.standup.app.authentication.AuthenticationApi
import com.standup.app.features.di.DaggerFeatureComponent
import com.standup.app.settings.SettingsHook
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 20-Feb-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class SettingsHookImpl : SettingsHook {

    @Inject
    lateinit var mAuthenticationApi: AuthenticationApi

    @OnlyForTesting
    @VisibleForTesting
    constructor(authenticationApi: AuthenticationApi) {
        this.mAuthenticationApi = authenticationApi
    }

    constructor() {
        DaggerFeatureComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SettingsHookImpl)
    }

    override fun logout() {
        mAuthenticationApi.logout()
    }

}
