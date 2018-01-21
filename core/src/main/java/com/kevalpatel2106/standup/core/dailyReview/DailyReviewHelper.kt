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

import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.annotations.Helper


/**
 * Created by Keval on 17/01/18.
 * An helper class for daily review package.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Helper(DailyReviewJob::class)
internal object DailyReviewHelper {

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
    internal fun getNextAlarmTime(userSettingsManager: UserSettingsManager): Long {
        return with(TimeUtils.getTodaysCalender12AM().timeInMillis + userSettingsManager.dailyReviewTimeFrom12Am) {

            if (this < System.currentTimeMillis()) {    //Auto dnd start time is already passed.

                //Schedule the job tomorrow.
                return@with this + TimeUtils.ONE_DAY_MILLISECONDS
            } else {    //Auto dnd start time is yet to pass.
                //Schedule the job today itself.
                return@with this
            }
        }
    }
}
