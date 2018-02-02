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

package com.kevalpatel2106.standup.authentication.deviceReg

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.SharedPreferenceKeys
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.Validator
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.di.DaggerUserAuthComponent
import com.kevalpatel2106.standup.authentication.repo.DeviceRegisterRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.misc.LottieJson
import com.kevalpatel2106.utils.SharedPrefsProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Keval on 07/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class DeviceRegViewModel : BaseViewModel {

    @Inject
    lateinit var userAuthRepo: UserAuthRepository
    @Inject
    lateinit var sharedPrefsProvider: SharedPrefsProvider
    @Inject
    lateinit var userSessionManager: UserSessionManager

    constructor() {
        DaggerUserAuthComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@DeviceRegViewModel)
    }

    @VisibleForTesting
    constructor(userAuthRepo: UserAuthRepository,
                sharedPrefsProvider: SharedPrefsProvider,
                userSessionManager: UserSessionManager) {
        this.userAuthRepo = userAuthRepo
        this.sharedPrefsProvider = sharedPrefsProvider
        this.userSessionManager = userSessionManager
    }


    internal val reposeToken = MutableLiveData<String>()

    @SuppressLint("VisibleForTests")
    internal fun register(deviceId: String, fcmId: String?) {

        //Validate device id.
        if (!Validator.isValidDeviceId(deviceId)) {
            sharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, false)
            errorMessage.value = ErrorMessage(R.string.device_reg_error_invalid_device_id)
            return
        }

        //Validate the FCM Id.
        if (!Validator.isValidFcmId(fcmId)) {
            sharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, false)
            errorMessage.value = ErrorMessage(R.string.device_reg_error_invalid_fcm_id)
            return
        }

        //Register device on the server
        sendDeviceDataToServer(fcmId!!, deviceId)
    }

    /**
     * Save the device information and FCM id to the server.
     *
     * @param regId FCM registration id.
     * @param deviceId Unique id of the device.
     */
    @SuppressLint("HardwareIds")
    @VisibleForTesting
    internal fun sendDeviceDataToServer(regId: String, deviceId: String) {
        //Prepare the request object
        val requestData = DeviceRegisterRequest(gcmKey = regId,
                deviceId = deviceId,
                userId = userSessionManager.userId)

        //Register to the server
        addDisposable(userAuthRepo
                .registerDevice(requestData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({
                    blockUi.postValue(true)
                })
                .doAfterTerminate({
                    blockUi.value = false
                })
                .subscribe({ data ->
                    data?.let {
                        sharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, true)
                        userSessionManager.token = data.token
                        reposeToken.value = data.token
                    }

                }, {
                    sharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, false)

                    val errorMessage = ErrorMessage(it.message)
                    errorMessage.setErrorBtn(R.string.error_retry_try_again, { sendDeviceDataToServer(regId, deviceId) })
                    errorMessage.errorImage = LottieJson.WARNING
                }))
    }

    fun markDeviceRegFailed() = sharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, false)
}
