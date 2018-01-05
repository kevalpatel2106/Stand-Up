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
import butterknife.OnClick
import com.kevalpatel2106.base.annotations.UIController
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.base.uiController.showSnack
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.constants.AnalyticsEvents
import com.kevalpatel2106.standup.constants.logEvent
import com.kevalpatel2106.standup.main.MainActivity
import com.kevalpatel2106.standup.misc.SUUtils
import com.kevalpatel2106.utils.UserSessionManager
import kotlinx.android.synthetic.main.activity_verify_email.*

@UIController
class VerifyEmailActivity : BaseActivity() {

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

    @VisibleForTesting
    internal lateinit var mModel: VerifyEmailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)
        setViewModel()

        verify_description_text.text = String.format(getString(R.string.verify_email_screen_message),
                UserSessionManager.email)
    }

    private fun setViewModel() {
        mModel = ViewModelProviders.of(this).get(VerifyEmailViewModel::class.java)

        mModel.blockUi.observe(this@VerifyEmailActivity, Observer<Boolean> {
            verify_btn_skip.isEnabled = !it!!
            verify_btn_resend.isEnabled = !it
            verify_btn_open_mail_btn.isEnabled = !it
        })

        //Observe error messages
        mModel.errorMessage.observe(this@VerifyEmailActivity, Observer {
            it!!.getMessage(this@VerifyEmailActivity)?.let {
                showSnack(message = it, actionName = R.string.error_retry_try_again,
                        duration = Snackbar.LENGTH_LONG,
                        actionListener = View.OnClickListener { onResendEmail() })
            }
        })

        mModel.mUiModel.observe(this@VerifyEmailActivity, Observer<VerifyEmailUiModel> {
            logEvent(AnalyticsEvents.EVENT_RESEND_VERIFICATION_MAIL)

            it?.let {
                if (it.isSuccess) {
                    showSnack(message = getString(R.string.message_verification_email_sent), duration = Snackbar.LENGTH_LONG)
                }
            }
        })
    }

    @OnClick(R.id.verify_btn_skip)
    fun onSkip() {
        MainActivity.launch(this@VerifyEmailActivity)
    }


    @OnClick(R.id.verify_btn_resend)
    fun onResendEmail() {
        mModel.resendEmail(UserSessionManager.userId)
    }

    @OnClick(R.id.verify_btn_open_mail_btn)
    fun onOpenEmail() {
        SUUtils.openEmailDialog(this@VerifyEmailActivity)
    }
}
