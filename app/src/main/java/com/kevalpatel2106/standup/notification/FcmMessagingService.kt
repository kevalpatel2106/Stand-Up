package com.kevalpatel2106.standup.notification

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

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

        //TODO implement
        Timber.d("onMessageReceived: " + remoteMessage!!.data.toString())

    }
}
