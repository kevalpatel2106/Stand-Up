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

package com.kevalpatel2106.standup.authentication.forgotPwd

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.annotation.VisibleForTesting
import android.view.View
import butterknife.OnClick
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.base.uiController.showSnack
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.constants.AnalyticsEvents
import com.kevalpatel2106.standup.constants.logEvent
import com.kevalpatel2106.standup.misc.SUUtils
import com.kevalpatel2106.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {

    @VisibleForTesting
    internal lateinit var model: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(this).get(ForgotPasswordViewModel::class.java)
        setContentView(R.layout.activity_forgot_password)

        model.isRequesting.observe(this@ForgotPasswordActivity, Observer<Boolean> {
            it?.let { forgot_password_submit_btn.displayLoader(it) }
        })

        //Observe error messages
        model.errorMessage.observe(this@ForgotPasswordActivity, Observer {
            it!!.getMessage(this@ForgotPasswordActivity)?.let { showSnack(it) }
        })

        //Set email error
        model.emailError.observe(this@ForgotPasswordActivity, Observer {
            forgot_password_email_et.error = it!!.getMessage(this@ForgotPasswordActivity)
        })

        //Observer the api responses.
        model.isForgotRequestSuccessful.observe(this@ForgotPasswordActivity, Observer<Boolean> {
            it?.let {
                if (it) {
                    showSnack(getString(R.string.forgot_password_successful),
                            getString(R.string.btn_title_open_mail), View.OnClickListener { SUUtils.openEmailDialog(this@ForgotPasswordActivity) })

                    //Finish after snack bar complete
                    Handler().postDelayed({ finish() }, 3500)
                }
            }
        })
        setToolbar(R.id.include, getString(R.string.title_activity_forgot_password), true)
    }

    @OnClick(R.id.forgot_password_submit_btn)
    fun submit() {
        //Log analytics
        val bundle = Bundle()
        bundle.putString(AnalyticsEvents.KEY_EMAIL, forgot_password_email_et.getTrimmedText())
        logEvent(AnalyticsEvents.EVENT_FORGOT_PASSWORD, bundle)

        ViewUtils.hideKeyboard(forgot_password_email_et)

        model.forgotPasswordRequest(forgot_password_email_et.getTrimmedText())
    }


    companion object {

        /**
         * Launch the [ForgotPasswordActivity].
         *
         * @param context Instance of the caller.
         */
        fun launch(context: Context) {
            context.startActivity(Intent(context, ForgotPasswordActivity::class.java))
        }
    }
}
