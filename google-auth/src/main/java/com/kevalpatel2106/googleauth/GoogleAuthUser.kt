package com.kevalpatel2106.googleauth

import android.net.Uri

/**
 * Created by multidots on 6/21/2016.
 */
data class GoogleAuthUser(var id: String) {
    var idToken: String? = null
    lateinit var name: String
    lateinit var email: String
    var familyName: String? = null
    var photoUrl: Uri? = null

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean {
        other?.let {
            return (other is GoogleAuthUser && other.id == id)
        }
        return false
    }
}
