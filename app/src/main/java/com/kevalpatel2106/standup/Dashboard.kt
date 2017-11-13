package com.kevalpatel2106.standup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class Dashboard : AppCompatActivity() {

    companion object {

        /**
         * Launch the [Dashboard] activity.
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, Dashboard::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }
}
