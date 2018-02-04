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
 */package com.standup.suUtils

import android.support.annotation.ColorInt
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.utils.TimeUtils
import com.standup.app.constants.AppConfig
import com.standup.app.misc.SUUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.IOException
import java.util.*

/**
 * Created by Kevalpatel2106 on 27-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(Parameterized::class)
class CreateTimeLineItemFromUserActivityTest(private val userActivityType: UserActivityType,
                                             @ColorInt private val color: Int) {


    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            return arrayListOf(
                    arrayOf(UserActivityType.SITTING, AppConfig.COLOR_SITTING),
                    arrayOf(UserActivityType.MOVING, AppConfig.COLOR_STANDING),
                    arrayOf(UserActivityType.NOT_TRACKED, AppConfig.COLOR_NOT_TRACKED)
            )
        }
    }


    @Test
    @Throws(IOException::class)
    fun checkCreateTimeLineItemFromUserActivityForToday() {
        val today12AmCal = TimeUtils.getTodaysCalender12AM()

        //3 o'clock
        today12AmCal.add(Calendar.HOUR, 2)
        val startTime = today12AmCal.timeInMillis

        //3 o'clock
        today12AmCal.add(Calendar.HOUR, 1)
        val endTime = today12AmCal.timeInMillis

        val userActivity = UserActivity(eventStartTimeMills = startTime,
                eventEndTimeMills = endTime,
                type = userActivityType.name.toLowerCase(),
                isSynced = true)

        val timelineItem = SUUtils.createTimeLineItemFromUserActivity(userActivity)
        assertEquals(timelineItem.startTimeMills, 2.times(3600).times(1000))
        assertEquals(timelineItem.endTimeMills, 3.times(3600).times(1000))
        assertEquals(timelineItem.color, color)
    }


    @Test
    @Throws(IOException::class)
    fun checkCreateTimeLineItemFromUserActivityForPreviousDay() {
        val today12AmCal = TimeUtils.getCalender12AM(11, 12, 2016)

        //11 o'clock
        today12AmCal.add(Calendar.HOUR, 11)
        val startTime = today12AmCal.timeInMillis

        //10 PM
        today12AmCal.add(Calendar.HOUR, 11)
        val endTime = today12AmCal.timeInMillis

        val userActivity = UserActivity(eventStartTimeMills = startTime,
                eventEndTimeMills = endTime,
                type = userActivityType.name.toLowerCase(),
                isSynced = true)

        val timelineItem = SUUtils.createTimeLineItemFromUserActivity(userActivity)
        assertEquals(timelineItem.startTimeMills, 11.times(3600).times(1000))
        assertEquals(timelineItem.endTimeMills, 22.times(3600).times(1000))
        assertEquals(timelineItem.color, color)
    }

    @Test
    @Throws(IOException::class)
    fun checkCreateTimeLineItemFromUserActivityLongerThanOneDay() {
        val today12AmCal = TimeUtils.getCalender12AM(4, 6, 2017)

        //11 o'clock
        today12AmCal.add(Calendar.HOUR, 11)
        val startTime = today12AmCal.timeInMillis

        //2 o'clock in the morning of 5-Jun
        today12AmCal.add(Calendar.HOUR, 15) //24 + 2.
        val endTime = today12AmCal.timeInMillis

        val userActivity = UserActivity(eventStartTimeMills = startTime,
                eventEndTimeMills = endTime,
                type = userActivityType.name.toLowerCase(),
                isSynced = true)

        val timelineItem = SUUtils.createTimeLineItemFromUserActivity(userActivity)
        assertEquals(timelineItem.startTimeMills, 11.times(3600).times(1000))
        assertEquals(timelineItem.endTimeMills, 26.times(3600).times(1000))  //The new end time should not change the date.
        assertEquals(timelineItem.color, color)
    }
}
