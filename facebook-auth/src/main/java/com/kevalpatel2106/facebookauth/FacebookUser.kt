package com.kevalpatel2106.facebookauth

import org.json.JSONObject

/**
 * Created by multidots on 6/16/2016.
 * This class represents facebook user profile.
 *
 * @author [&#39;https://github.com/kevalpatel2106&#39;]['https://github.com/kevalpatel2106']
 */
data class FacebookUser(
        var name: String?,

        var email: String?,

        var facebookID: String?,

        var gender: String?,

        var about: String?,

        var bio: String?,

        var coverPicUrl: String?,

        var profilePic: String?,

        /**
         * JSON response received. If you want to parse more fields.
         */
        val response: JSONObject) {

    constructor(response: JSONObject) : this(null, null, null, null, null, null, null, null, response)
}
