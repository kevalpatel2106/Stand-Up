package com.kevalpatel2106.standup.authentication.login

import android.Manifest
import android.animation.Animator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.DecelerateInterpolator
import butterknife.OnClick
import butterknife.Optional
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.base.annotations.UIController
import com.kevalpatel2106.facebookauth.FacebookHelper
import com.kevalpatel2106.facebookauth.FacebookResponse
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthResponse
import com.kevalpatel2106.googleauth.GoogleAuthUser
import com.kevalpatel2106.googleauth.GoogleSignInHelper
import com.kevalpatel2106.standup.Dashboard
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.deviceReg.RegisterDeviceService
import com.kevalpatel2106.utils.Validator
import com.kevalpatel2106.utils.showSnack
import kotlinx.android.synthetic.main.activity_login.*

@UIController
class LoginActivity : BaseActivity(), GoogleAuthResponse, FacebookResponse {

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

    private val REQ_CODE_GET_ACCOUNTS_GOOGLE = 38947
    private val REQ_CODE_GET_ACCOUNTS_FACEBOOK = 3456

    private val KEY_EMAIL_TEXT = "email_text"
    private val KEY_PASSWORD_TEXT = "password_text"
    private val KEY_CONFIRM_PASSWORD_TEXT = "confirm_password_text"
    private val KEY_NAME_TEXT = "name_text"
    private val KEY_IS_SIGNUP = "is_sign_up"

    private val ANIMATION_DURATION = 500L

    private var isSignUp = false

    @VisibleForTesting
    internal lateinit var mModel: LoginViewModel
    private lateinit var mGoogleSignInHelper: GoogleSignInHelper
    private lateinit var mFacebookSignInHelper: FacebookHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        mModel.mIsAuthenticationRunning.observe(this@LoginActivity, Observer<Boolean> {
            btn_login_fb_signin.isEnabled = !it!!
            btn_login_google_signin.isEnabled = !it
            btn_login.isEnabled = !it
        })
        mModel.mLoginUiModel.observe(this@LoginActivity, Observer<LoginUiModel> {
            it?.let {
                if (it.isSuccess) {
                    //Start syncing the token
                    RegisterDeviceService.start(this)

                    Dashboard.launch(this@LoginActivity)
                } else it.errorMsg?.let {
                    showSnack(it)
                }
            }
        })

        setContentView(R.layout.activity_login)

        //Initialize helpers
        mGoogleSignInHelper = GoogleSignInHelper(this, getString(R.string.server_client_id), this)
        mFacebookSignInHelper = FacebookHelper(this, getString(R.string.fb_login_field_string), this)

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
     * Start sign in with the google.
     */
    @OnClick(R.id.btn_login_google_signin)
    fun googleSignIn() {
        //check fot the get account permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleSignInHelper.performSignIn(this)
        } else {
            //Permission is not available
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.GET_ACCOUNTS),
                    REQ_CODE_GET_ACCOUNTS_GOOGLE)
        }
    }

    /**
     * Start sign in with the facebook.
     */
    @Optional
    @OnClick(R.id.btn_login_fb_signin)
    fun facebookSignIn() {
        //check fot the get account permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
            mFacebookSignInHelper.performSignIn(this)
        } else {
            //Permission is not available
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.GET_ACCOUNTS),
                    REQ_CODE_GET_ACCOUNTS_FACEBOOK)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQ_CODE_GET_ACCOUNTS_GOOGLE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Available
                googleSignIn()
            } else {
                showSnack(getString(R.string.error_google_signin_fail),
                        getString(R.string.error_retry_try_again),
                        View.OnClickListener { googleSignIn() })
            }
            REQ_CODE_GET_ACCOUNTS_FACEBOOK -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Available
                facebookSignIn()
            } else {
                showSnack(getString(R.string.error_facebook_signin_fail),
                        getString(R.string.error_retry_try_again),
                        View.OnClickListener { facebookSignIn() })
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        mGoogleSignInHelper.onActivityResult(requestCode, resultCode, data)
        mFacebookSignInHelper.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Validate the parameters and make login/sign up api call.
     */
    @OnClick(R.id.btn_login)
    fun submitData() {
        //validate the email
        if (Validator.isValidEmail(login_email_et.getTrimmedText())) {

            //validate password
            if (Validator.isValidPassword(login_password_et.getTrimmedText())) {

                if (isSignUp) {
                    //Validate confirm password.
                    if (login_password_et.getTrimmedText() == login_confirm_password_et.getTrimmedText()) {

                        //Validate the user name
                        if (Validator.isValidName(login_name_et.getTrimmedText())) {

                            //Perform user sign up.
                            mModel.performSignUp(login_email_et.getTrimmedText(),
                                    login_password_et.getTrimmedText(),
                                    login_name_et.getTrimmedText())
                        } else {
                            login_name_et.error = getString(R.string.error_login_invalid_name)
                        }
                    } else {
                        login_confirm_password_et.error = getString(R.string.login_error_password_did_not_match)
                    }
                } else {
                    //Perform the sign in  action.
                    mModel.performSignIn(login_email_et.getTrimmedText(), login_password_et.getTrimmedText())
                }
            } else {
                login_password_et.error = getString(R.string.error_login_invalid_password)
            }

        } else {
            login_email_et.error = getString(R.string.error_login_invalid_email)
        }
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

    override fun onGoogleAuthSignIn(user: GoogleAuthUser) =
            if (user.email.isEmpty() || user.name.isEmpty()) {
                showSnack(getString(R.string.error_google_login_email_not_found))
            } else {
                //Perform the authentication
                mModel.authenticateSocialUser(user)
            }

    override fun onGoogleAuthSignInFailed() {
        showSnack(getString(R.string.error_google_signin_fail),
                getString(R.string.error_retry_try_again),
                View.OnClickListener { googleSignIn() })
    }

    override fun onGoogleAuthSignOut(isSuccess: Boolean) {
        throw IllegalStateException("This method should never call.")
    }

    /**
     * This callback will be available when facebook sign in call fails.
     */
    override fun onFbSignInFail() {
        showSnack(getString(R.string.error_facebook_signin_fail),
                getString(R.string.error_retry_try_again),
                View.OnClickListener { facebookSignIn() })
    }

    /**
     * This method will be called whenever [FacebookHelper], authenticate and get the profile
     * detail from the facebook.
     *
     * @param facebookUser [FacebookUser].
     */
    override fun onFbProfileReceived(facebookUser: FacebookUser) =
            if (facebookUser.email.isNullOrEmpty() || facebookUser.name.isNullOrEmpty()) {
                showSnack(getString(R.string.error_fb_login_email_not_found))
            } else {
                //Perform the authentication
                mModel.authenticateSocialUser(facebookUser)
            }

    /**
     * This callback will be available whenever facebook sign out call completes. No matter success of
     * failure.
     */
    override fun onFBSignOut() {
        throw IllegalStateException("This method should never call.")
    }
}
