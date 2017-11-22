package com.kevalpatel2106.standup.constants

/**
 * Created by Keval on 17/11/17.
 * Object class to hold the keys used for shared preferences.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

internal object SharedPrefranceKeys {

    /**
     * This key holds to boolean to indicate if the app intro screen has been displayed or not?
     */
    const val IS_APP_INTRO_DISPLAYED = "is_app_intro_displayed"

    /**
     * This key holds to boolean to indicate if fcm token is synced or not?
     */
    const val IS_DEVICE_REGISTERED: String = "is_device_registered"
}
