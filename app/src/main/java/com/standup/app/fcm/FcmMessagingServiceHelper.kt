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

package com.standup.app.fcm

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Keval on 01-Jun-17.
 * This service will handle the incoming FCM messages.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see FcmMessagingServiceHelper
 */

class FcmMessagingServiceHelper @Inject constructor(private val userSessionManager: UserSessionManager,
                                                    private var userSettingsManager: UserSettingsManager) {


    internal val fireAuthenticationNotification = MutableLiveData<String>()

    internal val firePromotionalNotification = MutableLiveData<Array<String>>()

    internal val fireUpdateNotification = MutableLiveData<String>()

    @VisibleForTesting
    internal fun handleMessage(data: Map<String, String>) {
        when (data["type"]) {
            NotificationType.TYPE_EMAIL_VERIFIED -> {

                //Change the flag to true.
                userSessionManager.isUserVerified = true

                //Fire the notification
                fireAuthenticationNotification.value = data["message"]

            }
            NotificationType.TYPE_PROMOTIONAL -> {
                if (userSettingsManager.shouldDisplayPromotionalNotification
                        && data["title"] != null
                        && data["message"] != null) {

                    //Fire the notification
                    firePromotionalNotification.value = arrayOf(data["title"]!!, data["message"]!!)
                } else {
                    Timber.i("Promotional notifications are turned off.")
                }
            }
            NotificationType.TYPE_APP_UPDATE -> {
                if (userSettingsManager.shouldDisplayUpdateNotification) {
                    //Fire the notification
                    fireUpdateNotification.value = data["message"]
                } else {
                    Timber.i("Update notifications are turned off.")
                }
            }
        }
    }

    @VisibleForTesting
    internal fun shouldProcessNotification(data: Map<String, String>?): Boolean {
        if (data == null || data.isEmpty()) {
            Timber.w("No message received in the FCM payload.")
            return false
        }

        //Check for the user logged in
        if (!userSessionManager.isUserLoggedIn) {
            Timber.w("User is not registered. Skipping the message.")
            return false
        }

        //Handle for the type.
        if (!data.containsKey("type")) {
            Timber.w("Notification doesn't contain the type.")
            return false
        }

        return true
    }
}
