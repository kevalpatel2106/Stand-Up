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

package com.standup.core

import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.rxbus.Event
import com.kevalpatel2106.utils.rxbus.RxBus
import javax.inject.Inject

/**
 * Created by Keval on 24/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class CorePrefsProvider @Inject constructor(private val sharedPrefsProvider: SharedPrefsProvider) {

    companion object {

        /**
         * This key holds the time of the last sync time.
         */
        internal const val PREF_KEY_LAST_SYNC_TIME = "last_sync_time_mills"

        /**
         * This ket holds the time of the next standing notification in milliseconds.
         */
        const val PREF_KEY_NEXT_NOTIFICATION_TIME = "next_notification_time"
    }

    val lastSyncTime: Long
        get() = sharedPrefsProvider.getLongFromPreference(PREF_KEY_LAST_SYNC_TIME, 0)

    val nextNotificationTime: Long
        get() = sharedPrefsProvider.getLongFromPreference(PREF_KEY_NEXT_NOTIFICATION_TIME, 0)

    internal fun saveLastSyncTime(timeInMills: Long) {
        sharedPrefsProvider.savePreferences(PREF_KEY_LAST_SYNC_TIME, timeInMills)
    }

    internal fun saveNextNotificationTime(timeInMills: Long) {
        sharedPrefsProvider.savePreferences(PREF_KEY_NEXT_NOTIFICATION_TIME, timeInMills)
        RxBus.post(Event(CoreConfig.TAG_RX_NOTIFICATION_TIME_UPDATED))
    }
}
