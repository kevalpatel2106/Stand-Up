/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.about.report

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import butterknife.OnClick
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.SUUtils
import com.kevalpatel2106.utils.showSnack
import kotlinx.android.synthetic.main.activity_report_issue.*
import org.jetbrains.anko.alert

class ReportIssueActivity : BaseActivity() {

    private lateinit var model: ReportIssueViewModel
    private var versionItem: MaterialAboutActionItem? = null

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
        model = ViewModelProviders.of(this@ReportIssueActivity).get(ReportIssueViewModel::class.java)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_report_issue)
        setToolbar(R.id.toolbar, "", true)

        model.errorMessage.observe(this@ReportIssueActivity, Observer {
            it!!.getMessage(this@ReportIssueActivity)?.let { showSnack(it) }
        })
        model.versionUpdateResult.observe(this@ReportIssueActivity, Observer {
            it?.let {
                //Display the dialog
                val alertBuilder = alert(message = getString(R.string.report_issue_update_message),
                        title = getString(R.string.report_issue_update_title))
                alertBuilder.negativeButton(getString(R.string.report_issue_update_later_btn_title), {
                    //Do nothing
                })
                alertBuilder.positiveButton(getString(R.string.report_issue_update_btn_title), {
                    //Open the play store.
                    SUUtils.openLink(this@ReportIssueActivity, getString(R.string.rate_app_url))
                })
                alertBuilder.show()
            }
        })

        if (savedInstanceState == null) {
            //Check for the update automatically
            model.checkForUpdate(this@ReportIssueActivity)
        }
    }

    @OnClick(R.id.submit_issue_btn)
    fun reportIssue() {
        model.reportIssue(report_issue_title_et.getTrimmedText(), report_issue_description_et.getTrimmedText())
    }
}
