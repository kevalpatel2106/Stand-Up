package com.kevalpatel2106.standup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.activityengine.ActivityDetector
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.standup.authentication.deviceReg.RegisterDeviceService
import com.kevalpatel2106.standup.authentication.intro.IntroActivity
import com.kevalpatel2106.standup.authentication.verifyEmail.VerifyEmailActivity
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.dashboard.Dashboard
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

        if (!UserSessionManager.isUserLoggedIn) {
            IntroActivity.launch(this@SplashActivity)
        } else if (!UserSessionManager.isUserVerified) {
            VerifyEmailActivity.launch(this@SplashActivity)
        } else {
            //Start activity detection.
            ActivityDetector.startDetection()

            //Sync the device token
            if (!SharedPrefsProvider.getBoolFromPreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED))
                RegisterDeviceService.start(this@SplashActivity)

            //Launch the dashboard.
            Dashboard.launch(this@SplashActivity)
        }
    }
}
