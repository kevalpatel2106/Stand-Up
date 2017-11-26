package com.kevalpatel2106.standup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kevalpatel2106.activityengine.ActivityDetector

/**
 * Main activity which user will see after opening the application.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */
class Dashboard : AppCompatActivity() {

    companion object {

        /**
         * Launch the [Dashboard] activity.
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context) {
            val launchIntent = Intent(context, Dashboard::class.java)
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(launchIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        ActivityDetector(applicationContext).startDetection()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityDetector(applicationContext).stopDetection()
    }
}
