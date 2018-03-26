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

package com.standup.app.about.donate

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.utils.alert
import com.kevalpatel2106.utils.annotations.UIController
import com.kevalpatel2106.utils.copyToClipboard
import com.standup.app.about.R
import kotlinx.android.synthetic.main.activity_support_developement.*

/**
 * Created by Kevalpatel2106 on 18-Dec-17.
 * A [BaseActivity] to display the support development screen. This screen contains options to
 * donate money via PayPal. User can donate any amount from [DonationAmount].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@UIController
class SupportDevelopmentActivity : BaseActivity() {

    /**
     * View model for this activity.
     *
     * @see [SupportDevelopmentViewModel]
     */
    private lateinit var model: SupportDevelopmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = SupportDevelopmentViewModel()

        setContentView(R.layout.activity_support_developement)

        //Set the toolbar
        setToolbar(R.id.toolbar, "" /* Title is set in the xml. */, true)

        donate_two_dollar_card.setOnClickListener {
            model.donate2Dollar(this@SupportDevelopmentActivity)
        }

        donate_five_dollar_card.setOnClickListener {
            model.donate5Dollar(this@SupportDevelopmentActivity)
        }

        donate_ten_dollar_card.setOnClickListener {
            model.donate10Dollar(this@SupportDevelopmentActivity)
        }

        donate_twenty_dollar_card.setOnClickListener {
            model.donate20Dollar(this@SupportDevelopmentActivity)
        }

        model.donationOrderId.observe(this@SupportDevelopmentActivity, Observer {
            it?.let {

                alert(titleResource = R.string.donation_success_title,
                        messageResource = R.string.donation_success_message,
                        func = {
                            positiveButton(android.R.string.ok, {
                                //Do nothing
                                finish()
                            })
                            negativeButton(android.R.string.copy, {
                                copyToClipboard(it)
                                finish()
                            })
                        }
                ).show()
            }
        })
    }

    companion object {

        /**
         * Launch the [SupportDevelopmentActivity] activity.
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context) {
            val launchIntent = Intent(context, SupportDevelopmentActivity::class.java)
            context.startActivity(launchIntent)
        }
    }
}
