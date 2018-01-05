/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

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
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.DecelerateInterpolator
import butterknife.OnClick
import butterknife.Optional
import com.kevalpatel2106.base.annotations.UIController
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.base.uiController.showSnack
import com.kevalpatel2106.facebookauth.FacebookHelper
import com.kevalpatel2106.facebookauth.FacebookResponse
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthResponse
import com.kevalpatel2106.googleauth.GoogleAuthUser
import com.kevalpatel2106.googleauth.GoogleSignInHelper
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.deviceReg.DeviceRegisterActivity
import com.kevalpatel2106.standup.authentication.forgotPwd.ForgotPasswordActivity
import com.kevalpatel2106.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_login.*


/**
 * This activity servers as the login/sign up screen for the application. This will switch between
 * login/sign up field using some nice animations!
 */
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
        setContentView(R.layout.activity_login)

        setModel()

        //Initialize helpers
        mGoogleSignInHelper = GoogleSignInHelper(this, getString(R.string.server_client_id), this)
        mFacebookSignInHelper = FacebookHelper(this, getString(R.string.fb_login_field_string))

        //Play the loading animations
        if (savedInstanceState == null) {
            playLoadAnimations()

            //Switch between login/sign up based on the argument
            isSignUp = intent.getBooleanExtra(ARG_IS_SIGN_UP, false)
            if (isSignUp) {
                switchToSignUp()
            } else {
                tiv_name.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        tiv_name.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        switchToLogin()
                    }
                })
            }
        } else {
            login_logo_iv.alpha = 1f
        }
    }

    private fun setModel() {
        mModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        //Observer the api call changes
        mModel.blockUi.observe(this@LoginActivity, Observer<Boolean> {
            btn_login_fb_signin.isEnabled = !it!!
            btn_login_google_signin.isEnabled = !it
            btn_login.isEnabled = !it
        })

        //Observe error messages
        mModel.errorMessage.observe(this@LoginActivity, Observer {
            it!!.getMessage(this@LoginActivity)?.let { showSnack(it) }
        })
        mModel.mEmailError.observe(this@LoginActivity, Observer {
            login_email_et.error = it!!.getMessage(this@LoginActivity)
        })
        mModel.mPasswordError.observe(this@LoginActivity, Observer {
            login_password_et.error = it!!.getMessage(this@LoginActivity)
        })
        mModel.mNameError.observe(this@LoginActivity, Observer {
            login_name_et.error = it!!.getMessage(this@LoginActivity)
        })

        //Observer the api responses.
        mModel.mLoginUiModel.observe(this@LoginActivity, Observer<LoginUiModel> {
            it?.let {
                if (it.isSuccess) {
                    //Start syncing the token
                    DeviceRegisterActivity.launch(this, it.isNewUser, it.isVerify)
                    finish()
                }
            }
        })
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
            tiv_name.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    tiv_name.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    switchToLogin()
                }
            })
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
     * Open the for got password screen.
     */
    @OnClick(R.id.btn_forgot_password)
    fun openForgotPassword() {
        ViewUtils.hideKeyboard(login_email_et)
        ForgotPasswordActivity.launch(this@LoginActivity)
    }

    /**
     * Start sign in with the google.
     */
    @OnClick(R.id.btn_login_google_signin)
    fun googleSignIn() {
        ViewUtils.hideKeyboard(login_email_et)

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
        ViewUtils.hideKeyboard(login_email_et)

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
        ViewUtils.hideKeyboard(login_email_et)

        if (isSignUp) {
            //Perform user sign up.
            mModel.performSignUp(login_email_et.getTrimmedText(),
                    login_password_et.getTrimmedText(),
                    login_name_et.getTrimmedText(),
                    login_confirm_password_et.getTrimmedText())
        } else {
            //Perform the sign in  action.
            mModel.performSignIn(login_email_et.getTrimmedText(), login_password_et.getTrimmedText())
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

        //Make login button disable while animations are running
        btn_login_toggle.isEnabled = false

        //Change the texts for user to know the screen switched to sign up.
        btn_login_toggle.setText(R.string.login_already_account)
        btn_login.setText(R.string.login_create_account)

        //Start animations
        //1. Email button doesn't move
        //2. Name button moves down by it's height with fade in animation
        tiv_name.visibility = View.VISIBLE
        tiv_name.alpha = 0F
        tiv_name.animate()
                .translationYBy(tiv_name.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .alpha(1f)
                .setListener(null)
                .start()
        //3. Password field has to move down by height of name
        tiv_password.animate()
                .setDuration(ANIMATION_DURATION)
                .translationYBy(tiv_name.height.toFloat())
                .setListener(null)
                .start()
        //4. Forgot password button will move up by the name height with fade out animation
        btn_forgot_password.visibility = View.VISIBLE
        btn_forgot_password.alpha = 1F
        btn_forgot_password.animate()
                .translationYBy(tiv_name.height.toFloat() - btn_forgot_password.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .alpha(0f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                        throw IllegalStateException("Method should not call.")
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        //Do nothing
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        //Do nothing
                    }

                    override fun onAnimationStart(p0: Animator?) {
                        btn_forgot_password.isEnabled = false
                    }
                })
                .start()
        //5. Confirm password has to move down by it's height + name - forgot button height and fade in animations
        tiv_confirm_password.visibility = View.VISIBLE
        tiv_confirm_password.alpha = 0F
        tiv_confirm_password.animate()
                .translationYBy(-btn_forgot_password.height.toFloat() + tiv_name.height.toFloat() + tiv_confirm_password.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .alpha(1f)
                .setListener(null)
                .start()
        //6. Container of button has to move up/down by confirm password + name - forgot button height
        container_btn.animate()
                .setDuration(ANIMATION_DURATION)
                .translationYBy(tiv_name.height.toFloat() + tiv_confirm_password.height.toFloat() - btn_forgot_password.height.toFloat())
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                        throw IllegalStateException("Method should not call.")
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        btn_login_toggle.isEnabled = true
                        btn_login_fb_signin.isEnabled = true
                        btn_login_google_signin.isEnabled = true
                        btn_login.isEnabled = true
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        //Do nothing
                    }

                    override fun onAnimationStart(p0: Animator?) {
                        //Disable all buttons
                        btn_login_fb_signin.isEnabled = false
                        btn_login_google_signin.isEnabled = false
                        btn_login.isEnabled = false
                    }
                })
                .start()
    }

    /**
     * Change the view to login screen from sign up.
     */
    private fun switchToLogin() {
        isSignUp = false
        //Make login button disable while animations are running
        btn_login_toggle.isEnabled = false

        //Change the texts for user to know the screen switched to sign up.
        btn_login_toggle.setText(R.string.login_create_account)
        btn_login.setText(R.string.login_with_email_sign_in)

        //Clear all the fields
        login_confirm_password_et.clear()
        login_name_et.clear()

        //Start animations
        //1. Email button doesn't move
        //2. Name button moves up by it's height with fade out animation
        tiv_name.visibility = View.VISIBLE
        tiv_name.alpha = 1F
        tiv_name.animate()
                .translationYBy(-tiv_name.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .alpha(0F)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                        throw IllegalStateException("Method should not call.")
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        tiv_name.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        //Do nothing
                    }

                    override fun onAnimationStart(p0: Animator?) {
                        //Do nothing
                    }
                })
                .start()
        //3. Password field has to move up by height of name
        tiv_password.animate()
                .setDuration(ANIMATION_DURATION)
                .translationYBy(-tiv_name.height.toFloat())
                .start()
        //4. Forgot password button will move by the name height with fade in animation
        btn_forgot_password.visibility = View.VISIBLE
        btn_forgot_password.alpha = 0F
        btn_forgot_password.animate()
                .translationYBy(-tiv_name.height.toFloat() + btn_forgot_password.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .alpha(1f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                        throw IllegalStateException("Method should not call.")
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        //Do nothing
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        //Do nothing
                    }

                    override fun onAnimationStart(p0: Animator?) {
                        btn_forgot_password.isEnabled = true
                    }
                })
                .start()
        //5. Confirm password has to move down by it's height + name - forgot button height and fade out animations
        tiv_confirm_password.visibility = View.VISIBLE
        tiv_confirm_password.alpha = 1F
        tiv_confirm_password.animate()
                .translationYBy(btn_forgot_password.height.toFloat() - tiv_name.height.toFloat() - tiv_confirm_password.height.toFloat())
                .setDuration(ANIMATION_DURATION)
                .alpha(0f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                        throw IllegalStateException("Method should not call.")
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        tiv_confirm_password.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        //Do nothing
                    }

                    override fun onAnimationStart(p0: Animator?) {
                        //Do nothing
                    }
                })
                .start()
        //6. Container of button has to move up/down by confirm password + name - forgot button height
        container_btn.animate()
                .setDuration(ANIMATION_DURATION)
                .translationYBy(-tiv_name.height.toFloat() - tiv_confirm_password.height.toFloat() + btn_forgot_password.height.toFloat())
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                        throw IllegalStateException("Method should not call.")
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        btn_login_toggle.isEnabled = true
                        btn_login_fb_signin.isEnabled = true
                        btn_login_google_signin.isEnabled = true
                        btn_login.isEnabled = true
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        //Do nothing
                    }

                    override fun onAnimationStart(p0: Animator?) {
                        //Disable all buttons
                        btn_login_fb_signin.isEnabled = false
                        btn_login_google_signin.isEnabled = false
                        btn_login.isEnabled = false
                    }
                })
                .start()
    }

    override fun onGoogleAuthSignIn(user: GoogleAuthUser) = mModel.authenticateSocialUser(user)

    override fun onGoogleAuthSignInFailed() {
        showSnack(getString(R.string.error_google_signin_fail),
                getString(R.string.error_retry_try_again), View.OnClickListener { googleSignIn() })
    }

    override fun onGoogleAuthSignOut(isSuccess: Boolean) = throw IllegalStateException("This method should never call.")

    /**
     * This callback will be available when facebook sign in call fails.
     */
    override fun onFbSignInFail() {
        showSnack(getString(R.string.error_facebook_signin_fail),
                getString(R.string.error_retry_try_again), View.OnClickListener { facebookSignIn() })
    }

    /**
     * This method will be called whenever [FacebookHelper], authenticate and get the profile
     * detail from the facebook.
     *
     * @param facebookUser [FacebookUser].
     */
    override fun onFbProfileReceived(facebookUser: FacebookUser) = mModel.authenticateSocialUser(facebookUser)

    /**
     * This callback will be available whenever facebook sign out call completes. No matter success of
     * failure.
     */
    override fun onFBSignOut() = throw IllegalStateException("This method should never call.")
}
