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
import android.app.Service
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.annotation.VisibleForTesting
import com.google.firebase.iid.FirebaseInstanceId
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.utils.Utils


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
        internal fun start(context: Context) {
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
        internal fun stop(context: Context) {
            val launchIntent = Intent(context, RegisterDeviceService::class.java)
            launchIntent.putExtra(ARG_STOP_SERVICE, true)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context.startService(launchIntent)
            } else {
                context.startForegroundService(launchIntent)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    @VisibleForTesting
    internal var model = DeviceRegViewModel()

    private val errorObserver = Observer<ErrorMessage> { stopSelf() }

    private val successObserver = Observer<String> { stopSelf() }

    override fun onCreate() {
        super.onCreate()
        model.reposeToken.observeForever(successObserver)
        model.errorMessage.observeForever(errorObserver)

        //Make the service foreground by assigning notification
        startForeground(DeviceRegisterNotification.FOREGROUND_NOTIFICATION_ID,
                DeviceRegisterNotification.getNotification(this@RegisterDeviceService.applicationContext))
    }

    @SuppressLint("VisibleForTests")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.getBooleanExtra(ARG_STOP_SERVICE, false)) {   //Stop the service
            stopSelf()
        } else {
            model.register(Utils.getDeviceId(this@RegisterDeviceService),
                    FirebaseInstanceId.getInstance().token)
        }
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        //Error occurred. Mark device as not registered.
        model.markDeviceRegFailed()
        clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        clear()
    }

    private fun clear() {
        model.errorMessage.removeObserver(errorObserver)
        model.reposeToken.removeObserver(successObserver)

        stopForeground(true)
    }
}
