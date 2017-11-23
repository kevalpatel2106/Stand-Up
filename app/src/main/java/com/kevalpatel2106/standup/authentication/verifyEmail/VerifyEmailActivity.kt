package com.kevalpatel2106.standup.authentication.verifyEmail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kevalpatel2106.standup.Dashboard
import com.kevalpatel2106.standup.R

class VerifyEmailActivity : AppCompatActivity() {

    companion object {
        /**
         * Launch the [VerifyEmailActivity].
         *
         * @param context Instance of the caller.
         */
        fun launch(context: Context) {
            val launchIntent = Intent(context, VerifyEmailActivity::class.java)
            context.startActivity(launchIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        //TODO set the view.
        Dashboard.launch(this@VerifyEmailActivity)
    }
}
