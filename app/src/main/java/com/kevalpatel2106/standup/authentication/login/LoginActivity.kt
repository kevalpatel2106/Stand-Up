package com.kevalpatel2106.standup.authentication.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kevalpatel2106.base.annotations.UIController
import com.kevalpatel2106.standup.R

@UIController
class LoginActivity : AppCompatActivity() {

    companion object {

        /**
         * Launch the [LoginActivity].
         *
         * @param context Instance of the caller.
         */
        fun launch(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


    }
}
