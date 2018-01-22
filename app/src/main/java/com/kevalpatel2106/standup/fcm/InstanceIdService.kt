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

package com.kevalpatel2106.standup.fcm

import com.google.firebase.iid.FirebaseInstanceIdService
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.standup.authentication.deviceReg.RegisterDeviceService
import javax.inject.Inject

/**
 * Created by Keval on 01-Jun-17.
 * This service handles the instance id refresh.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see FirebaseInstanceIdService
 */

class InstanceIdService : FirebaseInstanceIdService() {

    @Inject lateinit var userSessionManager: UserSessionManager

    override fun onCreate() {
        super.onCreate()

        DaggerFcmComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@InstanceIdService)
    }

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        //Start syncing the new token
        if (userSessionManager.isUserLoggedIn) RegisterDeviceService.start(this)
    }

}
