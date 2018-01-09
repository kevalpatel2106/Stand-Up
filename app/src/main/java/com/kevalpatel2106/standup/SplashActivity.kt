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

package com.kevalpatel2106.standup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.standup.authentication.deviceReg.DeviceRegisterActivity
import com.kevalpatel2106.standup.authentication.intro.IntroActivity
import com.kevalpatel2106.standup.authentication.verification.VerifyEmailActivity
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.main.MainActivity
import com.kevalpatel2106.standup.profile.EditProfileActivity
import com.kevalpatel2106.standup.reminder.activityMonitor.ActivityMonitorService
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import javax.inject.Inject

/**
 * This is the launch activity for the application. It handles redirection logic for login, notifications
 * and dashboard screens.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */
class SplashActivity : BaseActivity() {

    @Inject lateinit var userSessionManager: UserSessionManager
    @Inject lateinit var sharedPrefProvider: SharedPrefsProvider

    companion object {

        /**
         * Launch the application in the new task.
         */
        fun getLaunchIntent(context: Context): Intent {
            val launchIntent = Intent(context, SplashActivity::class.java)
            launchIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent
                    .FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            return launchIntent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!userSessionManager.isUserLoggedIn) {   //User is not logged in. Complete the authentication flow.
            IntroActivity.launch(this@SplashActivity)
        } else {    //User is logged in.

            //If the device is not registered, register the device with the server.
            if (!sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED)) {
                DeviceRegisterActivity.launch(this@SplashActivity,
                        false,
                        userSessionManager.isUserVerified)
            }

            //Continue with the app launch flow.
            if (!userSessionManager.isUserVerified) {   //If user is not verified, open verify screen.

                VerifyEmailActivity.launch(this@SplashActivity)
            } else if (userSessionManager.displayName.isNullOrEmpty()) {   //User profile is not complete.

                EditProfileActivity.launch(this@SplashActivity)
            } else {    //All looks good.

                //Start activity monitoring if not running
                ActivityMonitorService.scheduleMonitoringJob(this@SplashActivity)

                //Launch the dashboard.
                MainActivity.launch(this@SplashActivity)
            }
        }
    }
}
