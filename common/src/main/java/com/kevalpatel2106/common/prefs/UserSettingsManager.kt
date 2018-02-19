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

package com.kevalpatel2106.common.prefs

import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import com.kevalpatel2106.utils.SharedPrefsProvider

/**
 * Created by Keval on 14/01/18.
 * Class to save the user settings parameters. This settings parameter are stored and modified
 * using the shared prefrance.
 *
 * The main categories of preferences are:
 * - Background sync settings
 * - Notification settings
 * - Daily review settings
 * - Do not disturb (DND) settings
 *
 * @constructor This class uses [sharedPrefProvider] to store the data into the Shared prefrance.
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

class UserSettingsManager(private val sharedPrefProvider: SharedPrefsProvider) {

    //************ Background sync settings *************//
    private val DEFAULT_SYNC_INTERVAL = 3600000.toString()    /* 1 hour */
    private val DEFAULT_ENABLE_BACKGROUND_SYNC = true


    /**
     * Check if the background sync is enabled or not? If the value is true,
     * [com.kevalpatel2106.standup.core.sync.SyncJob] will sync the data with the server while
     * the app is in background. If the value is false, background sync is disabled.
     */
    val enableBackgroundSync: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_ALLOW_BACKGROUND_SYNC,
                DEFAULT_ENABLE_BACKGROUND_SYNC)

    /**
     * Get the interval in milliseconds between two background sync with the server.
     */
    val syncInterval: Long
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_SYNC_INTERVAL,
                DEFAULT_SYNC_INTERVAL)!!.toLong()


    //************ notifications settings *************//
    private val DEFAULT_LED_COLOR = "red"
    private val DEFAULT_RINGTONE_NAME = "Default"
    private val DEFAULT_SHOULD_VIBRATE = true
    private val DEFAULT_SILENT_MODE = "do_not_play"
    private val DEFAULT_SHOULD_DISPLAY_POPUP = false
    private val DEFAULT_SHOULD_DISPLAY_UPDATE_NOTIFICATION = true
    private val DEFAULT_SHOULD_DISPLAY_PROMOTIONAL_NOTIFICATION = true

    val getReminderToneName: String
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_RINGTONE_NAME,
                DEFAULT_RINGTONE_NAME)!!

    val getReminderToneUri: Uri
        get() {
            val uriString = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_RINGTONE_URI,
                    null)

            return if (uriString == null) {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            } else {
                Uri.parse(uriString)
            }
        }

    val shouldVibrate: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_REMINDER_VIBRATE,
                DEFAULT_SHOULD_VIBRATE)

    val ledColorValue: String
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_REMINDER_LED_COLOR,
                DEFAULT_LED_COLOR)!!
    val silentModeRawValue: String
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_SILENT_MODE,
                DEFAULT_SILENT_MODE)!!

    val shouldPlayReminderSoundInSilent: Boolean
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_SILENT_MODE,
                DEFAULT_SILENT_MODE)!! == "do_not_play"

    val shouldPlayReminderSoundWithHeadphones: Boolean
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_SILENT_MODE,
                DEFAULT_SILENT_MODE)!! == "only_with_headphone"

    val shouldPlayReminderSoundAlways: Boolean
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_SILENT_MODE,
                DEFAULT_SILENT_MODE)!! == "always_play"

    val shouldDisplayPopUp: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_IS_TO_DISPLAY_POPUP,
                DEFAULT_SHOULD_DISPLAY_POPUP)

    val shouldDisplayUpdateNotification: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_IS_TO_SHOW_UPDATE_NOTIFICATION,
                DEFAULT_SHOULD_DISPLAY_UPDATE_NOTIFICATION)

    val shouldDisplayPromotionalNotification: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_IS_TO_SHOW_PROMOTIONAL_NOTIFICATION,
                DEFAULT_SHOULD_DISPLAY_PROMOTIONAL_NOTIFICATION)

    val ledColor: Int
        get() = when (ledColorValue) {
            "none" -> Color.TRANSPARENT
            "white" -> Color.WHITE
            "red" -> Color.RED
            "yellow" -> Color.YELLOW
            "green" -> Color.GREEN
            "cyan" -> Color.CYAN
            "blue" -> Color.BLUE
            else -> Color.TRANSPARENT
        }


    fun setReminderTone(name: String, uri: Uri) {
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_RINGTONE_NAME, name)
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_RINGTONE_URI, uri.toString())
    }


    //************ daily review settings *************//
    private val DEFAULT_DAILY_NOTIFICATION_ENABLE = true
    private val DEFAULT_DAILY_REVIEW_TIME = 9 * 3600000L

    val isDailyReviewEnable: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_ENABLE,
                DEFAULT_DAILY_NOTIFICATION_ENABLE)

    var dailyReviewTimeFrom12Am: Long
        get() = sharedPrefProvider.getLongFromPreference(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_TIME_12AM,
                DEFAULT_DAILY_REVIEW_TIME)
        set(value) = sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_TIME_12AM, value)


    //************ do not disturb settings *************//
    private val DEFAULT_MANNUAL_DND_ENABLE = false
    private val DEFAULT_AUTO_DND_ENABLE = false
    private val DEFAULT_AUTO_DND_START_TIME = 9 * 3600000L
    private val DEFAULT_AUTO_DND_END_TIME = 10 * 3600000L
    private val DEFAULT_SLEEP_START_TIME = 22 * 3600000L
    private val DEFAULT_SLEEP_END_TIME = 7 * 3600000L

    var isCurrentlyDndEnable: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_IS_FORCE_DND_ENABLE,
                DEFAULT_MANNUAL_DND_ENABLE)
        set(value) = sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_FORCE_DND_ENABLE, value)

    /**
     * Set the [startTimeMillsFrom12Am] and the [endTimeMillsFrom12Am] for the auto DND. These timings
     * are in milliseconds and they are calculated from the 12 AM. e.g. If the start time is 1 AM,
     * the [startTimeMillsFrom12Am] will be 3600000 milliseconds.
     *
     * [startTimeMillsFrom12Am] is stored in [SharedPrefsProvider] with key
     * [SharedPreferenceKeys.PREF_KEY_AUTO_DND_START_TIME_FROM_12AM] and [endTimeMillsFrom12Am] is
     * stored as [SharedPreferenceKeys.PREF_KEY_AUTO_DND_END_TIME_FROM_12AM].
     *
     * @see SharedPreferenceKeys.PREF_KEY_AUTO_DND_START_TIME_FROM_12AM
     * @see SharedPreferenceKeys.PREF_KEY_AUTO_DND_END_TIME_FROM_12AM
     */
    fun setAutoDndTime(startTimeMillsFrom12Am: Long, endTimeMillsFrom12Am: Long) {
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_AUTO_DND_START_TIME_FROM_12AM,
                startTimeMillsFrom12Am)
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_AUTO_DND_END_TIME_FROM_12AM,
                endTimeMillsFrom12Am)
    }

    /**
     * Check if the auto DND mode is enabled or not? Auto DND mode will turn on the DND mode between
     * the given time of the day automatically.
     *
     * The value of [isAutoDndEnable] is stored in [SharedPrefsProvider]with key
     * [SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE].
     *
     * The default value is [DEFAULT_AUTO_DND_ENABLE].
     *
     * @see SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE
     */
    val isAutoDndEnable
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE,
                DEFAULT_AUTO_DND_ENABLE)

    /**
     * Start time for the Auto DND mode. This time is calculated from 12AM and it is in milliseconds.
     * e.g. If the start time is 1 AM, the [autoDndStartTime] will be 3600000 milliseconds.
     *
     * The default value is [DEFAULT_AUTO_DND_START_TIME].
     *
     * @see SharedPreferenceKeys.PREF_KEY_AUTO_DND_START_TIME_FROM_12AM
     */
    val autoDndStartTime: Long
        get() = sharedPrefProvider.getLongFromPreference(SharedPreferenceKeys.PREF_KEY_AUTO_DND_START_TIME_FROM_12AM,
                DEFAULT_AUTO_DND_START_TIME)

    /**
     * End time for the Auto DND mode. This time is calculated from 12AM and it is in milliseconds.
     * e.g. If the start time is 1 AM, the [autoDndEndTime] will be 3600000 milliseconds.
     *
     * The default value is [DEFAULT_AUTO_DND_END_TIME].
     *
     * @see SharedPreferenceKeys.PREF_KEY_AUTO_DND_END_TIME_FROM_12AM
     */
    val autoDndEndTime: Long
        get() = sharedPrefProvider.getLongFromPreference(SharedPreferenceKeys.PREF_KEY_AUTO_DND_END_TIME_FROM_12AM,
                DEFAULT_AUTO_DND_END_TIME)


    //************ sleep settings *************//

    var isCurrentlyInSleepMode: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_IN_SLEEP_MODE_ON, false)
        set(value) = sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IN_SLEEP_MODE_ON, value)

    /**
     * Save the [startTimeMillsFrom12Am] and [endTimeMillsFrom12Am]. When the sleep time start,
     * application will stop tracking the user activity and firing the notifications.
     *
     * These timings are in milliseconds and they are calculated from the 12 AM. e.g. If the start
     * time is 1 AM,the [startTimeMillsFrom12Am] will be 3600000 milliseconds.
     *
     * [startTimeMillsFrom12Am] is stored in [SharedPrefsProvider] with key
     * [SharedPreferenceKeys.PREF_KEY_SLEEP_START_TIME_FROM_12AM] and [endTimeMillsFrom12Am] is
     * stored as [SharedPreferenceKeys.PREF_KEY_SLEEP_END_TIME_FROM_12AM].
     *
     * @see SharedPreferenceKeys.PREF_KEY_SLEEP_START_TIME_FROM_12AM
     * @see SharedPreferenceKeys.PREF_KEY_SLEEP_END_TIME_FROM_12AM
     */
    fun setSleepTime(startTimeMillsFrom12Am: Long, endTimeMillsFrom12Am: Long) {
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_SLEEP_START_TIME_FROM_12AM,
                startTimeMillsFrom12Am)
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_SLEEP_END_TIME_FROM_12AM,
                endTimeMillsFrom12Am)
    }

    val sleepStartTime: Long
        get() = sharedPrefProvider.getLongFromPreference(SharedPreferenceKeys.PREF_KEY_SLEEP_START_TIME_FROM_12AM,
                DEFAULT_SLEEP_START_TIME)

    val sleepEndTime: Long
        get() = sharedPrefProvider.getLongFromPreference(SharedPreferenceKeys.PREF_KEY_SLEEP_END_TIME_FROM_12AM,
                DEFAULT_SLEEP_END_TIME)
}
