package com.kevalpatel2106.standup.profile.repo

import com.google.gson.annotations.SerializedName

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
data class SaveProfileResponse(

        @SerializedName("uid")
        val userId: Long,

        @SerializedName("name")
        val name: String,

        @SerializedName("email")
        val email: String,

        @SerializedName("photo")
        val photo: String,

        @SerializedName("height")
        val height: String,

        @SerializedName("weight")
        val weight: String,

        @SerializedName("gender")
        val gender: String,

        @SerializedName("isVerified")
        val isVerified: String
)