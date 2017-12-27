/*
 *  Copyright 2017 Keval Patel.
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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.kevalpatel2106.standup.authentication.logout.Logout
import com.kevalpatel2106.standup.constants.AnalyticsEvents
import com.kevalpatel2106.standup.constants.logEvent

/**
 * This receiver will listen for the action "com.kevalpatel2106.network.unauthorized" action broadcast
 * from the network parser. This will clear the user session and logs out the user from the
 * application in case of unauthorized access.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */
class UnauthorizedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.logEvent(AnalyticsEvents.EVENT_UNAUTHORIZED_FORCE_LOGOUT)

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
