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

package com.kevalpatel2106.common

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.Utils

/**
 * Created by Keval on 08/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object AnalyticsEvents {

    //------------------Authentication flow events------------------//
    const val EVENT_LOGIN = FirebaseAnalytics.Event.LOGIN

    const val EVENT_EMAIL_SIGN_UP = "email_sign_up"

    const val EVENT_GOOGLE_SIGN_UP = "google_sign_up"
    const val EVENT_GOOGLE_SIGN_UP_FAILED = "google_sign_up_failed"
    const val EVENT_FACEBOOK_SIGN_UP = "facebook_sign_up"
    const val EVENT_FACEBOOK_SIGN_UP_FAILED = "facebook_sign_up_failed"

    //Device registration process
    const val EVENT_DEVICE_REGISTERED = "device_registered"
    const val EVENT_DEVICE_REGISTER_FAIL = "device_register_fail"

    //Verify your email screen
    const val EVENT_VERIFICATION_MAIL_SKIPPED = "verification_mail_skipped"
    const val EVENT_RESEND_VERIFICATION_MAIL = "resend_verification_mail"
    const val EVENT_OPEN_MAIL_BUTTON_FEATURE_USED = "open_mail_button_feature_used"

    //Forgot password screen
    const val EVENT_FORGOT_PASSWORD = "forgot_password_req"

    //Token failed.
    const val EVENT_UNAUTHORIZED_FORCE_LOGOUT = "unauthorized_force_logout"

    //Log out success.
    const val EVENT_LOGOUT_SUCCESS = "logout_success"


    //------------------Profile events------------------//
    const val EVENT_PROFILE_UPDATED = "profile_updated"
    const val EVENT_OPENING_PROFILE_WITHOUT_VERIFING_EMAIL = "event_opening_profile_without_verifing_email"
    const val EVENT_PROFILE_UPDATE_ERROR = "profile_updated_error"


    //------------------About page actions------------------//
    const val EVENT_APP_FORK_ON_GITHUB = "app_fork_on_github"
    const val EVENT_JOIN_SLACK_CHANNEL = "join_slack_channel"
    const val EVENT_CHECK_UPDATE_MANUALLY = "check_update_manually"
    const val EVENT_RATE_APP_ON_PLAY_STORE = "rate_app_on_play_store"
    const val EVENT_APP_INVITE_SUCCESS = "app_invite_success"
    const val EVENT_APP_INVITE_CANCEL = "app_invite_cancel"


    //------------------Core module events------------------//
    const val EVENT_ACTIVITY_RECOGNITION_ERROR = "activity_recognition_error"


    //------------------Bundle keys------------------//
    internal const val KEY_USER_ID = "user_id"
    internal const val KEY_DEVICE_ID = "device_id"
    const val KEY_FCM_ID = "fcm_id"
    const val KEY_EMAIL = "email"
    const val KEY_MESSAGE = "message"
}

@SuppressLint("MissingPermission")
fun Context.logEvent(event: String,
                     bundle: Bundle? = null) {

    val param = bundle ?: Bundle()

    val userSessionManager = UserSessionManager(SharedPrefsProvider(this))
    if (userSessionManager.isUserLoggedIn) {
        param.putLong(AnalyticsEvents.KEY_USER_ID, userSessionManager.userId)
        param.putString(AnalyticsEvents.KEY_DEVICE_ID, Utils.getDeviceId(applicationContext))
    }

    FirebaseAnalytics.getInstance(applicationContext).logEvent(event, param)
}
