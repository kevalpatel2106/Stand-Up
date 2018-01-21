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

package com.kevalpatel2106.standup.core.dailyReview

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.utils.TimeUtils
import timber.log.Timber


/**
 * Created by Keval on 17/01/18.
 * An helper class for daily review package. This helper class manages registering and canceling the
 * daily review alarms.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object DailyReviewHelper {

    /**
     * Unique request code for pending intent.
     */
    private const val PENDING_INTENT_REQUEST_CODE = 463

    /**
     * Register the new daily review alarm based on the [UserSettingsManager.dailyReviewTimeFrom12Am]
     * time. It will register the exact alarm on the time given by [getNextAlarmTime]. This alarm
     * works event in dose mode.
     *
     * @see getNextAlarmTime
     */
    @SuppressLint("VisibleForTests")
    fun registerDailyReview(context: Context, userSettingsManager: UserSettingsManager) {
        val nextAlarmTime = getNextAlarmTime(userSettingsManager)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC, nextAlarmTime, getPendingIntent(context))

        Timber.i("Daily review notification scheduled after : ".plus(nextAlarmTime).plus(" milliseconds."))
    }

    /**
     * Cancel daily review alarm if any registered before.
     *
     * @see registerDailyReview
     */
    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getPendingIntent(context))

        Timber.i("Canceled daily review alarms.")
    }

    /**
     * Get the [PendingIntent] for the daily review alarm. This [PendingIntent] will invoke
     * [DailyReviewReceiver] when the alarm goes off. The request code of the [PendingIntent] is
     * [PENDING_INTENT_REQUEST_CODE].
     *
     * @see DailyReviewReceiver
     */
    private fun getPendingIntent(context: Context): PendingIntent {
        return PendingIntent.getBroadcast(context,
                PENDING_INTENT_REQUEST_CODE,
                Intent(context.applicationContext, DailyReviewReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Get the next daily review alarm time. If the current day time is already passed than this
     * will return the millisecond time for the alarm on the next day.
     *
     * e.g. If the user has selected review alarm time for 9am every day, ti will first check if the
     * current time is less than the today 9 am? If the current time is less than today's 9 am,
     * method will return today's 9am as the next alarm time. If the current time is 10 am than
     * method will return next day 9 am as the next alarm time.
     *
     * @see UserSettingsManager.dailyReviewTimeFrom12Am
     */
    @VisibleForTesting
    internal fun getNextAlarmTime(userSettingsManager: UserSettingsManager): Long {
        val timeFrom12Am = userSettingsManager.dailyReviewTimeFrom12Am
        val today12AmTimeMills = TimeUtils.getTodaysCalender12AM().timeInMillis
        val alarmTime = timeFrom12Am + today12AmTimeMills

        //Check if the alarm time is passed?
        //If the alarm time is passed, add one day into it and set an alarm for the next day.
        return if (alarmTime < System.currentTimeMillis()) {
            alarmTime + TimeUtils.ONE_DAY_MILLISECONDS  // Add one day
        } else {
            alarmTime
        }
    }
}
