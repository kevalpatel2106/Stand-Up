package com.kevalpatel2106.standup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kevalpatel2106.utils.UserSessionManager

/**
 * This receiver will listen for the action "com.kevalpatel2106.network.unauthorized" action broadcast
 * from the network parser. This will clear the user session and logs out the user from the
 * application in case of unauthorized access.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */
class UnauthorizedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //Clear the user session.
        UserSessionManager.clearUserSession()

        //Launch the splash screen
        context.startActivity(SplashActivity.getLaunchIntent(context))
    }
}
