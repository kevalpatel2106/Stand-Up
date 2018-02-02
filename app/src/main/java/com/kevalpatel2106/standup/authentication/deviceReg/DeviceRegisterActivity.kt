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

package com.kevalpatel2106.standup.authentication.deviceReg

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.annotation.VisibleForTesting
import android.view.View
import com.google.firebase.iid.FirebaseInstanceId
import com.kevalpatel2106.common.AnalyticsEvents
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.common.base.uiController.showSnack
import com.kevalpatel2106.common.logEvent
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.di.DaggerUserAuthComponent
import com.kevalpatel2106.standup.authentication.verification.VerifyEmailActivity
import com.kevalpatel2106.standup.main.MainActivity
import com.kevalpatel2106.standup.misc.LottieJson
import com.kevalpatel2106.standup.misc.playAnotherAnimation
import com.kevalpatel2106.standup.misc.playAnotherRepeatAnimation
import com.kevalpatel2106.standup.profile.EditProfileActivity
import com.kevalpatel2106.utils.Utils
import kotlinx.android.synthetic.main.activity_device_register.*
import javax.inject.Inject


/**
 * This [BaseActivity] is responsible for registering the user device to the server.
 */
class DeviceRegisterActivity : BaseActivity() {

    @VisibleForTesting
    internal lateinit var model: DeviceRegViewModel

    @Inject
    internal lateinit var userSessionManager: UserSessionManager

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inject
        DaggerUserAuthComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@DeviceRegisterActivity)

        setContentView(R.layout.activity_device_register)


        setModel()
        model.register(Utils.getDeviceId(this@DeviceRegisterActivity), FirebaseInstanceId.getInstance().token)
    }

    @Suppress("ObjectLiteralToLambda")
    @SuppressLint("VisibleForTests")
    private fun setModel() {
        model = ViewModelProviders.of(this).get(DeviceRegViewModel::class.java)
        model.reposeToken.observe(this@DeviceRegisterActivity, Observer {
            it?.let {
                device_reg_tv.text = getString(R.string.set_up_complete)
                device_reg_iv.playAnotherAnimation(LottieJson.CORRECT_TICK_INSIDE_GREEN_CIRCLE)
                Handler().postDelayed({ navigateToNextScreen() }, 2_000L /* 2 seconds delay */)

                //Log the analytics event
                this.logEvent(AnalyticsEvents.EVENT_DEVICE_REGISTERED)
            }
        })
        model.blockUi.observe(this@DeviceRegisterActivity, Observer {
            it?.let {
                if (it) {
                    //Registration is progress
                    device_reg_iv.playAnotherRepeatAnimation(LottieJson.ANIMATING_CLOCK)
                    device_reg_tv.text = getString(R.string.setting_up_your_device)
                }
            }
        })
        model.errorMessage.observe(this@DeviceRegisterActivity, Observer {
            it?.let {

                //Set error view
                device_reg_iv.playAnotherAnimation(LottieJson.WARNING)
                device_reg_tv.text = getString(R.string.set_up_failed)
                showSnack(
                        message = it.getMessage(this@DeviceRegisterActivity)!!,
                        actionName = it.getErrorBtnText(),
                        actionListener = object : View.OnClickListener {
                            override fun onClick(v: View?) {
                                it.getOnErrorClick()?.invoke()
                            }
                        }
                )

                this.logEvent(AnalyticsEvents.EVENT_DEVICE_REGISTER_FAIL, android.os.Bundle().apply {
                    putString(AnalyticsEvents.KEY_MESSAGE, it.getMessage(this@DeviceRegisterActivity))
                    putString(AnalyticsEvents.KEY_FCM_ID, FirebaseInstanceId.getInstance().token)
                })
            }
        })
    }

    /**
     * Decide which screen should come next?
     *
     * - If the user is not verified (i.e. [ARG_IS_VERIFIED] is false) navigate to [VerifyEmailActivity].
     * - If the user is new (i.e. [ARG_IS_NEW_USER] is true), navigate to [EditProfileActivity].
     * - If the user is old user and email is verified, navigate to [MainActivity].
     */
    @VisibleForTesting
    internal fun navigateToNextScreen() {
        when {
            !intent.getBooleanExtra(ARG_IS_VERIFIED, false) -> {
                VerifyEmailActivity.launch(this@DeviceRegisterActivity)
            }
            intent.getBooleanExtra(ARG_IS_NEW_USER, false) -> {
                EditProfileActivity.launch(this@DeviceRegisterActivity, userSessionManager)
            }
            else -> {
                MainActivity.launch(this@DeviceRegisterActivity)
            }
        }

        //Kill
        finish()
    }

    override fun onBackPressed() {
        //Do nothing
        //We are not allowing user to go back
    }

    companion object {

        /**
         * Key for the boolean argument. The value of this key wil be true if the user is new user else
         * it will be false.
         */
        @VisibleForTesting
        const val ARG_IS_NEW_USER = "arg_is_new_user"

        /**
         * Key for the boolean argument. The value of this key wil be true if the user has not
         * verified his email address else it will be false.
         */
        @VisibleForTesting
        const val ARG_IS_VERIFIED = "arg_is_verified"

        /**
         * Launch the [DeviceRegisterActivity]. If the user is newly registered, [isNewUser] will be
         * true. If the user has verified his email address [isVerified] will be true.
         *
         * @param context Instance of the caller.
         * @see ARG_IS_NEW_USER
         * @see ARG_IS_VERIFIED
         */
        @JvmStatic
        fun launch(context: Context, isNewUser: Boolean, isVerified: Boolean) {
            val launchIntent = Intent(context, DeviceRegisterActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(ARG_IS_NEW_USER, isNewUser)
                putExtra(ARG_IS_VERIFIED, isVerified)
            }
            context.startActivity(launchIntent)
        }
    }
}
