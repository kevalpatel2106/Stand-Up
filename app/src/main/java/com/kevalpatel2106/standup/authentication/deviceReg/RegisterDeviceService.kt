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
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.support.annotation.VisibleForTesting
import com.google.firebase.iid.FirebaseInstanceId
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.consumer.NWErrorConsumer
import com.kevalpatel2106.network.consumer.NWSuccessConsumer
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.standup.constants.SharedPrefranceKeys
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import com.kevalpatel2106.utils.Utils
import io.reactivex.disposables.Disposable


/**
 * Created by Keval on 08-Feb-17.
 * This service will register the current device to the server with the FCM id.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

class RegisterDeviceService : Service() {
    companion object {

        /**
         * Start th service.
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
    }

    @VisibleForTesting
    var mDisposable: Disposable? = null

    @VisibleForTesting
    var mUserAuthRepository: UserAuthRepository = UserAuthRepositoryImpl()

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        makeForeground()

        //Get the GCM Id.
        val regId = FirebaseInstanceId.getInstance().token
        if (regId == null) {
            //Error occurred. Mark device as not registered.
            SharedPrefsProvider.savePreferences(SharedPrefranceKeys.IS_DEVICE_REGISTERED, false)
            stopSelf()
        } else {
            sendDeviceDataToServer(regId, Utils.getDeviceId(this))
        }
    }

    /**
     * Make the current service as foreground
     */
    @Suppress("DEPRECATION")
    private fun makeForeground() {
        val notification = Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentText(getString(R.string.register_device_service_notificsation_message))
                .setSmallIcon(R.mipmap.ic_launcher) //TODO Add small notification icon.
                .setPriority(Notification.PRIORITY_LOW)
                .build()
        startForeground(234, notification)
    }

    /**
     * Save the device information and FCM id to the server.
     *
     * @param regId FCM registration id.
     * @param deviceId Unique id of the device.
     */
    @SuppressLint("HardwareIds")
    @VisibleForTesting
    fun sendDeviceDataToServer(regId: String, deviceId: String) {
        //Prepare the request object
        val requestData = DeviceRegisterRequest(gcmKey = regId, deviceId = deviceId)

        //Register to the server
        mDisposable = mUserAuthRepository
                .registerDevice(requestData)
                .subscribe(object : NWSuccessConsumer<DeviceRegisterData>() {
                    override fun onSuccess(data: DeviceRegisterData?) {
                        data?.let {
                            SharedPrefsProvider.savePreferences(SharedPrefranceKeys.IS_DEVICE_REGISTERED, true)
                            UserSessionManager.token = data.token
                        }

                        stopSelf()
                    }
                }, object : NWErrorConsumer() {

                    override fun onInternetUnavailable(message: String) {
                        SharedPrefsProvider.savePreferences(SharedPrefranceKeys.IS_DEVICE_REGISTERED, false)
                        stopSelf()
                    }

                    override fun onError(code: Int, message: String) {
                        SharedPrefsProvider.savePreferences(SharedPrefranceKeys.IS_DEVICE_REGISTERED, false)
                        stopSelf()
                    }
                })
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        //Error occurred. Mark device as not registered.
        SharedPrefsProvider.savePreferences(SharedPrefranceKeys.IS_DEVICE_REGISTERED, false)

        if (mDisposable != null) mDisposable!!.dispose()
        stopForeground(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDisposable != null) mDisposable!!.dispose()
        stopForeground(true)
    }
}
