/*
 * Copyright 2017 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.filemanager.utils


/**
 * Created by Keval on 24-Oct-16.
 * This class manages user login session.
 *
 * @author [&#39;https://github.com/kevalpatel2106&#39;]['https://github.com/kevalpatel2106']
 */

object UserSessionManager {
    //User preference keys.
    private val USER_ID = "USER_ID"                        //User unique id
    private val USER_DISPLAY_NAME = "USER_DISPLAY_NAME"    //First name of the user
    private val USER_EMAIL = "USER_EMAIL"                  //Email address of the user
    private val USER_TOKEN = "USER_TOKEN"                  //Authentication token
    private val USER_PHOTO = "USER_PHOTO"                  //User photo

    /**
     * Get the user id of current user.
     *
     * @return user id.
     */
    val userId: Long
        get() = SharedPrefsProvider.getLongFromPreference(USER_ID)

    /**
     * Photo of current user.
     */
    val photo: String?
        get() = SharedPrefsProvider.getStringFromPreferences(USER_PHOTO)

    /**
     * First name of the user.
     */
    val displayName: String?
        get() = SharedPrefsProvider.getStringFromPreferences(USER_DISPLAY_NAME)

    /**
     * Current authentication token
     */
    var token: String?
        get() = SharedPrefsProvider.getStringFromPreferences(USER_TOKEN)
        set(token) = SharedPrefsProvider.savePreferences(USER_TOKEN, token)

    /**
     * Email of the user logged in.
     */
    val email: String?
        get() = SharedPrefsProvider.getStringFromPreferences(USER_EMAIL)

    /**
     * Check if the user is currently logged in?
     *
     * @return true if the user is currently logged in.
     */
    val isUserLoggedIn: Boolean
        get() = SharedPrefsProvider.getLongFromPreference(USER_ID) > 0
                && SharedPrefsProvider.getStringFromPreferences(USER_TOKEN) != null

    /**
     * Set user session detail.
     *
     * @param userId unique id of the user.
     * @param displayName Display name of the user.
     * @param email Email id of the user.
     * @param photoUrl User profile picture url.
     * @param token  authentication token.
     */
    @JvmStatic
    @JvmOverloads
    @Synchronized
    fun setNewSession(userId: Long,
                      displayName: String,
                      email: String,
                      token: String? = null,
                      photoUrl: String?) {

        synchronized(this) {

            //Save to the share prefs.
            SharedPrefsProvider.savePreferences(USER_ID, userId)
            updateSession(displayName = displayName,
                    email = email,
                    photoUrl = photoUrl)
            this.token = token
        }
    }


    /**
     * Update the user session detail.
     *
     * @param displayName Display name of the user.
     * @param email Email id of the user.
     * @param photoUrl User profile picture url.
     */
    @JvmStatic
    @Synchronized
    fun updateSession(displayName: String,
                      email: String,
                      photoUrl: String?) {
        synchronized(this) {

            //Save to the share prefs.
            SharedPrefsProvider.savePreferences(USER_DISPLAY_NAME, displayName)
            SharedPrefsProvider.savePreferences(USER_EMAIL, email)
            SharedPrefsProvider.savePreferences(USER_PHOTO, photoUrl)
        }
    }

    /**
     * Clear token from the session.
     */
    @JvmStatic
    fun clearToken() {
        SharedPrefsProvider.removePreferences(USER_TOKEN)
    }

    /**
     * Clear user data.
     */
    @JvmStatic
    fun clearUserSession() {
        SharedPrefsProvider.removePreferences(USER_ID)
        SharedPrefsProvider.removePreferences(USER_DISPLAY_NAME)
        SharedPrefsProvider.removePreferences(USER_EMAIL)
        SharedPrefsProvider.removePreferences(USER_PHOTO)

        clearToken()
    }
}
