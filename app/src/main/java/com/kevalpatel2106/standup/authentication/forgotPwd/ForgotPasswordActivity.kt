package com.kevalpatel2106.standup.authentication.forgotPwd

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.annotation.VisibleForTesting
import android.view.View
import butterknife.OnClick
import com.cocosw.bottomsheet.BottomSheet
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.constants.AnalyticsEvents
import com.kevalpatel2106.standup.constants.logEvent
import com.kevalpatel2106.utils.Utils
import com.kevalpatel2106.utils.ViewUtils
import com.kevalpatel2106.utils.showSnack
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {

    companion object {

        /**
         * Launch the [ForgotPasswordActivity].
         *
         * @param context Instance of the caller.
         */
        fun launch(context: Context) {
            context.startActivity(Intent(context, ForgotPasswordActivity::class.java))
        }
    }

    @VisibleForTesting
    internal lateinit var mModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get(ForgotPasswordViewModel::class.java)
        setContentView(R.layout.activity_forgot_password)

        //Observer the api call changes
        mModel.blockUi.observe(this@ForgotPasswordActivity, Observer<Boolean> {
            forgot_password_submit_btn.isEnabled = !it!!
        })
        forgot_password_submit_btn.isEnabled = !mModel.blockUi.value!!

        //Observe error messages
        mModel.errorMessage.observe(this@ForgotPasswordActivity, Observer {
            it!!.getMessage(this@ForgotPasswordActivity)?.let { showSnack(it) }
        })

        //Set email error
        mModel.mEmailError.observe(this@ForgotPasswordActivity, Observer {
            forgot_password_email_et.error = it!!.getMessage(this@ForgotPasswordActivity)
        })

        //Observer the api responses.
        mModel.mUiModel.observe(this@ForgotPasswordActivity, Observer<ForgotPasswordUiModel> {

            //Log analytics
            val bundle = Bundle()
            bundle.putString(AnalyticsEvents.KEY_EMAIL, forgot_password_email_et.getTrimmedText())
            logEvent(AnalyticsEvents.EVENT_FORGOT_PASSWORD, bundle)

            it?.let {
                if (it.isSuccess) {
                    showSnack(getString(R.string.forgot_password_successful),
                            getString(R.string.btn_title_open_mail), View.OnClickListener { onOpenEmail() })

                    //Finish after snack bar complete
                    Handler().postDelayed({ finish() }, 3500)
                }
            }
        })
        setToolbar(R.id.include, getString(R.string.title_activity_forgot_password), true)
    }

    @OnClick(R.id.forgot_password_submit_btn)
    fun submit() {
        ViewUtils.hideKeyboard(forgot_password_email_et)
        mModel.forgotPasswordRequest(forgot_password_email_et.getTrimmedText())
    }

    private fun onOpenEmail() {
        val bottomSheet = BottomSheet.Builder(this@ForgotPasswordActivity).title("Open mail")

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
