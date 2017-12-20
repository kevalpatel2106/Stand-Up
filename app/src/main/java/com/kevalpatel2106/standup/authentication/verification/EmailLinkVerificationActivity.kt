package com.kevalpatel2106.standup.authentication.verification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.annotation.VisibleForTesting
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import com.kevalpatel2106.base.annotations.UIController
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.intro.IntroActivity
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_email_link_verification.*

/**
 * Created by Keval on 27/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@UIController
class EmailLinkVerificationActivity : BaseActivity() {
    companion object {

        @VisibleForTesting
        const val ARG_URL = "arg_url"

        /**
         * Launch the [EmailLinkVerificationActivity].
         *
         * @param context Instance of the caller.
         * @param url [Uri] received in the deep link
         * @return True if the activity opened.
         */
        fun launch(context: Context, url: Uri): Boolean {

            //Validate the url
            if (url.pathSegments.size != 3) {
                Toast.makeText(context, "Invalid verification link.", Toast.LENGTH_LONG).show()
                return false
            }

            val launchIntent = Intent(context, EmailLinkVerificationActivity::class.java)
            launchIntent.putExtra(ARG_URL, url.toString())
            context.startActivity(launchIntent)
            return true
        }
    }

    @VisibleForTesting
    internal lateinit var mAuthRepo: UserAuthRepository

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_link_verification)

        mAuthRepo = UserAuthRepositoryImpl()

        //Make an GET api call
        verify_email_link_description_tv.text = getString(R.string.verify_email_link_description_verifying)
        verifyEmail(intent.getStringExtra(ARG_URL))
    }

    @VisibleForTesting
    internal fun verifyEmail(url: String) {
        verify_email_link_progressbar.visibility = View.VISIBLE
        addSubscription(mAuthRepo.verifyEmailLink(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //Success
                    verify_email_link_description_tv.text = getString(R.string.verify_email_link_success)
                    verify_email_link_progressbar.visibility = View.INVISIBLE

                    //Change the verify logo
                    verify_email_link_logo.setImageResource(R.drawable.ic_right_tick)
                    verify_email_link_logo.scaleX = 0.6f
                    verify_email_link_logo.scaleY = 0.6f
                    verify_email_link_logo.animate()
                            .setDuration(500)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            .start()

                    //Go to the email link activity
                    Handler().postDelayed({
                        IntroActivity.launch(this@EmailLinkVerificationActivity, true)
                    }, 3000)
                }, {
                    //Error
                    verify_email_link_description_tv.text = getString(R.string.verify_email_link_fail)
                    verify_email_link_progressbar.visibility = View.INVISIBLE

                    //Change the verify logo
                    verify_email_link_logo.setImageResource(R.drawable.ic_warning)
                    verify_email_link_logo.scaleX = 0.6f
                    verify_email_link_logo.scaleY = 0.6f
                    verify_email_link_logo.animate()
                            .setDuration(500)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            .start()

                    //Go to the email link activity
                    Handler().postDelayed({
                        IntroActivity.launch(this@EmailLinkVerificationActivity, true)
                    }, 3000)
                }))
    }

    override fun onBackPressed() {
        //Do nothing
    }
}