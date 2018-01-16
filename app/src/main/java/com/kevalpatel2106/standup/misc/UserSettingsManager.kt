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

package com.kevalpatel2106.standup.misc

import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import java.util.*

/**
 * Created by Keval on 14/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

class UserSettingsManager(private val sharedPrefProvider: SharedPrefsProvider) {

    private val DEFAULT_LED_COLOR = "red"
    private val DEFAULT_SYNC_INTERVAL = 3600000.toString()    /* 1 hour */
    private val DEFAULT_RINGTONE_NAME = "Default"
    private val DEFAULT_ENABLE_BACKGROUND_SYNC = true
    private val DEFAULT_SHOULD_VIBRATE = true
    private val DEFAULT_SIELENT_MODE = "do_not_play"
    private val DEFAULT_SHOULD_DISPLAY_POPUP = false
    private val DEFAULT_SHOULD_DISPLAY_UPDATE_NOTIFICATION = true
    private val DEFAULT_SHOULD_DISPLAY_PROMOTIONAL_NOTIFICATION = true
    private val DEFAULT_DAILY_NOTIFICATION_ENABLE = true
    private val DEFAULT_DAILY_REVIEW_TIME = 9 * 3600000L
    private val DEFAULT_MANNUAL_DND_ENABLE = false
    private val DEFAULT_AUTO_DND_ENABLE = false
    private val DEFAULT_AUTO_DND_START_TIME = 9 * 3600000L
    private val DEFAULT_AUTO_DND_END_TIME = 10 * 3600000L
    private val DEFAULT_SLEEP_START_TIME = 22 * 3600000L
    private val DEFAULT_SLEEP_END_TIME = 7 * 3600000L

    val enableBackgroundSync: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_ALLOW_BACKGROUND_SYNC,
                DEFAULT_ENABLE_BACKGROUND_SYNC)

    val syncInterval: Long
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_SYNC_INTERVAL,
                DEFAULT_SYNC_INTERVAL)!!.toLong()

    val getReminderToneName: String?
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_RINGTONE_NAME,
                DEFAULT_RINGTONE_NAME)

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
                DEFAULT_SIELENT_MODE)!!

    val shouldPlayReminderSoundInSilent: Boolean
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_SILENT_MODE,
                DEFAULT_SIELENT_MODE)!! == "do_not_play"

    val shouldPlayReminderSoundWithHeadphones: Boolean
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_SILENT_MODE,
                DEFAULT_SIELENT_MODE)!! == "only_with_headphone"

    val shouldPlayReminderSoundAlways: Boolean
        get() = sharedPrefProvider.getStringFromPreferences(SharedPreferenceKeys.PREF_KEY_SILENT_MODE,
                DEFAULT_SIELENT_MODE)!! == "always_play"

    val shouldDisplayPopUp: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_IS_TO_DISPLAY_POPUP,
                DEFAULT_SHOULD_DISPLAY_POPUP)

    val shouldDisplayUpdateNotification: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_IS_TO_SHOW_UPDATE_NOTIFICATION,
                DEFAULT_SHOULD_DISPLAY_UPDATE_NOTIFICATION)

    val shouldDisplayPromotionalNotification: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_IS_TO_SHOW_PROMOTIONAL_NOTIFICATION,
                DEFAULT_SHOULD_DISPLAY_PROMOTIONAL_NOTIFICATION)

    val isDailyReviewEnable: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_ENABLE,
                DEFAULT_DAILY_NOTIFICATION_ENABLE)

    var dailyReviewTimeFrom12Am: Long
        get() = sharedPrefProvider.getLongFromPreference(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_TIME_12AM,
                DEFAULT_DAILY_REVIEW_TIME)
        set(value) = sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_TIME_12AM, value)

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

    val isDndEnable: Boolean
        get() {
            if (isForceDndEnable) return true

            //Check if we have auto DND enabled?
            if (isAutoDndEnable) {
                val currentMillsFrom12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis())

                //Check if we are in the range of auto dnd time?
                return if (autoDndEndTime >= autoDndStartTime) {
                    //End time is more than start time
                    //e.g. start time is 4:00 PM and end time is 5:00 PM
                    currentMillsFrom12Am in autoDndStartTime..autoDndEndTime
                } else {
                    //End time is less than start time
                    //e.g. start time is 11:00 PM and end time is 1:00 AM
                    currentMillsFrom12Am in autoDndStartTime..TimeUtils.ONE_DAY_MILLISECONDS
                            && currentMillsFrom12Am in 0..autoDndEndTime
                }
            }

            return false
        }

    private val isForceDndEnable: Boolean
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_IS_FORCE_DND_ENABLE,
                DEFAULT_MANNUAL_DND_ENABLE)

    val isAutoDndEnable
        get() = sharedPrefProvider.getBoolFromPreferences(SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE,
                DEFAULT_AUTO_DND_ENABLE)

    val autoDndStartTime: Long
        get() = sharedPrefProvider.getLongFromPreference(SharedPreferenceKeys.PREF_KEY_AUTO_DND_START_TIME_FROM_12AM,
                DEFAULT_AUTO_DND_START_TIME)

    val autoDndEndTime: Long
        get() = sharedPrefProvider.getLongFromPreference(SharedPreferenceKeys.PREF_KEY_AUTO_DND_END_TIME_FROM_12AM,
                DEFAULT_AUTO_DND_END_TIME)

    val sleepStartTime: Long
        get() = sharedPrefProvider.getLongFromPreference(SharedPreferenceKeys.PREF_KEY_SLEEP_START_TIME_FROM_12AM,
                DEFAULT_SLEEP_START_TIME)

    val sleepEndTime: Long
        get() = sharedPrefProvider.getLongFromPreference(SharedPreferenceKeys.PREF_KEY_SLEEP_END_TIME_FROM_12AM,
                DEFAULT_SLEEP_END_TIME)


    fun setReminderTone(name: String, uri: Uri) {
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_RINGTONE_NAME, name)
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_RINGTONE_URI, uri.toString())
    }

    fun setAutoDndTime(startTimeMillsFron12Am: Long, endTimeMillsFron12Am: Long) {
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_AUTO_DND_START_TIME_FROM_12AM,
                startTimeMillsFron12Am)
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_AUTO_DND_END_TIME_FROM_12AM,
                endTimeMillsFron12Am)
    }

    fun setSleepime(startTimeMillsFron12Am: Long, endTimeMillsFron12Am: Long) {
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_SLEEP_START_TIME_FROM_12AM,
                startTimeMillsFron12Am)
        sharedPrefProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_SLEEP_END_TIME_FROM_12AM,
                endTimeMillsFron12Am)
    }
}
