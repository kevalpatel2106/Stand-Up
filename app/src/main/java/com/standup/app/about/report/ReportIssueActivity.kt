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

package com.standup.app.about.report

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import butterknife.OnClick
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.common.base.uiController.showSnack
import com.kevalpatel2106.common.base.uiController.showToast
import com.kevalpatel2106.utils.Utils
import com.kevalpatel2106.utils.alert
import com.standup.R
import com.standup.app.misc.SUUtils
import kotlinx.android.synthetic.main.activity_report_issue.*


class ReportIssueActivity : BaseActivity() {

    private lateinit var model: ReportIssueViewModel
    private var reportingSnackbar: Snackbar? = null

    companion object {

        /**
         * Launch the [ReportIssueActivity] activity.
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context) {
            val launchIntent = Intent(context, ReportIssueActivity::class.java)
            context.startActivity(launchIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_report_issue)

        setToolbar(R.id.toolbar, "", true)

        //Se the view model
        setModel()

        if (savedInstanceState == null) {
            //Check for the update automatically
            model.checkForUpdate()
        }
    }

    @OnClick(R.id.btn_prefer_emailing_us)
    fun shootAnEmail() {
        SUUtils.sendEmail(this@ReportIssueActivity,
                report_issue_title_et.getTrimmedText(),
                report_issue_description_et.getTrimmedText(),
                getString(R.string.support_email))
    }

    private fun setModel() {
        model = ViewModelProviders.of(this@ReportIssueActivity).get(ReportIssueViewModel::class.java)
        model.errorMessage.observe(this@ReportIssueActivity, Observer {
            it!!.getMessage(this@ReportIssueActivity)?.let { showSnack(it) }
        })
        model.versionUpdateResult.observe(this@ReportIssueActivity, Observer {
            it?.let {
                //Display the dialog
                alert(messageResource = R.string.report_issue_update_message,
                        titleResource = R.string.report_issue_update_title,
                        func = {
                            negativeButton(getString(R.string.report_issue_update_later_btn_title), {
                                //Do nothing
                            })
                            positiveButton(getString(R.string.report_issue_update_btn_title), {
                                //Open the play store.
                                SUUtils.openLink(this@ReportIssueActivity, getString(R.string.rate_app_url))
                            })
                        })
            }
        })
        model.issueId.observe(this@ReportIssueActivity, Observer {
            it?.let {
                alert(message = String.format(getString(R.string.issue_success_alert_message), it),
                        title = getString(R.string.issue_success_alert_title),
                        func = {
                            positiveButton(android.R.string.ok, {
                                //Do nothing

                                finish()
                            })
                            negativeButton(android.R.string.copy, {
                                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Issue id:", it.toString())
                                clipboard.primaryClip = clip

                                showToast(R.string.copied_to_clip_board)

                                finish()
                            })
                        })
            }
        })
        model.blockUi.observe(this@ReportIssueActivity, Observer {
            it?.let {
                submit_issue_btn.isEnabled = !it

                if (it) {
                    //Display the snackbar
                    reportingSnackbar = showSnack(message = R.string.report_issue_reporting_message,
                            duration = Snackbar.LENGTH_INDEFINITE)
                } else {
                    //Dismiss snackbar
                    reportingSnackbar?.dismiss()
                }
            }
        })
    }

    @OnClick(R.id.submit_issue_btn)
    fun reportIssue() {
        model.reportIssue(report_issue_title_et.getTrimmedText(),
                report_issue_description_et.getTrimmedText(),
                Utils.getDeviceId(this@ReportIssueActivity))
    }
}
