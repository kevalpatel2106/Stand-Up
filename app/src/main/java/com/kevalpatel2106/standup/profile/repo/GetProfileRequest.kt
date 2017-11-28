package com.kevalpatel2106.standup.profile.repo

import com.google.gson.annotations.SerializedName

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
data class GetProfileRequest(

        @SerializedName("uid")
        val userId: Long
)