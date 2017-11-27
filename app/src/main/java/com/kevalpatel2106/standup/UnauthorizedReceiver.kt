package com.kevalpatel2106.standup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.kevalpatel2106.standup.authentication.logout.Logout

/**
 * This receiver will listen for the action "com.kevalpatel2106.network.unauthorized" action broadcast
 * from the network parser. This will clear the user session and logs out the user from the
 * application in case of unauthorized access.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */
class UnauthorizedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //Toast the user.
        Toast.makeText(context,
                context.getString(R.string.error_unauthorised_response),
                Toast.LENGTH_LONG).show()

        //Clear the session
        Logout.clearSession(context)

        //Launch the splash screen
        context.startActivity(SplashActivity.getLaunchIntent(context))
    }
}
