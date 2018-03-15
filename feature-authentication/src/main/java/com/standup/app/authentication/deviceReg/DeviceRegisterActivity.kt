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

package com.standup.app.authentication.deviceReg

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
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.common.base.uiController.showSnack
import com.kevalpatel2106.common.misc.AnalyticsEvents
import com.kevalpatel2106.common.misc.logEvent
import com.kevalpatel2106.common.misc.lottie.LottieJson
import com.kevalpatel2106.common.misc.lottie.playAnotherAnimation
import com.kevalpatel2106.common.misc.lottie.playAnotherRepeatAnimation
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.userActivity.UserActivityDao
import com.kevalpatel2106.common.userActivity.repo.UserActivityRepoImpl
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.utils.Utils
import com.standup.app.authentication.AuthenticationHook
import com.standup.app.authentication.BuildConfig
import com.standup.app.authentication.R
import com.standup.app.authentication.di.DaggerUserAuthComponent
import com.standup.app.authentication.verification.VerifyEmailActivity
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

    @Inject
    internal lateinit var authenticationHook: AuthenticationHook

    @Inject
    internal lateinit var userActivityDao: UserActivityDao

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
    }

    @Suppress("ObjectLiteralToLambda")
    @SuppressLint("VisibleForTests")
    private fun setModel() {
        model = ViewModelProviders.of(this).get(DeviceRegViewModel::class.java)
        model.reposeToken.observe(this@DeviceRegisterActivity, Observer {
            it?.let {
                //Recreate the app component to use all the new session tokens
                (application as BaseApplication).recreateAppComponent()

                device_reg_tv.text = getString(R.string.set_up_syncing)

                //Log the analytics event
                this.logEvent(AnalyticsEvents.EVENT_DEVICE_REGISTERED)

                //Start syncing old data.
                model.syncOldActivities(UserActivityRepoImpl(userActivityDao,
                        NetworkApi(
                                userId = userSessionManager.userId.toString(),
                                token = userSessionManager.token
                        ).getRetrofitClient(BuildConfig.BASE_URL)))
            }
        })
        model.syncComplete.observe(this@DeviceRegisterActivity, Observer {
            it?.let {
                if (it) {
                    device_reg_tv.text = getString(R.string.set_up_complete)

                    device_reg_iv.playAnotherAnimation(LottieJson.CORRECT_TICK_INSIDE_GREEN_CIRCLE)
                    Handler().postDelayed({ navigateToNextScreen() }, 2_000L /* 2 seconds delay */)
                }
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
        model.register(
                deviceId = Utils.getDeviceId(this@DeviceRegisterActivity),
                fcmId = FirebaseInstanceId.getInstance().token
        )
    }

    /**
     * Decide which screen should come next?
     *
     * - If the user is not verified (i.e. [ARG_IS_VERIFIED] is false) navigate to [VerifyEmailActivity].
     * - If the user is new (i.e. [ARG_IS_NEW_USER] is true), navigate to [AuthenticationHook.openEditUserProfile].
     * - If the user is old user and email is verified, navigate to [AuthenticationHook.openMainScreen].
     */
    @VisibleForTesting
    internal fun navigateToNextScreen() {
        when {
            !intent.getBooleanExtra(ARG_IS_VERIFIED, false) -> {
                VerifyEmailActivity.launch(this@DeviceRegisterActivity)
            }
            intent.getBooleanExtra(ARG_IS_NEW_USER, false) -> {
                authenticationHook.openEditUserProfile(this@DeviceRegisterActivity, userSessionManager)
            }
            else -> {
                authenticationHook.openMainScreen(this@DeviceRegisterActivity)
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
        internal fun launch(context: Context, isNewUser: Boolean, isVerified: Boolean) {
            val launchIntent = Intent(context, DeviceRegisterActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(ARG_IS_NEW_USER, isNewUser)
                putExtra(ARG_IS_VERIFIED, isVerified)
            }
            context.startActivity(launchIntent)
        }
    }
}
