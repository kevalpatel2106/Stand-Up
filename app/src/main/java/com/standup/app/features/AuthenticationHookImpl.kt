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

import android.content.Context
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.standup.app.authentication.AuthenticationHook
import com.standup.app.features.di.DaggerFeatureComponent
import com.standup.app.main.MainActivity
import com.standup.app.profile.ProfileModule
import com.standup.app.splash.SplashActivity
import dagger.Lazy
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 20-Feb-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class AuthenticationHookImpl : AuthenticationHook {

    @Inject
    internal lateinit var profileModule: Lazy<ProfileModule>

    init {
        DaggerFeatureComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@AuthenticationHookImpl)
    }

    override fun openEditUserProfile(context: Context, userSessionManager: UserSessionManager) {
        profileModule.get().openProfile(context, userSessionManager)
    }

    override fun openMainScreen(context: Context) {
        MainActivity.launch(context)
    }

    override fun openSplashScreen(context: Context) {
        SplashActivity.launch(context)
    }

}
