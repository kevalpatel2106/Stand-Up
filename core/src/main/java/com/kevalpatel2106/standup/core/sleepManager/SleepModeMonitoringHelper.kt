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

package com.kevalpatel2106.standup.core.sleepManager

import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.annotations.Helper

/**
 * Created by Keval on 19/01/18.
 * Helper class for [SleepModeMonitoringJob].
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@Helper(SleepModeMonitoringJob::class)
internal object SleepModeMonitoringHelper {

    /**
     * Get the next sleep mode start time in unix milliseconds. It will convert the
     * [UserSettingsManager.sleepStartTime] which is in the milliseconds from the 12AM into the unix
     * time of the current day if the the time is not passed away or it will give start time for the
     * next day.
     *
     * @see UserSettingsManager.sleepStartTime
     * @see TimeUtils.getTodaysCalender12AM
     */
    @JvmStatic
    fun getSleepStartTiming(userSettingsManager: UserSettingsManager): Long {
        return with(TimeUtils.getTodaysCalender12AM().timeInMillis + userSettingsManager.sleepStartTime) {

            if (this < System.currentTimeMillis()) {    //Auto dnd start time is already passed.

                //Schedule the job tomorrow.
                return@with this + TimeUtils.ONE_DAY_MILLISECONDS
            } else {    //Auto dnd start time is yet to pass.
                //Schedule the job today itself.
                return@with this
            }
        }
    }

    /**
     * Get the next sleep mode end time in unix milliseconds. It will convert the
     * [UserSettingsManager.sleepEndTime] which is in the milliseconds from the 12AM into the unix
     * time of the current day if the the time is not passed away or it will give start time for the
     * next day.
     *
     * @see UserSettingsManager.sleepEndTime
     * @see TimeUtils.getTodaysCalender12AM
     */
    @JvmStatic
    fun getSleepEndTiming(userSettingsManager: UserSettingsManager): Long {
        return with(TimeUtils.getTodaysCalender12AM().timeInMillis + userSettingsManager.sleepEndTime) {

            if (this < System.currentTimeMillis()) {    //Auto dnd start time is already passed.

                //Schedule the job tomorrow.
                return@with this + TimeUtils.ONE_DAY_MILLISECONDS
            } else {    //Auto dnd start time is yet to pass.
                //Schedule the job today itself.
                return@with this
            }
        }
    }


    /**
     * Check if Sleep mode should be turned on based on the sleep timings? Based on the return value
     * application figure out [UserSettingsManager.isCurrentlyInSleepMode] to set true or false.
     *
     * The application should be in Sleep mode currently if current time (i.e. [System.currentTimeMillis] is
     * between [UserSettingsManager.sleepStartTime] and [UserSettingsManager.sleepEndTime].
     *
     * @return True if the DND should be enabled.
     */
    fun isCurrentlyInSleepMode(userSettingsManager: UserSettingsManager): Boolean {
        val currentTimeFrom12Am = System.currentTimeMillis() - TimeUtils.getTodaysCalender12AM().timeInMillis
        return (userSettingsManager.sleepStartTime <= currentTimeFrom12Am)
                && (userSettingsManager.sleepEndTime >= currentTimeFrom12Am)
    }

    /**
     * Is it okay to run/schedule the [SleepModeMonitoringJob]?
     *
     * [SleepModeMonitoringJob] should only be running if user is logged in (i.e.
     * [UserSessionManager.isUserLoggedIn] is true.).
     *
     * @return True if application is good to schedule/run the [SleepModeMonitoringJob] or else false.
     */
    fun shouldRunningJob(userSessionManager: UserSessionManager): Boolean {
        return userSessionManager.isUserLoggedIn
    }
}
