package com.kevalpatel2106.standup.authentication.login

import android.animation.Animator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.view.View
import android.view.animation.DecelerateInterpolator
import butterknife.OnClick
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.base.annotations.UIController
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.signUp.SignUpRequest
import com.kevalpatel2106.utils.Validator
import kotlinx.android.synthetic.main.activity_login.*

@UIController
class LoginActivity : BaseActivity() {

    companion object {
        private val ARG_IS_SIGN_UP = "arg_is_sign_up"

        /**
         * Launch the [LoginActivity].
         *
         * @param context Instance of the caller.
         */
        fun launch(context: Context, openInSignUpMode: Boolean) {
            val launchIntent = Intent(context, LoginActivity::class.java)
            launchIntent.putExtra(ARG_IS_SIGN_UP, openInSignUpMode)
            context.startActivity(launchIntent)
        }
    }

    private val REQ_CODE_GET_ACCOUNTS = 37647

    private val KEY_EMAIL_TEXT = "email_text"
    private val KEY_PASSWORD_TEXT = "password_text"
    private val KEY_CONFIRM_PASSWORD_TEXT = "confirm_password_text"
    private val KEY_NAME_TEXT = "name_text"
    private val KEY_IS_SIGNUP = "is_sign_up"

    private val ANIMATION_DURATION = 500L

    private var isSignUp = false

    @VisibleForTesting
    internal lateinit var mModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        mModel.mIsAuthenticationRunning.observe(this@LoginActivity, Observer<Boolean> {
            btn_login_fb_signin.isEnabled = !it!!
            btn_login_google_signin.isEnabled = !it
            btn_login.isEnabled = !it
        })

        setContentView(R.layout.activity_login)

        //Play the loading animations
        if (savedInstanceState == null) {
            playLoadAnimations()
        } else {
            login_logo_iv.alpha = 1f
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        isSignUp = intent.getBooleanExtra(ARG_IS_SIGN_UP, false)
        if (isSignUp) {
            switchToSignUp()
        } else {
            switchToLogin()
            tiv_confirm_password.visibility = View.GONE
            tiv_name.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {
            outState.putBoolean(KEY_IS_SIGNUP, isSignUp)
            outState.putString(KEY_EMAIL_TEXT, login_email_et.text.toString())
            outState.putString(KEY_PASSWORD_TEXT, login_password_et.text.toString())
            outState.putString(KEY_NAME_TEXT, login_name_et.text.toString())
            outState.putString(KEY_CONFIRM_PASSWORD_TEXT, login_confirm_password_et.text.toString())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isSignUp = savedInstanceState.getBoolean(KEY_IS_SIGNUP, false)
        login_email_et.setText(savedInstanceState.getString(KEY_EMAIL_TEXT))
        login_password_et.setText(savedInstanceState.getString(KEY_PASSWORD_TEXT))
        login_confirm_password_et.setText(savedInstanceState.getString(KEY_CONFIRM_PASSWORD_TEXT))
        login_name_et.setText(savedInstanceState.getString(KEY_NAME_TEXT))

        //set the layout
        if (isSignUp) {
            switchToSignUp()
        } else {
            switchToLogin()
            tiv_confirm_password.visibility = View.GONE
            tiv_name.visibility = View.GONE
        }
    }

    /**
     * Perform the load animations.
     */
    private fun playLoadAnimations() {
        //Animate the main logo image
        login_logo_iv.animate()
                .setDuration(2 * ANIMATION_DURATION)
                .setStartDelay(300L)
                .alpha(1f)
                .setInterpolator(DecelerateInterpolator())
                .start()
    }

    /**
     *
     */
    @OnClick(R.id.btn_login)
    fun submitData() =
            //validate the email
            if (Validator.isValidEmail(login_email_et.getTrimmedText())) {

                //validate password
                if (Validator.isValidPassword(login_password_et.getTrimmedText())) {

                    if (!isSignUp) {
                        //Perform the sign in  action.
                        mModel.performSignIn(login_email_et.getTrimmedText(), login_password_et.getTrimmedText())
                    } else {
                        //Validate confirm password.
                        if (login_password_et.getTrimmedText() == login_confirm_password_et.getTrimmedText()) {

                            //Validate the user name
                            if (Validator.isValidName(login_name_et.getTrimmedText())) {

                                //Perform user sign up.
                                mModel.performSignUp(login_email_et.getTrimmedText(),
                                        login_password_et.getTrimmedText(),
                                        login_name_et.getTrimmedText())
                            } else {
                                login_name_et.setError(getString(R.string.error_login_invalid_name))
                            }
                        } else {
                            login_confirm_password_et.setError(getString(R.string.login_error_password_did_not_match))
                        }
                    }
                } else {
                    login_password_et.setError(getString(R.string.error_login_invalid_password))
                }

            } else {
                login_email_et.setError(getString(R.string.error_login_invalid_email))
            }

    /**
     * Switch between login and sign up view.
     */
    @OnClick(R.id.btn_login_toggle)
    fun toggleLoginSignUp() {
        if (isSignUp) switchToLogin()
        else switchToSignUp()
    }

    /**
     * Change the view to register screen from signup.
     */
    private fun switchToSignUp() {
        isSignUp = true
        btn_login_toggle.isEnabled = false
        btn_login_toggle.setText(R.string.login_already_account)

        btn_login.setText(R.string.login_create_account)

        //Allow fade animation
        container_btn.animate()
                .setDuration(ANIMATION_DURATION)
                .translationYBy(tiv_name.height.toFloat() + tiv_confirm_password.height.toFloat())
                .start()
        tiv_password.animate()
                .setDuration(ANIMATION_DURATION)
                .translationYBy(tiv_name.height.toFloat())
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                        throw IllegalStateException("Method should not call.")
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        btn_login_toggle.isEnabled = true
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        //Do nothing
                    }

                    override fun onAnimationStart(p0: Animator?) {
                        //Do nothing
                    }
                })
                .start()
        tiv_confirm_password.visibility = View.VISIBLE
        tiv_confirm_password.alpha = 0F
        tiv_confirm_password.animate()
                .translationYBy(tiv_confirm_password.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .alpha(1f)
                .start()

        tiv_name.visibility = View.VISIBLE
        tiv_name.alpha = 0F
        tiv_name.animate()
                .translationYBy(tiv_name.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .alpha(1f)
                .start()
    }

    /**
     * Change the view to login screen from sign up.
     */
    private fun switchToLogin() {
        isSignUp = false
        btn_login_toggle.isEnabled = false
        btn_login_toggle.setText(R.string.login_create_account)

        btn_login.setText(R.string.login_with_email_sign_in)

        container_btn.animate()
                .setDuration(ANIMATION_DURATION)
                .translationYBy(-tiv_name.height.toFloat() - tiv_confirm_password.height.toFloat())
                .start()
        tiv_password.animate()
                .setDuration(ANIMATION_DURATION)
                .translationYBy(-tiv_name.height.toFloat())
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                        throw IllegalStateException("Method should not call.")
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        btn_login_toggle.isEnabled = true
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        //Do nothing
                    }

                    override fun onAnimationStart(p0: Animator?) {
                        //Do nothing
                    }

                })
                .start()

        //Clear all the fields
        login_confirm_password_et.clear()
        login_name_et.clear()

        //Start fade out animation for password/name fields.
        tiv_confirm_password.animate()
                .translationYBy(-tiv_confirm_password.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .alpha(0f)
                .start()
        tiv_name.animate()
                .translationYBy(-tiv_name.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .alpha(0f)
                .start()
    }
}
