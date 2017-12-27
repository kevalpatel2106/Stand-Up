/*
 *  Copyright 2017 Keval Patel.
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

@file:Suppress("PrivatePropertyName")

package com.kevalpatel2106.standup.authentication.intro

import android.Manifest
import android.annotation.SuppressLint
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
import butterknife.OnClick
import butterknife.Optional
import com.kevalpatel2106.base.annotations.UIController
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.facebookauth.FacebookHelper
import com.kevalpatel2106.facebookauth.FacebookResponse
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthResponse
import com.kevalpatel2106.googleauth.GoogleAuthUser
import com.kevalpatel2106.googleauth.GoogleSignInHelper
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.deviceReg.DeviceRegisterActivity
import com.kevalpatel2106.standup.authentication.login.LoginActivity
import com.kevalpatel2106.utils.showSnack
import kotlinx.android.synthetic.main.activity_intro.*


/**
 * This activity displays the introduction screens.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@UIController
class IntroActivity : BaseActivity(), GoogleAuthResponse, FacebookResponse {
    private val REQ_CODE_GET_ACCOUNTS_GOOGLE = 38947
    private val REQ_CODE_GET_ACCOUNTS_FACEBOOK = 3456

    companion object {

        /**
         * Launch the [IntroActivity].
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        @JvmOverloads
        fun launch(context: Context, isNewTask: Boolean = false) {
            val launchIntent = Intent(context, IntroActivity::class.java)
            if (isNewTask)
                launchIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(launchIntent)
        }
    }

    private lateinit var mGoogleSignInHelper: GoogleSignInHelper
    private lateinit var mFacebookSignInHelper: FacebookHelper

    @VisibleForTesting
    internal lateinit var mModel: IntroViewModel

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        mModel = ViewModelProviders.of(this).get(IntroViewModel::class.java)

        //Observer the api call changes
        mModel.blockUi.observe(this@IntroActivity, Observer<Boolean> {
            manageButtons(!it!!)
        })
        manageButtons(!mModel.blockUi.value!!)

        //Observe error messages
        mModel.errorMessage.observe(this@IntroActivity, Observer {
            it!!.getMessage(this@IntroActivity)?.let { showSnack(it) }
        })

        //Observer the api responses.
        mModel.mIntroUiModel.observe(this@IntroActivity, Observer<IntroUiModel> {
            it?.let {
                if (it.isSuccess) {
                    //Start syncing the token
                    DeviceRegisterActivity.launch(this, it.isNewUser, true)
                    finish()
                }
            }
        })

        intro_view_pager.adapter = IntroPagerAdapter(supportFragmentManager)

        //Initialize helpers
        mGoogleSignInHelper = GoogleSignInHelper(this,
                getString(R.string.server_client_id),
                this)
        mFacebookSignInHelper = FacebookHelper(this,
                getString(R.string.fb_login_field_string))
    }

    /**
     * Enable/Disable all the buttons.
     */
    @Suppress("PLUGIN_WARNING")
    @VisibleForTesting
    fun manageButtons(enableAllViews: Boolean) {
        btn_login_fb_signin?.let { it.isEnabled = enableAllViews }
        btn_create_account.isEnabled = enableAllViews
        btn_login_google_signin.isEnabled = enableAllViews
        btn_login_using_email.isEnabled = enableAllViews
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

    /**
     * Launch [LoginActivity] to sign in using the email.
     */
    @OnClick(R.id.btn_login_using_email)
    fun emailSignIn() = LoginActivity.launch(this@IntroActivity, false)

    @OnClick(R.id.btn_create_account)
    fun createAccount() = LoginActivity.launch(this@IntroActivity, true)

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

    override fun onGoogleAuthSignIn(user: GoogleAuthUser) = mModel.authenticateSocialUser(user)

    override fun onGoogleAuthSignInFailed() {
        showSnack(getString(R.string.error_google_signin_fail),
                getString(R.string.error_retry_try_again), View.OnClickListener { googleSignIn() })
    }

    override fun onGoogleAuthSignOut(isSuccess: Boolean) =
            throw IllegalStateException("This method should never call.")

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