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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.design.widget.Snackbar
import android.view.View
import android.view.WindowManager
import butterknife.OnClick
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.common.base.uiController.showSnack
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.SUUtils
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.authentication.di.DaggerUserAuthComponent
import com.kevalpatel2106.standup.constants.AnalyticsEvents
import com.kevalpatel2106.standup.constants.logEvent
import com.kevalpatel2106.standup.main.MainActivity
import com.kevalpatel2106.utils.annotations.UIController
import com.kevalpatel2106.utils.getColorCompat
import kotlinx.android.synthetic.main.activity_verify_email.*
import javax.inject.Inject


@UIController
class VerifyEmailActivity : BaseActivity() {

    @Inject lateinit var userSessionManager: UserSessionManager

    @VisibleForTesting
    internal lateinit var model: VerifyEmailViewModel

    private var resendingSnackbar: Snackbar? = null

    init {
        DaggerUserAuthComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@VerifyEmailActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Make the status bar gray
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColorCompat(R.color.colorWindowBackgroundDark)

        setContentView(R.layout.activity_verify_email)
        setViewModel()

        verify_description_text.text = String.format(getString(R.string.verify_email_screen_message),
                userSessionManager.email)
    }

    private fun setViewModel() {
        model = ViewModelProviders.of(this).get(VerifyEmailViewModel::class.java)

        model.blockUi.observe(this@VerifyEmailActivity, Observer<Boolean> {
            verify_btn_skip.isEnabled = !it!!
            verify_btn_resend.isEnabled = !it
            verify_btn_open_mail_btn.isEnabled = !it
        })

        //Observe error messages
        model.errorMessage.observe(this@VerifyEmailActivity, Observer {
            it!!.getMessage(this@VerifyEmailActivity)?.let {
                showSnack(message = it, actionName = R.string.error_retry_try_again,
                        duration = Snackbar.LENGTH_LONG,
                        actionListener = View.OnClickListener { onResendEmail() })
            }
        })


        model.resendInProgress.observe(this@VerifyEmailActivity, Observer {
            it?.let {
                if (it) {
                    resendingSnackbar = showSnack(message = R.string.message_resending_verification_email,
                            duration = Snackbar.LENGTH_INDEFINITE)
                } else {
                    resendingSnackbar?.dismiss()
                }
            }
        })

        model.resendSuccessful.observe(this@VerifyEmailActivity, Observer {
            it?.let { if (it) showSnack(R.string.message_verification_email_sent) }
        })
    }

    @OnClick(R.id.verify_btn_skip)
    fun onSkip() {
        MainActivity.launch(this@VerifyEmailActivity)
        finish()
    }


    @OnClick(R.id.verify_btn_resend)
    fun onResendEmail() {
        logEvent(AnalyticsEvents.EVENT_RESEND_VERIFICATION_MAIL)
        model.resendEmail()
    }

    @OnClick(R.id.verify_btn_open_mail_btn)
    fun onOpenEmail() {
        SUUtils.openEmailDialog(this@VerifyEmailActivity)
    }


    companion object {
        /**
         * Launch the [VerifyEmailActivity].
         *
         * @param context Instance of the caller.
         */
        fun launch(context: Context) {
            val launchIntent = Intent(context, VerifyEmailActivity::class.java)
            context.startActivity(launchIntent)
        }
    }
}
