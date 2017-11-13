package com.kevalpatel2106.standup

import android.os.Bundle
import com.kevalpatel2106.base.BaseActivity

/**
 * This is the launch activity for the application. It handles redirection logic for login, notifications
 * and dashboard screens.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */
class SplashActivity : BaseActivity() {
    override fun runItForFirstCreation() {
        //Do nothing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO Logic for login navigation.
        //Launch the dashboard.
        Dashboard.launch(this@SplashActivity)
    }
}
