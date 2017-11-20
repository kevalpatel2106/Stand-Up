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

package com.kevalpatel2106.standup.authentication.signUp

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kevalpatel2106.network.BaseData

/**
 * Created by Keval on 27-Dec-16.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

data class SignUpResponseData(
        @SerializedName("uid")
        @Expose
        val uid: Long,

        @SerializedName("is_new_user")
        @Expose
        val isNewUser: Boolean = false,

        @SerializedName("photo")
        @Expose
        val photoUrl: String? = null
) : BaseData()