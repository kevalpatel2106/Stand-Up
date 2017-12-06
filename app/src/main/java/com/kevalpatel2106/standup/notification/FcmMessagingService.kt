package com.kevalpatel2106.standup.notification

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager

import timber.log.Timber


/**
 * Created by Keval on 01-Jun-17.
 * This service will handle the incoming FCM messages.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see FcmMessagingService
 */

class FcmMessagingService : FirebaseMessagingService() {

    @SuppressLint("BinaryOperationInTimber")
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage == null) {
            Timber.w("UNo message received in the FCM payload.")
            return
        }


        Timber.d("onMessageReceived: " + remoteMessage.data.toString())

        //Check for the user logged in
        SharedPrefsProvider.init(this@FcmMessagingService)
        if (!UserSessionManager.isUserLoggedIn) {
            Timber.w("User is not registered. Skipping the message.")
            return
        }

        //Handle for the type.
        if (!remoteMessage.data.containsKey("type")) {
            Timber.w("Notification doesn't contain the type.")
            return
        }

        //Handle based on type
        when (remoteMessage.data["type"]) {
            NotificationType.TYPE_EMAIL_VERIFIED -> {

                //Change the flag to true.
                UserSessionManager.isUserVerified = true

                //Fire the notification
                EmailVerifiedNotification.notify(this@FcmMessagingService, remoteMessage.data["message"])
            }
        }
    }
}
