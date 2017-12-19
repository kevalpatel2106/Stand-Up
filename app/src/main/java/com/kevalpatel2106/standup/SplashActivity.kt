package com.kevalpatel2106.standup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.standup.authentication.deviceReg.DeviceRegisterActivity
import com.kevalpatel2106.standup.authentication.intro.IntroActivity
import com.kevalpatel2106.standup.authentication.verifyEmail.VerifyEmailActivity
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.dashboard.DashboardActivity
import com.kevalpatel2106.standup.profile.EditProfileActivity
import com.kevalpatel2106.standup.reminder.scheduler.ReminderScheduler
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager

/**
 * This is the launch activity for the application. It handles redirection logic for login, notifications
 * and dashboard screens.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */
class SplashActivity : BaseActivity() {

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

        if (!UserSessionManager.isUserLoggedIn) {   //User is not logged in. Complete the authentication flow.
            IntroActivity.launch(this@SplashActivity)
        } else {    //User is logged in.

            //If the device is not registered, register the device with the server.
            if (!SharedPrefsProvider.getBoolFromPreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED)) {
                DeviceRegisterActivity.launch(this@SplashActivity,
                        false,
                        UserSessionManager.isUserVerified)
            }

            //Contrinue with the app launch flow.
            if (!UserSessionManager.isUserVerified) {   //If user is not verified, open verify screen.
                VerifyEmailActivity.launch(this@SplashActivity)

            } else if (UserSessionManager.displayName.isNullOrEmpty()) {   //User profile is not complete.
                EditProfileActivity.launch(this@SplashActivity)

            } else {    //All looks good.
                //Start the detection engine
                ReminderScheduler.startSchedulerIfNotRunning(this.applicationContext)

                //Launch the dashboard.
                DashboardActivity.launch(this@SplashActivity)
            }
        }
    }
}
