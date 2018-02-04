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

package com.standup.app.authentication.verification

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.annotation.VisibleForTesting
import android.view.View
import android.widget.Toast
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.annotations.UIController
import com.standup.R
import com.standup.app.misc.LottieJson
import com.standup.app.misc.playAnotherAnimation
import com.standup.app.misc.playRepeatAnimation
import com.standup.app.splash.SplashActivity
import kotlinx.android.synthetic.main.activity_email_link_verification.*

/**
 * Created by Keval on 27/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@UIController
class EmailLinkVerificationActivity : BaseActivity() {

    internal lateinit var model: EmailLinkVerifyViewModel

    private var allowBackPress: Boolean = false

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_link_verification)
        setModel()

        //Make an GET api call
        model.verifyEmail(intent.getStringExtra(ARG_URL))
    }

    private fun setModel() {
        model = ViewModelProviders.of(this@EmailLinkVerificationActivity)
                .get(EmailLinkVerifyViewModel::class.java)

        model.blockUi.observe(this@EmailLinkVerificationActivity, Observer {
            it?.let {
                if (it) {
                    verify_email_link_description_tv.text = getString(R.string.verify_email_link_description_verifying)
                    verify_email_link_progressbar.visibility = View.VISIBLE

                    verify_email_link_logo.playRepeatAnimation(LottieJson.ANIMATING_CLOCK)

                    allowBackPress = false
                } else {
                    verify_email_link_progressbar.visibility = View.INVISIBLE
                }
            }
        })

        model.errorMessage.observe(this@EmailLinkVerificationActivity, Observer {
            //Error
            Handler().postDelayed({ setError(it) }, 1000)
        })
        model.emailVerified.observe(this@EmailLinkVerificationActivity, Observer {
            //Success
            Handler().postDelayed({ setSuccess() }, 1000)
        })
    }

    private fun setSuccess() {
        verify_email_link_description_tv.text = getString(R.string.verify_email_link_success)
        verify_email_link_progressbar.visibility = View.INVISIBLE

        //Change the verify logo
        verify_email_link_logo.playAnotherAnimation(LottieJson.VERIFIED_BADGE)


        //Go to the email link activity
        Handler().postDelayed({
            SplashActivity.launch(this@EmailLinkVerificationActivity)
        }, 2000)
    }

    private fun setError(it: ErrorMessage?) {
        verify_email_link_description_tv.text = if (it == null) {
            getString(R.string.verify_email_link_fail)
        } else {
            it.getMessage(this@EmailLinkVerificationActivity)
        }

        //Change the verify logo
        verify_email_link_logo.playAnotherAnimation(it?.errorImage ?: LottieJson.WARNING)

        allowBackPress = true
    }

    override fun onBackPressed() {
        if (allowBackPress) super.onBackPressed()
    }

    companion object {

        @VisibleForTesting
        const val ARG_URL = "arg_url"

        /**
         * Launch the [EmailLinkVerificationActivity].
         *
         * @param context Instance of the caller.
         * @param url [Uri] received in the deep link
         * @return True if the activity opened.
         */
        fun launch(context: Context, url: Uri): Boolean {

            //Validate the url
            if (url.pathSegments.size != 3) {
                Toast.makeText(context, R.string.error_invalid_verification_link, Toast.LENGTH_LONG).show()
                return false
            }

            //Check if the user is verified
            if (UserSessionManager(SharedPrefsProvider(context)).isUserVerified) {
                Toast.makeText(context, R.string.error_user_already_verified, Toast.LENGTH_LONG).show()
                return false
            }

            val launchIntent = Intent(context, EmailLinkVerificationActivity::class.java)
            launchIntent.putExtra(ARG_URL, url.toString())
            context.startActivity(launchIntent)
            return true
        }
    }
}
