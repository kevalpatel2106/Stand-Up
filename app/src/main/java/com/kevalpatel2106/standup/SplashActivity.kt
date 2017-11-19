package com.kevalpatel2106.standup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.standup.constants.SharedPrefranceKeys
import com.kevalpatel2106.standup.intro.IntroActivity
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

        if (!SharedPrefsProvider.getBoolFromPreferences(SharedPrefranceKeys.IS_APP_INTRO_DISPLAYED)) {
            IntroActivity.launch(this@SplashActivity)
        } else if (!UserSessionManager.isUserLoggedIn) {
            //TODO Open the login activity
        } else {
            //Launch the dashboard.
            Dashboard.launch(this@SplashActivity)
        }
    }
}
