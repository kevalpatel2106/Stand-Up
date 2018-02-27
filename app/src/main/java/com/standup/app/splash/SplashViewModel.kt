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

package com.standup.app.splash

import android.arch.lifecycle.MutableLiveData
import com.kevalpatel2106.common.prefs.SharedPreferenceKeys
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.annotations.ViewModel
import com.standup.core.Core
import javax.inject.Inject

/**
 * Created by Keval on 02/02/18.
 * A [ViewModel] class for the [SplashActivity].
 *
 * This view model is responsible for
 * - Initializing the navigation from the splash screen by calling [initiateFlow].
 * - Refreshing the [Core] by calling [setUpCore].
 *
 * @constructor that provides [UserSessionManager], [SharedPrefsProvider] and [Core].
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@ViewModel(SplashActivity::class)
internal class SplashViewModel @Inject constructor(private val userSessionManager: UserSessionManager,
                                                   private val sharedPrefProvider: SharedPrefsProvider,
                                                   private val core: Core) {

    internal val openIntro = MutableLiveData<Boolean>()

    internal val openDeviceRegister = MutableLiveData<Boolean>()

    internal val openVerifyEmail = MutableLiveData<Boolean>()

    internal val openProfile = MutableLiveData<Boolean>()

    internal val openDashboard = MutableLiveData<Boolean>()

    init {
        openIntro.value = false
        openDeviceRegister.value = false
        openVerifyEmail.value = false
        openProfile.value = false
        openDashboard.value = false
    }

    /**
     * Refresh the [Core] and start syncing the user activity if the background sync is enabled.
     *
     * @see Core.refresh
     * @see Core.forceSync
     */
    fun setUpCore() {
        core.refresh()
        Core.forceSync()
    }

    fun initiateFlow() {
        if (!userSessionManager.isUserLoggedIn) {

            //User is not logged in. Complete the authentication flow.
            openIntro.value = true
        } else {    //User is logged in.

            //If the device is not registered, register the device with the server.
            if (!sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED)) {

                openDeviceRegister.value = true
            } else if (!userSessionManager.isUserVerified) {

                //If user is not verified, open verify screen.
                openVerifyEmail.value = true
            } else if (userSessionManager.displayName.isNullOrEmpty()) {

                //User profile is not complete.
                openProfile.value = true
            } else {
                //All looks good.
                //Launch the dashboard.
                openDashboard.value = true
            }
        }
    }
}
