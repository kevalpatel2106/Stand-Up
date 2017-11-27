package com.kevalpatel2106.standup.authentication.repo

import com.google.gson.annotations.SerializedName

/**
 * Created by Kevalpatel2106 on 27-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
data class LogoutRequest(

        @SerializedName("uid")
        val uid: Long,

        @SerializedName("deviceId")
        val deviceId: String
)