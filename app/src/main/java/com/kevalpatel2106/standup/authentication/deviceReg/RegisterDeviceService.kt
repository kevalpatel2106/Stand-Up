/*
 * Copyright 2017 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel2106.standup.authentication.deviceReg

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.annotation.VisibleForTesting
import com.google.firebase.iid.FirebaseInstanceId
import com.kevalpatel2106.standup.authentication.repo.DeviceRegisterRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.notification.DeviceRegisterNotification
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import com.kevalpatel2106.utils.Utils
import hugo.weaving.DebugLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


/**
 * Created by Keval on 08-Feb-17.
 * This service will register the current device to the server with the FCM id.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

class RegisterDeviceService : Service() {

    companion object {
        private val ARG_STOP_SERVICE = "arg_stop_service"

        /**
         * Start the service.
         *
         * @param context Instance of caller.
         */
        fun start(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context.startService(Intent(context, RegisterDeviceService::class.java))
            } else {
                context.startForegroundService(Intent(context, RegisterDeviceService::class.java))
            }
        }

        /**
         * Stop the service.
         *
         * @param context Instance of caller.
         */
        fun stop(context: Context) {
            val launchIntent = Intent(context, RegisterDeviceService::class.java)
            launchIntent.putExtra(ARG_STOP_SERVICE, true)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context.startService(launchIntent)
            } else {
                context.startForegroundService(launchIntent)
            }
        }
    }

    @VisibleForTesting
    var mDisposable: Disposable? = null

    @VisibleForTesting
    var mUserAuthRepository: UserAuthRepository = UserAuthRepositoryImpl()

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        //Make the service foreground by assigning notification
        startForeground(DeviceRegisterNotification.FOREGROUND_NOTIFICATION_ID,
                DeviceRegisterNotification.buildNotification(this@RegisterDeviceService.applicationContext))
    }

    @SuppressLint("VisibleForTests")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.getBooleanExtra(ARG_STOP_SERVICE, false)) {   //Stop the service
            stopSelf()
        } else {
            //Get the GCM Id.
            val regId = FirebaseInstanceId.getInstance().token
            if (regId == null) {
                Timber.i("Firebase id is not available.")

                //Error occurred. Mark device as not registered.
                SharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, false)

                stopSelf()
            } else {
                sendDeviceDataToServer(regId, Utils.getDeviceId(this))
            }
        }
        return START_NOT_STICKY
    }

    /**
     * Save the device information and FCM id to the server.
     *
     * @param regId FCM registration id.
     * @param deviceId Unique id of the device.
     */
    @SuppressLint("HardwareIds")
    @VisibleForTesting
    @DebugLog
    fun sendDeviceDataToServer(regId: String, deviceId: String) {
        //Prepare the request object
        val requestData = DeviceRegisterRequest(gcmKey = regId, deviceId = deviceId)

        //Register to the server
        mDisposable = mUserAuthRepository
                .registerDevice(requestData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    data?.let {
                        SharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, true)
                        UserSessionManager.token = data.token
                    }

                    stopSelf()
                }, {
                    SharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, false)
                    stopSelf()
                })
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        //Error occurred. Mark device as not registered.
        SharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, false)

        if (mDisposable != null) mDisposable!!.dispose()
        stopForeground(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDisposable != null) mDisposable!!.dispose()
        stopForeground(true)
    }
}
