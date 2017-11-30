package com.kevalpatel2106.facebookauth

/**
 * Created by multidots on 6/16/2016.
 * This class represents facebook user profile.
 *
 * @author [&#39;https://github.com/kevalpatel2106&#39;]['https://github.com/kevalpatel2106']
 */
data class FacebookUser(val facebookID: String) {

    var name: String? = null
    var email: String? = null
    var gender: String? = null
    var about: String? = null
    var bio: String? = null
    var coverPicUrl: String? = null
    var profilePic: String? = null
    override fun hashCode(): Int = facebookID.hashCode()

    override fun equals(other: Any?): Boolean {
        other?.let {
            return (other is FacebookUser && other.facebookID == facebookID)
        }
        return false
    }
}
