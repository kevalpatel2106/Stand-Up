package com.kevalpatel2106.standup.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.standup.R

class SettingsActivity : BaseActivity() {


    companion object {

        /**
         * Launch the [SettingsActivity].
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context) {
            val launchIntent = Intent(context, SettingsActivity::class.java)
            context.startActivity(launchIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
}
