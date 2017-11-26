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

package com.kevalpatel2106.standup.authentication.repo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kevalpatel2106.network.BaseData
import com.kevalpatel2106.utils.UserSessionManager
import com.kevalpatel2106.utils.Utils

/**
 * Created by Keval on 02-Jan-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

data class DeviceRegisterRequest(
        @SerializedName("deviceId")
        @Expose
        val deviceId: String ,

        @SerializedName("gcmKey")
        @Expose
        var gcmKey: String?)
    : BaseData() {

    @SerializedName("deviceName")
    @Expose
    val deviceName: String = Utils.getDeviceName()

    @SerializedName("uid")
    @Expose
    val userId: Long = UserSessionManager.userId
}
