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

package com.kevalpatel2106.common

import com.kevalpatel2106.utils.SharedPrefsProvider


/**
 * Created by Keval on 24-Oct-16.
 * This class manages user login session.
 *
 * @author [&#39;https://github.com/kevalpatel2106&#39;]['https://github.com/kevalpatel2106']
 */

class UserSessionManager(private val sharedPrefProvider: SharedPrefsProvider) {
    /**
     * Get the user id of current user.
     *
     * @return user id.
     */
    val userId: Long
        get() = sharedPrefProvider.getLongFromPreference(SharedPreferenceKeys.USER_ID)

    /**
     * Photo of current user.
     */
    val photo: String?
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.USER_PHOTO)

    /**
     * First name of the user.
     */
    val displayName: String?
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.USER_DISPLAY_NAME)

    /**
     * Current authentication token
     */
    var token: String?
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.USER_TOKEN)
        set(token) = sharedPrefProvider.savePreferences(SharedPreferenceKeys.USER_TOKEN, token)

    /**
     * Email of the user logged in.
     */
    var email: String?
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.USER_EMAIL)
        set(email) = sharedPrefProvider.savePreferences(SharedPreferenceKeys.USER_EMAIL, email)

    /**
     * Check if the user is currently logged in?
     *
     * @return true if the user is currently logged in.
     */
    val isUserLoggedIn: Boolean
        get() = userId > 0 && token != null

    val isMale: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.USER_IS_MALE, true)

    val height: Float
        get() = if (sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.USER_HEIGHT) != null) {
            sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.USER_HEIGHT)!!.toFloat()
        } else {
            0F
        }

    val weight: Float
        get() = if (sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.USER_WEIGHT) != null) {
            sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.USER_WEIGHT)!!.toFloat()
        } else {
            0F
        }

    /**
     * Check if the user is currently logged in?
     *
     * @return true if the user is currently logged in.
     */
    var isUserVerified: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.USER_IS_VERIFIED)
        set(isVerified) = sharedPrefProvider.savePreferences(SharedPreferenceKeys.USER_IS_VERIFIED, isVerified)

    fun isItMe(id: Long) = userId == id

    /**
     * Set user session detail.
     *
     * @param userId unique id of the user.
     * @param displayName Display name of the user.
     * @param email Email id of the user.
     * @param photoUrl User profile picture url.
     * @param token  authentication token.
     */
    @JvmOverloads
    @Synchronized
    fun setNewSession(userId: Long,
                      displayName: String,
                      email: String,
                      token: String? = null,
                      photoUrl: String?,
                      isVerified: Boolean) {

        synchronized(this) {

            //Save to the share prefs.
            sharedPrefProvider.savePreferences(SharedPreferenceKeys.USER_ID, userId)
            updateSession(displayName = displayName, photoUrl = photoUrl)

            this.email = email
            this.token = token
            this.isUserVerified = isVerified
        }
    }


    /**
     * Update the user session detail.
     *
     * @param displayName Display name of the user.
     * @param photoUrl User profile picture url.
     */
    @Synchronized
    fun updateSession(displayName: String,
                      photoUrl: String? = null,
                      height: Float = 0F,
                      weight: Float = 0F,
                      isMale: Boolean = true) {
        synchronized(this) {

            //Save to the share prefs.
            sharedPrefProvider.savePreferences(SharedPreferenceKeys.USER_IS_MALE, isMale)
            sharedPrefProvider.savePreferences(SharedPreferenceKeys.USER_HEIGHT, height.toString())
            sharedPrefProvider.savePreferences(SharedPreferenceKeys.USER_WEIGHT, weight.toString())
            sharedPrefProvider.savePreferences(SharedPreferenceKeys.USER_DISPLAY_NAME, displayName)
            sharedPrefProvider.savePreferences(SharedPreferenceKeys.USER_PHOTO, photoUrl)
        }
    }

    /**
     * Clear token from the session.
     */
    fun clearToken() {
        sharedPrefProvider.removePreferences(SharedPreferenceKeys.USER_TOKEN)
    }

    /**
     * Clear user data.
     */
    fun clearUserSession() {
        sharedPrefProvider.nukePrefrance()
    }
}
