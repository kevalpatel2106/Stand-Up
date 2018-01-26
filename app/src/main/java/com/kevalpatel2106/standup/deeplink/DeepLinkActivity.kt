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

package com.kevalpatel2106.standup.deeplink

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.standup.BuildConfig
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.SplashActivity
import com.kevalpatel2106.standup.authentication.verification.EmailLinkVerificationActivity
import com.kevalpatel2106.utils.SharedPrefsProvider

/**
 * Created by Kevalpatel2106 on 27-Nov-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class DeepLinkActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
    }

    @SuppressLint("VisibleForTests")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        //Check if the user is logged in?
        if (!UserSessionManager(SharedPrefsProvider(this@DeepLinkActivity)).isUserLoggedIn) {
            startActivity(SplashActivity.getLaunchIntent(this@DeepLinkActivity))
            return
        }

        val uri = intent.data

        when {
            uri.toString() == getString(R.string.invitation_deep_link) -> {

                //Firebase app invite link
                //Open the splash
                startActivity(SplashActivity.getLaunchIntent(this@DeepLinkActivity))
                finish()
            }
            uri.toString().startsWith(prefix = BuildConfig.BASE_URL, ignoreCase = false) -> {
                //This link is from our servers only
                when (uri.pathSegments[0]) {
                    "verifyEmailLink" -> {  //Verify the email link
                        EmailLinkVerificationActivity.launch(context = this@DeepLinkActivity, url = uri)
                    }
                    "forgotPasswordLink" -> { //The password reset link
                        openLink(uri.toString())
                    }
                    else -> {
                        openLink(uri.toString())
                    }
                }
            }
            else -> {
                //Don't know what's the link for
                openLink(intent.data.toString())
            }
        }
        finish()
    }

    private fun openLink(linkToOpen: String) {
        WebViewActivity.launch(this@DeepLinkActivity, linkToOpen, false)
    }

}
