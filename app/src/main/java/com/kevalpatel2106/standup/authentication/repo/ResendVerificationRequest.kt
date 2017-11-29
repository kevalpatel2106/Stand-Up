package com.kevalpatel2106.standup.authentication.repo

import com.google.gson.annotations.SerializedName
import com.kevalpatel2106.base.annotations.Model

/**
 * Created by Keval on 26/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Model
class ResendVerificationRequest(

        @SerializedName("uid")
        val userId: Long
)