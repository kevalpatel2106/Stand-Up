/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.kevalpatel2106.googleauth

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

/**
 * Created by multidots on 6/21/2016.
 * This is the helper class for Google sign in using Google Auth.
 * You can find detail steps here: [&#39;https://developers.google.com/identity/sign-in/android/start-integrating#add-config&#39;]['https://developers.google.com/identity/sign-in/android/start-integrating.add-config']
 *
 * @property context        instance of caller activity
 * @property serverClientId The client ID of the server that will verify the integrity of the token. If you don't have clientId pass null.
 * For more detail visit [&#39;https://developers.google.com/identity/sign-in/android/backend-auth&#39;]['https://developers.google.com/identity/sign-in/android/backend-auth']
 */
class GoogleSignInHelper(private val context: FragmentActivity,
                         serverClientId: String?,
                         private val listener: GoogleAuthResponse)
    : GoogleApiClient.OnConnectionFailedListener {

    private val RC_SIGN_IN = 100
    private val mGoogleApiClient: GoogleApiClient

    init {
        //build api client
        mGoogleApiClient = buildGoogleApiClient(buildSignInOptions(serverClientId))
    }

    internal fun buildSignInOptions(serverClientId: String?): GoogleSignInOptions {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
        if (serverClientId != null) gso.requestIdToken(serverClientId)
        return gso.build()
    }

    internal fun buildGoogleApiClient(gso: GoogleSignInOptions): GoogleApiClient {
        return GoogleApiClient.Builder(context)
                .enableAutoManage(context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    fun performSignIn(activity: Activity) {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun performSignIn(activity: Fragment) {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun onActivityResult(requestCode: Int, @Suppress("UNUSED_PARAMETER") resultCode: Int, data: Intent?) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            data?.let {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

                if (result.isSuccess) {

                    // Signed in successfully, show authenticated UI.
                    result.signInAccount?.let {
                        listener.onGoogleAuthSignIn(parseToGoogleUser(it))
                        return
                    }
                }
            }
            listener.onGoogleAuthSignInFailed()
        }
    }

    private fun parseToGoogleUser(account: GoogleSignInAccount): GoogleAuthUser {
        val user = GoogleAuthUser(id = account.id!!)
        user.name = account.displayName!!
        user.familyName = account.familyName
        user.idToken = account.idToken
        user.email = account.email!!
        user.photoUrl = account.photoUrl
        return user
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        listener.onGoogleAuthSignInFailed()
    }

    fun performSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                .setResultCallback { status -> listener.onGoogleAuthSignOut(status.isSuccess) }
    }
}
