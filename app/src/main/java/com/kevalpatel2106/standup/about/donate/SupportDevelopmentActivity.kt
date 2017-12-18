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
        setContentView(R.layout.activity_support_developement)
        setToolbar(R.id.toolbar, "", true)
    }

    @OnClick(R.id.donate_two_dollar_card)
    fun donate2Dollar() {
        SUUtils.openLink(this@SupportDevelopmentActivity, getString(R.string.donate_2_dollar_link))
    }

    @OnClick(R.id.donate_five_dollar_card)
    fun donate5Dollar() {
        SUUtils.openLink(this@SupportDevelopmentActivity, getString(R.string.donate_5_dollar_link))
    }

    @OnClick(R.id.donate_ten_dollar_card)
    fun donate10Dollar() {
        SUUtils.openLink(this@SupportDevelopmentActivity, getString(R.string.donate_10_dollar_link))
    }

    @OnClick(R.id.donate_twenty_dollar_card)
    fun donate20Dollar() {
        SUUtils.openLink(this@SupportDevelopmentActivity, getString(R.string.donate_20_dollar_link))
    }
}