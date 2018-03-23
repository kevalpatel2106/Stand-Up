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

package com.standup.app.diary.list

import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityDao
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.standup.app.diary.repo.DiaryRepo
import java.util.*

/**
 * Created by Keval on 23/03/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
internal object DiaryViewModelTestUtils {
    private val DIFF_BETWEEN_END_AND_START = 60000L   //60 Sec

    internal fun insert2EventsInEachDayFor7DaysForSameMonth(userActivityDao: UserActivityDao) {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = Calendar.getInstance()
        //Convert date to 30 dec 2017
        //We pick this date because as we go past dates in the loop we won't change the month.
        cal.set(Calendar.DAY_OF_MONTH, 30)
        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        for (i in DiaryRepo.PAGE_SIZE + 4 downTo 1) {

            if (i.rem(2) == 0) cal.add(Calendar.DAY_OF_MONTH, -1)

            val startTime = cal.timeInMillis + if (i.rem(2) == 0) DIFF_BETWEEN_END_AND_START else 0
            val endTime = startTime + DIFF_BETWEEN_END_AND_START

            userActivityDao.insert(UserActivity(eventStartTimeMills = startTime,
                    eventEndTimeMills = endTime,
                    type = when {
                        i.rem(2) == 0 -> UserActivityType.SITTING.name.toLowerCase()
                        i.rem(3) == 0 -> UserActivityType.MOVING.name.toLowerCase()
                        else -> UserActivityType.NOT_TRACKED.name.toLowerCase()
                    },
                    isSynced = false))
        }
    }

    internal fun insert2EventsInEachDayFor7DaysBetweenTwoMonth(userActivityDao: UserActivityDao) {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = Calendar.getInstance()
        //Convert date to 4 dec 2017
        //We pick this date because as we go past 7 dates the last 3 events will be in previous month.
        cal.set(Calendar.DAY_OF_MONTH, 4)
        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        for (i in DiaryRepo.PAGE_SIZE + 4 downTo 1) {

            if (i.rem(2) == 0) cal.add(Calendar.DAY_OF_MONTH, -1)

            val startTime = cal.timeInMillis + if (i.rem(2) == 0) DIFF_BETWEEN_END_AND_START else 0
            val endTime = startTime + DIFF_BETWEEN_END_AND_START

            userActivityDao.insert(UserActivity(eventStartTimeMills = startTime,
                    eventEndTimeMills = endTime,
                    type = when {
                        i.rem(2) == 0 -> UserActivityType.SITTING.name.toLowerCase()
                        i.rem(3) == 0 -> UserActivityType.MOVING.name.toLowerCase()
                        else -> UserActivityType.NOT_TRACKED.name.toLowerCase()
                    },
                    isSynced = false))
        }
    }

    internal fun insert2EventsInDifferentDaysOfDifferentMonths(
            totalDays: Int = 7,
            userActivityDao: UserActivityDao
    ) {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = Calendar.getInstance()
        //Convert date to 30 dec 2017. No specific reason
        cal.set(Calendar.DAY_OF_MONTH, 30)
        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        for (i in totalDays * 2 downTo 1) {

            if (i.rem(2) == 0) {
                cal.add(Calendar.MONTH, -1)
                cal.add(Calendar.DAY_OF_MONTH, -1)
            }

            val startTime = cal.timeInMillis + if (i.rem(2) == 0) DIFF_BETWEEN_END_AND_START else 0
            val endTime = startTime + DIFF_BETWEEN_END_AND_START

            userActivityDao.insert(UserActivity(eventStartTimeMills = startTime,
                    eventEndTimeMills = endTime,
                    type = when {
                        i.rem(2) == 0 -> UserActivityType.SITTING.name.toLowerCase()
                        i.rem(3) == 0 -> UserActivityType.MOVING.name.toLowerCase()
                        else -> UserActivityType.NOT_TRACKED.name.toLowerCase()
                    },
                    isSynced = false))
        }
    }
}