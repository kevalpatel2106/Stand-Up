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
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.repo.DeviceRegisterRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.misc.Validator
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Keval on 07/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DeviceRegViewModel : BaseViewModel {

    /**
     * Repository to provide user authentications.
     */
    internal var mUserAuthRepo: UserAuthRepository

    /**
     * Private constructor to add the custom [UserAuthRepository] for testing.
     *
     * @param userAuthRepo Add your own [UserAuthRepository].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(userAuthRepo: UserAuthRepository) : super() {
        mUserAuthRepo = userAuthRepo
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : super() {
        //This is the original user authentication repo.
        mUserAuthRepo = UserAuthRepositoryImpl()
    }

    internal val token = MutableLiveData<String>()

    @SuppressLint("VisibleForTests")
    internal fun register(deviceId: String, fcmId: String?) {

        //Validate device id.
        if (!Validator.isValidDeviceId(deviceId)) {
            SharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, false)
            errorMessage.value = ErrorMessage(R.string.device_reg_error_invalid_device_id)
            return
        }

        //Validate the FCM Id.
        if (!Validator.isValidFcmId(fcmId)) {
            SharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, false)
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
        val requestData = DeviceRegisterRequest(gcmKey = regId, deviceId = deviceId)

        //Register to the server
        addDisposable(mUserAuthRepo
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
                        SharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, true)
                        UserSessionManager.token = data.token
                        token.value = data.token
                    }

                }, {
                    SharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED, false)
                    errorMessage.value = ErrorMessage(it.message)
                }))
    }
}