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

package com.kevalpatel2106.standup.about.donate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import butterknife.OnClick
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.SUUtils
import com.kevalpatel2106.utils.annotations.UIController

/**
 * Created by Kevalpatel2106 on 18-Dec-17.
 * A [BaseActivity] to display the support development screen. This screen contains options to
 * donate money via PayPal. User can donate any amount from [DonationAmount].
 * TODO : Convert PayPal donations to IAP.
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
    }

    /**
     * Method handles donate 2 dollar button click.
     */
    @OnClick(R.id.donate_two_dollar_card)
    fun donate2Dollar() {

        //Open the PayPal link
        SUUtils.openLink(this@SupportDevelopmentActivity, getString(model.getDonationLink(2)))
    }

    /**
     * Method handles donate 5 dollar button click.
     */
    @OnClick(R.id.donate_five_dollar_card)
    fun donate5Dollar() {

        //Open the PayPal link
        SUUtils.openLink(this@SupportDevelopmentActivity, getString(model.getDonationLink(5)))
    }

    /**
     * Method handles donate 10 dollar button click.
     */
    @OnClick(R.id.donate_ten_dollar_card)
    fun donate10Dollar() {

        //Open the PayPal link
        SUUtils.openLink(this@SupportDevelopmentActivity, getString(model.getDonationLink(10)))
    }

    /**
     * Method handles donate 20 dollar button click. Awesome!!! :-)
     */
    @OnClick(R.id.donate_twenty_dollar_card)
    fun donate20Dollar() {

        //Open the PayPal link
        SUUtils.openLink(this@SupportDevelopmentActivity, getString(model.getDonationLink(20)))
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
