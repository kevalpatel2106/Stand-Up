package com.kevalpatel2106.standup.about.donate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.standup.R

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

        //TODO Integrate donate button
    }
}