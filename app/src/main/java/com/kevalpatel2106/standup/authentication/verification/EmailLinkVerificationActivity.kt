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

package com.kevalpatel2106.standup.authentication.verification

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
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import com.kevalpatel2106.base.annotations.UIController
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.intro.IntroActivity
import kotlinx.android.synthetic.main.activity_email_link_verification.*

/**
 * Created by Keval on 27/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@UIController
class EmailLinkVerificationActivity : BaseActivity() {

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
                Toast.makeText(context, "Invalid verification link.", Toast.LENGTH_LONG).show()
                return false
            }

            val launchIntent = Intent(context, EmailLinkVerificationActivity::class.java)
            launchIntent.putExtra(ARG_URL, url.toString())
            context.startActivity(launchIntent)
            return true
        }
    }

    internal lateinit var model: EmailLinkVerifyViewModel

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_link_verification)

        model = ViewModelProviders.of(this@EmailLinkVerificationActivity)
                .get(EmailLinkVerifyViewModel::class.java)

        model.blockUi.observe(this@EmailLinkVerificationActivity, Observer {
            it?.let {
                if (it) {
                    verify_email_link_description_tv.text = getString(R.string.verify_email_link_description_verifying)
                    verify_email_link_progressbar.visibility = View.VISIBLE
                } else {
                    verify_email_link_progressbar.visibility = View.INVISIBLE
                }
            }
        })

        model.errorMessage.observe(this@EmailLinkVerificationActivity, Observer {
            //Error
            verify_email_link_description_tv.text = if (it == null) {
                getString(R.string.verify_email_link_fail)
            } else {
                it.getMessage(this@EmailLinkVerificationActivity)
            }

            //Change the verify logo
            verify_email_link_logo.setImageResource(it?.errorImage ?: R.drawable.ic_warning)
            verify_email_link_logo.scaleX = 0.6f
            verify_email_link_logo.scaleY = 0.6f
            verify_email_link_logo.animate()
                    .setDuration(500)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .start()

            //Go to the email link activity
            Handler().postDelayed({
                IntroActivity.launch(this@EmailLinkVerificationActivity, true)
            }, 3000)
        })

        model.emailVerified.observe(this@EmailLinkVerificationActivity, Observer {
            //Success
            verify_email_link_description_tv.text = getString(R.string.verify_email_link_success)

            //Change the verify logo
            verify_email_link_logo.setImageResource(R.drawable.ic_right_tick)
            verify_email_link_logo.scaleX = 0.6f
            verify_email_link_logo.scaleY = 0.6f
            verify_email_link_logo.animate()
                    .setDuration(500)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .start()

            //Go to the email link activity
            Handler().postDelayed({
                IntroActivity.launch(this@EmailLinkVerificationActivity, true)
            }, 3000)
        })

        //Make an GET api call
        model.verifyEmail(intent.getStringExtra(ARG_URL))
    }

    override fun onBackPressed() {
        //Do nothing
    }
}