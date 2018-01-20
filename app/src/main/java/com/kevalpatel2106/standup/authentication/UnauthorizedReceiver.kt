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

package com.kevalpatel2106.standup.authentication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.SplashActivity
import com.kevalpatel2106.standup.authentication.di.DaggerUserAuthComponent
import com.kevalpatel2106.standup.authentication.logout.Logout
import com.kevalpatel2106.standup.constants.AnalyticsEvents
import com.kevalpatel2106.standup.constants.logEvent
import javax.inject.Inject

/**
 * This receiver will listen for the action "com.kevalpatel2106.network.unauthorized" action broadcast
 * from the network parser. This will clear the user session and logs out the user from the
 * application in case of unauthorized access.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */
class UnauthorizedReceiver : BroadcastReceiver() {

    @Inject lateinit var logout : Logout

    override fun onReceive(context: Context, intent: Intent) {
        context.logEvent(AnalyticsEvents.EVENT_UNAUTHORIZED_FORCE_LOGOUT)

        //Toast the user.
        Toast.makeText(context,
                context.getString(R.string.error_unauthorised_response),
                Toast.LENGTH_LONG).show()

        DaggerUserAuthComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@UnauthorizedReceiver)

        //Clear the session
        logout.clearSession()

        //Launch the splash screen
        context.startActivity(SplashActivity.getLaunchIntent(context))
    }
}
