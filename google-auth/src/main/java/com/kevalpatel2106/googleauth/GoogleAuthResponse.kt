package com.kevalpatel2106.googleauth

/**
 * Created by Keval on 6/21/2016.
 */

interface GoogleAuthResponse {

    fun onGoogleAuthSignIn(user: GoogleAuthUser)

    fun onGoogleAuthSignInFailed()

    fun onGoogleAuthSignOut(isSuccess: Boolean)
}
