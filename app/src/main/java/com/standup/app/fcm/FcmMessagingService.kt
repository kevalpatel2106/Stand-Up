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

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kevalpatel2106.common.application.BaseApplication
import com.standup.app.authentication.AuthenticationModule
import dagger.Lazy
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Keval on 01-Jun-17.
 * This service will handle the incoming FCM messages.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see FcmMessagingService
 */

class FcmMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseServiceHelper: FcmMessagingServiceHelper

    @Inject
    lateinit var authenticationModule: Lazy<AuthenticationModule>

    private val updateNotificationListener = Observer<String> {
        it?.let { UpdateNotification.notify(this.applicationContext, it) }
    }

    private val promotionalNotificationListener = Observer<Array<String>> {
        it?.let { PromotionalNotification.notify(this.applicationContext, it[0], it[1]) }
    }

    private val authenticationNotificationListener = Observer<String> {
        it?.let { authenticationModule.get().fireEmailVerifiedNotification(this.applicationContext, it) }
    }

    override fun onCreate() {
        super.onCreate()

        DaggerFcmComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@FcmMessagingService)

        firebaseServiceHelper.fireUpdateNotification.observeForever(updateNotificationListener)
        firebaseServiceHelper.firePromotionalNotification.observeForever(promotionalNotificationListener)
        firebaseServiceHelper.fireAuthenticationNotification.observeForever(authenticationNotificationListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseServiceHelper.fireUpdateNotification.removeObserver(updateNotificationListener)
        firebaseServiceHelper.firePromotionalNotification.removeObserver(promotionalNotificationListener)
        firebaseServiceHelper.fireAuthenticationNotification.removeObserver(authenticationNotificationListener)
    }

    @SuppressLint("BinaryOperationInTimber", "VisibleForTests")
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        if (!firebaseServiceHelper.shouldProcessNotification(remoteMessage?.data)) return
        Timber.d("onMessageReceived: " + remoteMessage!!.data.toString())

        //Handle based on type
        firebaseServiceHelper.handleMessage(remoteMessage.data)
    }
}
