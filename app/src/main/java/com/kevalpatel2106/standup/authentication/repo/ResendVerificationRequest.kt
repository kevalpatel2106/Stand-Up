package com.kevalpatel2106.standup.authentication.repo

import com.google.gson.annotations.SerializedName

/**
 * Created by Keval on 26/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class ResendVerificationRequest(

        @SerializedName("uid")
        val userId: Long
)