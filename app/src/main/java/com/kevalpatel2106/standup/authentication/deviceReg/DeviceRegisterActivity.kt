package com.kevalpatel2106.standup.authentication.deviceReg

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.verifyEmail.VerifyEmailActivity
import com.kevalpatel2106.standup.dashboard.DashboardActivity
import com.kevalpatel2106.standup.profile.EditProfileActivity
import com.kevalpatel2106.utils.Utils
import com.kevalpatel2106.utils.showSnack
import kotlinx.android.synthetic.main.activity_device_register.*


class DeviceRegisterActivity : AppCompatActivity() {

    companion object {

        @VisibleForTesting
        const val ARG_IS_NEW_USER = "arg_is_new_user"

        @VisibleForTesting
        const val ARG_IS_VERIFIED = "arg_is_verified"

        /**
         * Launch the [DeviceRegisterActivity].
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context, isNewUser: Boolean, isVerified: Boolean) {
            val launchIntent = Intent(context, DeviceRegisterActivity::class.java)
            launchIntent.putExtra(ARG_IS_NEW_USER, isNewUser)
            launchIntent.putExtra(ARG_IS_VERIFIED, isVerified)
            context.startActivity(launchIntent)
        }
    }

    @VisibleForTesting
    internal lateinit var mModel: DeviceRegViewModel

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get(DeviceRegViewModel::class.java)

        setContentView(R.layout.activity_device_register)


        mModel.token.observe(this@DeviceRegisterActivity, Observer {
            it?.let { navigateToNextScreen() }
        })

        mModel.errorMessage.observe(this@DeviceRegisterActivity, Observer {
            it!!.getMessage(this@DeviceRegisterActivity)?.let { showSnack(it) }
        })

        if (device_reg_iv.drawable is Animatable) {
            (device_reg_iv.drawable as Animatable).start()
        }

        mModel.register(Utils.getDeviceId(this@DeviceRegisterActivity), FirebaseInstanceId.getInstance().token)
    }

    @VisibleForTesting
    internal fun navigateToNextScreen() {
        when {
            !intent.getBooleanExtra(ARG_IS_VERIFIED, false) -> {
                VerifyEmailActivity.launch(this@DeviceRegisterActivity)
            }
            intent.getBooleanExtra(ARG_IS_NEW_USER, false) -> {
                EditProfileActivity.launch(this@DeviceRegisterActivity)
            }
            else -> {
                DashboardActivity.launch(this@DeviceRegisterActivity)
            }
        }

        finish()
    }

    override fun onBackPressed() {
        //Do nothing
    }
}
