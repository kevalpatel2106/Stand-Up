package com.kevalpatel2106.standup.constants

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.kevalpatel2106.utils.UserSessionManager
import com.kevalpatel2106.utils.Utils
import kotlinx.coroutines.experimental.launch

/**
 * Created by Keval on 08/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object AnalyticsEvents {

    //Analytics events
    const val EVENT_LOGOUT = "logout"
    const val EVENT_DEVICE_REGISTERED = "device_registered"
    const val EVENT_RESEND_VERIFICATION_MAIL = "resend_verification_mail"
    const val EVENT_FORGOT_PASSWORD = "forgot_password_req"
    const val EVENT_PROFILE_UPDATED = "profile_updated"
    const val EVENT_OPEN_MAIL_BUTTON_FEATURE = "open_mail_button_feature"
    const val EVENT_UNAUTHORIZED_FORCE_LOGOUT = "unauthorized_force_logout"


    //Bundle keys
    internal const val KEY_USER_ID = "user_id"
    internal const val KEY_DEVICE_ID = "device_id"
    internal const val KEY_EMAIL = "email"
}

fun Context.logEvent(event: String,
                     bundle: Bundle? = null) {

    launch {
        val param = bundle ?: Bundle()

        if (UserSessionManager.isUserLoggedIn) {
            param.putLong(AnalyticsEvents.KEY_USER_ID, UserSessionManager.userId)
            param.putString(AnalyticsEvents.KEY_DEVICE_ID, Utils.getDeviceId(applicationContext))
        }

        FirebaseAnalytics.getInstance(applicationContext).logEvent(event, param)
    }
}
