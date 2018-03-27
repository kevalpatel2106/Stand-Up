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

@file:Suppress("PrivatePropertyName")

package com.standup.app.authentication.intro

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.view.View
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.common.base.uiController.showSnack
import com.kevalpatel2106.common.misc.AnalyticsEvents
import com.kevalpatel2106.common.misc.logEvent
import com.kevalpatel2106.facebookauth.FacebookHelper
import com.kevalpatel2106.facebookauth.FacebookResponse
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthResponse
import com.kevalpatel2106.googleauth.GoogleAuthUser
import com.kevalpatel2106.googleauth.GoogleSignInHelper
import com.kevalpatel2106.utils.annotations.UIController
import com.standup.app.authentication.BuildConfig
import com.standup.app.authentication.R
import com.standup.app.authentication.deviceReg.DeviceRegisterActivity
import com.standup.app.authentication.login.LoginActivity
import kotlinx.android.synthetic.main.activity_intro.*


/**
 * This activity displays the introduction screens.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@UIController
class IntroActivity : BaseActivity(), GoogleAuthResponse, FacebookResponse {
    companion object {

        /**
         * Launch the [IntroActivity].
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        @JvmOverloads
        internal fun launch(context: Context, isNewTask: Boolean = false) {
            val launchIntent = Intent(context, IntroActivity::class.java)
            if (isNewTask)
                launchIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(launchIntent)
        }
    }

    private lateinit var mGoogleSignInHelper: GoogleSignInHelper
    private lateinit var mFacebookSignInHelper: FacebookHelper

    @VisibleForTesting
    internal lateinit var model: IntroViewModel

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        //Set view model
        setViewModel()

        intro_view_pager.adapter = IntroPagerAdapter(supportFragmentManager)

        //Initialize helpers
        mGoogleSignInHelper = GoogleSignInHelper(this,
                getString(R.string.server_client_id),
                this)
        mFacebookSignInHelper = FacebookHelper(this,
                getString(R.string.fb_login_field_string))

        btn_login_google_signin.setOnClickListener {
            mGoogleSignInHelper.performSignIn(this)
            logEvent(AnalyticsEvents.EVENT_GOOGLE_SIGN_UP)
        }

        btn_login_fb_signin?.setOnClickListener {
            mFacebookSignInHelper.performSignIn(this)
            logEvent(AnalyticsEvents.EVENT_FACEBOOK_SIGN_UP)
        }

        @Suppress("ConstantConditionIf")
        btn_login_using_email.visibility = if (BuildConfig.FEATURE_EMAIL_LOGIN) View.VISIBLE else View.GONE
        btn_login_using_email.setOnClickListener {
            LoginActivity.launch(this@IntroActivity, false)
        }

        @Suppress("ConstantConditionIf")
        btn_create_account.visibility = if (BuildConfig.FEATURE_EMAIL_LOGIN) View.VISIBLE else View.GONE
        btn_create_account.setOnClickListener {
            LoginActivity.launch(this@IntroActivity, true)
        }
    }

    @SuppressLint("VisibleForTests")
    private fun setViewModel() {
        model = ViewModelProviders.of(this).get(IntroViewModel::class.java)

        //Observer the api call changes
        model.blockUi.observe(this@IntroActivity, Observer<Boolean> {
            it?.let { manageButtons(!it) }
        })

        model.isGoogleLoginProgress.observe(this@IntroActivity, Observer<Boolean> {
            it?.let { btn_login_google_signin.displayLoader(it) }
        })

        model.isFacebookLoginProgress.observe(this@IntroActivity, Observer<Boolean> {
            it?.let { btn_login_fb_signin?.displayLoader(it) }
        })

        //Observe error messages
        model.errorMessage.observe(this@IntroActivity, Observer {
            it?.let { it.getMessage(this@IntroActivity)?.let { showSnack(it) } }
        })

        //Observer the api responses.
        model.introUiModel.observe(this@IntroActivity, Observer<IntroUiModel> {
            it?.let {
                if (it.isSuccess) {
                    //Start syncing the token
                    DeviceRegisterActivity.launch(this, it.isNewUser, true)
                    finish()
                }
            }
        })
    }

    /**
     * Enable/Disable all the buttons.
     */
    @Suppress("PLUGIN_WARNING")
    private fun manageButtons(enableAllViews: Boolean) {
        btn_login_fb_signin?.let { it.isEnabled = enableAllViews }
        btn_create_account.isEnabled = enableAllViews
        btn_login_google_signin.isEnabled = enableAllViews
        btn_login_using_email.isEnabled = enableAllViews
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mGoogleSignInHelper.onActivityResult(requestCode, resultCode, data)
        mFacebookSignInHelper.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onGoogleAuthSignIn(user: GoogleAuthUser) {
        model.authenticateSocialUser(user)
        mGoogleSignInHelper.performSignOut()
    }

    override fun onGoogleAuthSignInFailed() {
        logEvent(AnalyticsEvents.EVENT_GOOGLE_SIGN_UP_FAILED)
        showSnack(getString(R.string.error_google_signin_fail),
                getString(R.string.error_retry_try_again), View.OnClickListener { btn_login_google_signin.performClick() })
    }

    override fun onGoogleAuthSignOut(isSuccess: Boolean) {
        //Do nothing
    }

    /**
     * This callback will be available when facebook sign in call fails.
     */
    override fun onFbSignInFail() {
        logEvent(AnalyticsEvents.EVENT_FACEBOOK_SIGN_UP_FAILED)
        showSnack(getString(R.string.error_facebook_signin_fail),
                getString(R.string.error_retry_try_again), View.OnClickListener { btn_login_fb_signin?.performClick() })
    }

    /**
     * This method will be called whenever [FacebookHelper], authenticate and get the profile
     * detail from the facebook.
     *
     * @param facebookUser [FacebookUser].
     */
    override fun onFbProfileReceived(facebookUser: FacebookUser) {
        model.authenticateSocialUser(facebookUser)
        mFacebookSignInHelper.performSignOut()
    }

    /**
     * This callback will be available whenever facebook sign out call completes. No matter success of
     * failure.
     */
    override fun onFBSignOut() {
        //Do nothing
    }
}
