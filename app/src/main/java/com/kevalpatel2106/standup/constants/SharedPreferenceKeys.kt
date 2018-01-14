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

package com.kevalpatel2106.standup.constants

/**
 * Created by Keval on 17/11/17.
 * Object class to hold the keys used for shared preferences.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

internal object SharedPreferenceKeys {

    /**
     * This key holds to boolean to indicate if fcm token is synced or not?
     */
    const val IS_DEVICE_REGISTERED: String = "is_device_registered"

    /**
     * This key holds to boolean to indicate if navigation drawer has been displayed to the user?
     */
    const val IS_NAVIGATION_DRAWER_DISPLAYED: String = "is_navigation_drawer_displayed"

    /**
     * This key holds the time of the last sync time.
     */
    const val PREF_KEY_LAST_SYNC_TIME = "last_sync_time_mills"

    /**
     * This ket holds the time of the next standing notification in milliseconds.
     */
    const val PREF_KEY_NEXT_NOTIFICATION_TIME = "next_notification_time"


    // *********** User session keys. ************ //
    const val USER_ID = "user_id"                        //User unique id

    const val USER_DISPLAY_NAME = "user_display_name"    //First name of the user

    const val USER_EMAIL = "user_email"                  //Email address of the user

    const val USER_TOKEN = "user_token"                  //Authentication token

    const val USER_IS_MALE = "user_is_male"                  //Is user male

    const val USER_HEIGHT = "user_height"                  //User height in cms

    const val USER_WEIGHT = "user_weight"                  //User weight in kg

    const val USER_PHOTO = "user_photo"                  //User photo

    const val USER_IS_VERIFIED = "user_is_verified"        //Is the user verified.


    // *********** User settings keys. ************ //
    /**
     * This key holds the boolean that will be true is user enabled background syncing. The default
     * value is true.
     *
     * DON'T CHANGE THE VALUE OF THIS KEY. THIS KEY IS ALSO REPLICATED TO `@string/pref_key` FILE.
     */
    const val PREF_KEY_ALLOW_BACKGROUND_SYNC = "pref_key_background_sync"
}