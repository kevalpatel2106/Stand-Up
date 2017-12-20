package com.kevalpatel2106.standup.deeplink

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.standup.BuildConfig
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.SplashActivity
import com.kevalpatel2106.standup.authentication.verification.EmailLinkVerificationActivity
import com.kevalpatel2106.utils.UserSessionManager

/**
 * Created by Kevalpatel2106 on 27-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
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
        if (!UserSessionManager.isUserLoggedIn) {
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
                        val isOpened = EmailLinkVerificationActivity.launch(context = this@DeepLinkActivity, url = uri)
                        if (!isOpened) openLink(uri.toString())
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