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

package com.kevalpatel2106.standup.splash

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.standup.authentication.deviceReg.DeviceRegisterActivity
import com.kevalpatel2106.standup.authentication.intro.IntroActivity
import com.kevalpatel2106.standup.authentication.verification.VerifyEmailActivity
import com.kevalpatel2106.standup.main.MainActivity
import com.kevalpatel2106.standup.profile.EditProfileActivity
import javax.inject.Inject

/**
 * This is the launch activity for the application. It handles redirection logic for login, notifications
 * and dashboard screens.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class SplashActivity : BaseActivity() {

    @Inject
    lateinit var model: SplashViewModel

    @Inject
    lateinit var userSessionManager: UserSessionManager

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

        //Inject
        DaggerSplashComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SplashActivity)

        setModel()

        model.initiateFlow()
    }


    private fun setModel() {

        model.openIntro.observe(this@SplashActivity, Observer {
            it?.let {
                if (it) {
                    IntroActivity.launch(this@SplashActivity)
                    finish()
                }
            }
        })

        model.openDeviceRegister.observe(this@SplashActivity, Observer {
            it?.let {
                if (it) {
                    DeviceRegisterActivity.launch(context = this@SplashActivity,
                            isNewUser = false,
                            isVerified = userSessionManager.isUserVerified)
                    finish()
                }
            }
        })

        model.openVerifyEmail.observe(this@SplashActivity, Observer {
            it?.let {
                if (it) {
                    VerifyEmailActivity.launch(this@SplashActivity)
                    finish()
                }
            }
        })

        model.openProfile.observe(this@SplashActivity, Observer {
            it?.let {
                if (it) {
                    EditProfileActivity.launch(this@SplashActivity, userSessionManager)
                    finish()
                }
            }
        })

        model.openDashboard.observe(this@SplashActivity, Observer {
            it?.let {
                if (it) {
                    MainActivity.launch(this@SplashActivity)
                    finish()
                }
            }
        })
    }
}
