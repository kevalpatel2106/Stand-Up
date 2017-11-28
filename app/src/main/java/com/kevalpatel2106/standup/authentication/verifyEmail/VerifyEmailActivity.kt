package com.kevalpatel2106.standup.authentication.verifyEmail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.design.widget.Snackbar
import android.view.View
import butterknife.OnClick
import com.cocosw.bottomsheet.BottomSheet
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.standup.dashboard.Dashboard
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.utils.UserSessionManager
import com.kevalpatel2106.utils.Utils
import com.kevalpatel2106.utils.showSnack
import kotlinx.android.synthetic.main.activity_verify_email.*


class VerifyEmailActivity : BaseActivity() {

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

    @VisibleForTesting
    internal lateinit var mModel: VerifyEmailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get(VerifyEmailViewModel::class.java)
        mModel.mIsAuthenticationRunning.observe(this@VerifyEmailActivity, Observer<Boolean> {
            verify_btn_skip.isEnabled = !it!!
            verify_btn_resend.isEnabled = !it
            verify_btn_open_mail_btn.isEnabled = !it
        })
        mModel.mUiModel.observe(this@VerifyEmailActivity, Observer<VerifyEmailUiModel> {
            it?.let {
                if (it.isSuccess) {
                    showSnack(message = getString(R.string.message_verification_email_sent), duration = Snackbar.LENGTH_LONG)
                } else it.errorMsg?.let {
                    showSnack(message = it, actionName = R.string.error_retry_try_again, duration = Snackbar.LENGTH_LONG,
                            actionListener = View.OnClickListener { onResendEmail() })
                }
            }
        })

        setContentView(R.layout.activity_verify_email)

        verify_description_text.text = String.format(getString(R.string.verify_email_screen_message),
                UserSessionManager.email)
    }

    @OnClick(R.id.verify_btn_skip)
    fun onSkip() {
        Dashboard.launch(this@VerifyEmailActivity)
    }


    @OnClick(R.id.verify_btn_resend)
    fun onResendEmail() {
        mModel.resendEmail(UserSessionManager.userId)
    }

    @OnClick(R.id.verify_btn_open_mail_btn)
    fun onOpenEmail() {
        val bottomSheet = BottomSheet.Builder(this@VerifyEmailActivity).title("Open mail")

        //Get the list of email clients.
        val emailAppsList = Utils.getEmailApplications(packageManager)

        //Add each items to the bottom sheet
        for (i in 0 until emailAppsList.size) {
            val s = emailAppsList[i]
            Utils.getApplicationName(s.activityInfo.packageName, packageManager)?.let {
                bottomSheet.sheet(i, s.loadIcon(packageManager), it)
            }
        }

        //On clicking any item, open the email application
        bottomSheet.listener { _, pos ->
            startActivity(packageManager.getLaunchIntentForPackage(emailAppsList[pos].activityInfo.packageName))
        }
        bottomSheet.build()
        bottomSheet.show()
    }
}