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
import com.kevalpatel2106.base.annotations.Model
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthUser

/**
 * Created by Keval on 27-Dec-16.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Model
data class SignUpRequest(

        @SerializedName("email")
        @Expose
        var email: String,

        @SerializedName("name")
        @Expose
        var displayName: String,

        @SerializedName("password")
        @Expose
        var password: String? = null,

        @SerializedName("photo")
        @Expose
        var photo: String? = null
) {

    constructor(fbUser: FacebookUser) : this(email = fbUser.email!!,
            displayName = fbUser.name!!,
            password = null,
            photo = fbUser.profilePic)


    constructor(googleUser: GoogleAuthUser) : this(email = googleUser.email,
            displayName = googleUser.name,
            password = null,
            photo = googleUser.photoUrl.toString())
}
