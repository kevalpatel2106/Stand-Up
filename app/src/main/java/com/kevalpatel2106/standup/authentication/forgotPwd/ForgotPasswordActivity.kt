package com.kevalpatel2106.standup.authentication.forgotPwd

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import butterknife.OnClick
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.utils.Validator
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
        mModel.mIsAuthenticationRunning.observe(this@ForgotPasswordActivity, Observer<Boolean> {
            forgot_password_submit_btn.isEnabled = !it!!
        })
        forgot_password_submit_btn.isEnabled = !mModel.mIsAuthenticationRunning.value!!

        //Observer the api responses.
        mModel.mUiModel.observe(this@ForgotPasswordActivity, Observer<ForgotPasswordUiModel> {
            if (it!!.isSuccess) {
                finish()
            } else {
                showSnack(it.errorMsg!!)
            }
        })
        setToolbar(R.id.include, getString(R.string.title_activity_forgot_password), true)
    }

    @OnClick(R.id.forgot_password_submit_btn)
    fun submit() {
        ViewUtils.hideKeyboard(forgot_password_email_et)

        val email = forgot_password_email_et.getTrimmedText()
        if (Validator.isValidEmail(email)) {
            mModel.forgotPasswordRequest(email)
        } else {
            forgot_password_email_et.error = getString(R.string.error_login_invalid_email)
        }
    }
}
