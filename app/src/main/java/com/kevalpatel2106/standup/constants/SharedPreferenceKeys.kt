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
     * @see [com.kevalpatel2106.standup.misc.UserSettingsManager.enableBackgroundSync]
     */
    const val PREF_KEY_ALLOW_BACKGROUND_SYNC = "pref_key_background_sync"

    /**
     * This preference holds the [android.net.Uri] of the selected ring tone in the string format.
     * The default value of this preference will be null.
     *
     * @see [com.kevalpatel2106.standup.misc.UserSettingsManager.getReminderToneUri]
     */
    const val PREF_KEY_RINGTONE_URI = "pref_key_ringtone_uri"

    /**
     * Name of the selected reminder tone. The default value of this preference should be 'Default'.
     *
     * @see [com.kevalpatel2106.standup.misc.UserSettingsManager.getReminderToneName]
     */
    const val PREF_KEY_RINGTONE_NAME = "pref_key_ringtone_name"

    /**
     * Boolean to indicate weather to vibrate when the notification arrives or not? Default value is
     * true.
     *
     * DON'T CHANGE THE VALUE OF THIS KEY. THIS KEY IS ALSO REPLICATED TO `@string/pref_key` FILE.
     * @see [com.kevalpatel2106.standup.misc.UserSettingsManager.shouldVibrate]
     */
    const val PREF_KEY_REMINDER_VIBRATE = "pref_key_reminder_notifications_vibrate"

    /**
     * String value of the led color to display when reminder notification arrives. Default value is
     * red. Values should be from [com.kevalpatel2106.standup.R.array.light_color_value_list].
     *
     * DON'T CHANGE THE VALUE OF THIS KEY. THIS KEY IS ALSO REPLICATED TO `@string/pref_key` FILE.
     * @see [com.kevalpatel2106.standup.misc.UserSettingsManager.ledColorValue]
     */
    const val PREF_KEY_REMINDER_LED_COLOR = "pref_key_reminder_notifications_led"

    /**
     * String value of the silent mode operation. Values should be from
     * [com.kevalpatel2106.standup.R.array.play_in_silent_mode_value_list].
     *
     * DON'T CHANGE THE VALUE OF THIS KEY. THIS KEY IS ALSO REPLICATED TO `@string/pref_key` FILE.
     * @see [com.kevalpatel2106.standup.misc.UserSettingsManager.silentModeRawValue]
     */
    const val PREF_KEY_SILENT_MODE = "pref_key_reminder_notifications_play_in_silent"

    /**
     * Boolean to indicate weather to display reminder pop up when the screen is on or not?
     *
     * DON'T CHANGE THE VALUE OF THIS KEY. THIS KEY IS ALSO REPLICATED TO `@string/pref_key` FILE.
     * @see [com.kevalpatel2106.standup.misc.UserSettingsManager.shouldDisplayPopUp]
     */
    const val PREF_KEY_IS_TO_DISPLAY_POPUP = "pref_key_reminder_pop_up_enable"

    /**
     * Boolean to indicate weather to display application update notification or not?
     *
     * DON'T CHANGE THE VALUE OF THIS KEY. THIS KEY IS ALSO REPLICATED TO `@string/pref_key` FILE.
     * @see [com.kevalpatel2106.standup.misc.UserSettingsManager.shouldDisplayUpdateNotification]
     */
    const val PREF_KEY_IS_TO_SHOW_UPDATE_NOTIFICATION = "pref_key_update_notifications_enable"

    /**
     * Boolean to indicate weather to display promotional/announcement notifications or not?
     *
     * DON'T CHANGE THE VALUE OF THIS KEY. THIS KEY IS ALSO REPLICATED TO `@string/pref_key` FILE.
     * @see [com.kevalpatel2106.standup.misc.UserSettingsManager.shouldDisplayPromotionalNotification]
     */
    const val PREF_KEY_IS_TO_SHOW_PROMOTIONAL_NOTIFICATION = "pref_key_promotional_notifications_enable"
}