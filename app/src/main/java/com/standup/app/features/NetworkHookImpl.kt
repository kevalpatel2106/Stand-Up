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

import android.app.Application
import android.content.Context
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.network.NetworkHook
import com.standup.app.authentication.AuthenticationApi
import com.standup.app.features.di.DaggerFeatureComponent
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 01-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class NetworkHookImpl : NetworkHook {

    @Inject
    internal lateinit var mAuthenticationApi: AuthenticationApi

    @Inject
    internal lateinit var application: Application

    init {
        DaggerFeatureComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@NetworkHookImpl)
    }

    override fun onAuthenticationFailed() {
        mAuthenticationApi.logout()
    }

    override fun getContext(): Context = application


}
