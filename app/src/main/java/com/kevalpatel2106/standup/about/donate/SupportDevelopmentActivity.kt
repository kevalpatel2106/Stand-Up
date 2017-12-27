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

package com.kevalpatel2106.standup.about.donate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import butterknife.OnClick
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.SUUtils

/**
 * Created by Kevalpatel2106 on 18-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SupportDevelopmentActivity : BaseActivity() {

    private lateinit var mModel: SupportDevelopmentViewModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mModel = SupportDevelopmentViewModel()

        setContentView(R.layout.activity_support_developement)
        setToolbar(R.id.toolbar, "", true)
    }

    @OnClick(R.id.donate_two_dollar_card)
    fun donate2Dollar() {
        SUUtils.openLink(this@SupportDevelopmentActivity,
                getString(mModel.getDonationLink(2)))
    }

    @OnClick(R.id.donate_five_dollar_card)
    fun donate5Dollar() {
        SUUtils.openLink(this@SupportDevelopmentActivity,
                getString(mModel.getDonationLink(5)))
    }

    @OnClick(R.id.donate_ten_dollar_card)
    fun donate10Dollar() {
        SUUtils.openLink(this@SupportDevelopmentActivity,
                getString(mModel.getDonationLink(10)))
    }

    @OnClick(R.id.donate_twenty_dollar_card)
    fun donate20Dollar() {
        SUUtils.openLink(this@SupportDevelopmentActivity,
                getString(mModel.getDonationLink(20)))
    }
}