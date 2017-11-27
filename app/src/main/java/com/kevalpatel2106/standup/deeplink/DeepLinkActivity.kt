package com.kevalpatel2106.standup.deeplink

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.standup.SplashActivity
import com.kevalpatel2106.standup.authentication.verifyEmail.EmailLinkVerificationActivity
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

        val action = intent.action

        if (action == Intent.ACTION_VIEW) {
            val data = intent.data
            when (data.pathSegments[0]) {
                "verifyEmailLink" -> {  //Verify the email link
                    val isOpened = EmailLinkVerificationActivity.launch(context = this@DeepLinkActivity, url = data)
                    if (!isOpened) openLink(data.toString())
                }
                "forgotPasswordLink" -> { //The password reset link
                    openLink(data.toString())
                }
                else -> {
                    openLink(data.toString())
                }
            }
        } else {
            openLink(intent.data.toString())
        }
        finish()
    }

    @VisibleForTesting
    fun openLink(linkToOpen: String) {
        WebViewActivity.launch(this@DeepLinkActivity, linkToOpen, false)
    }

}