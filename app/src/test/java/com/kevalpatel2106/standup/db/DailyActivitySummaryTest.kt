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

package com.kevalpatel2106.standup.db

import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.Utils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Kevalpatel2106 on 26-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@Suppress("DEPRECATION")
@RunWith(JUnit4::class)
class DailyActivitySummaryTest {
    private val DIFF_BETWEEN_END_AND_START = 60000L   //60 Sec
    private val NUMBER_OF_ITEM = 10
    private val STANDING_EVENT_POS = arrayOf(2, 6, 8)
    private val SITTING_EVENT_POS = arrayOf(1, 4, 7, 9)

    private fun getMockUserActivityList(): ArrayList<UserActivity> {
        val userActivities = ArrayList<UserActivity>(NUMBER_OF_ITEM)

        (NUMBER_OF_ITEM downTo 1 step 1).mapTo(userActivities) {

            val startTime = System.currentTimeMillis() - (it * DIFF_BETWEEN_END_AND_START)
            val endTime = startTime + DIFF_BETWEEN_END_AND_START

            UserActivity(eventStartTimeMills = startTime,
                    eventEndTimeMills = endTime,
                    type = when {
                        SITTING_EVENT_POS.contains(it) -> UserActivityType.SITTING.name.toLowerCase()
                        STANDING_EVENT_POS.contains(it) -> UserActivityType.MOVING.name.toLowerCase()
                        else -> UserActivityType.NOT_TRACKED.name.toLowerCase()
                    },
                    isSynced = false)
        }
        return userActivities
    }

    @Test
    @Throws(IOException::class)
    fun checkForInvalidDate() {
        try {
            DailyActivitySummary(dayOfMonth = 0,
                    monthOfYear = 0,
                    year = 2017,
                    dayActivity = getMockUserActivityList())
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkForInvalidMonth() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 12,
                    year = 2017,
                    dayActivity = getMockUserActivityList())
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkForInvalidYear() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 1899,
                    dayActivity = getMockUserActivityList())
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkForEmptyActivityList() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = ArrayList())
            //Test passed.
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkForInitWithValidParams() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = getMockUserActivityList())
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkForFutureDay() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2030,
                    dayActivity = getMockUserActivityList())
            Assert.fail()
        } catch (e: IllegalStateException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkForPastDate() {
        try {
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = getMockUserActivityList())

            //Check end time
            val tempCal = TimeUtils.getCalender12AM(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017)
            Assert.assertEquals(summary.endTimeMills, tempCal.timeInMillis + TimeUtils.ONE_DAY_MILLISECONDS)

            //Check duration
            val expectedDuration = 86400000L
            Assert.assertEquals(summary.durationMills, expectedDuration)
            Assert.assertEquals(summary.durationTimeHours, "24h 0m")
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkDurationForToday() {
        try {
            val nowCal = Calendar.getInstance()
            val summary = DailyActivitySummary(dayOfMonth = nowCal.get(Calendar.DAY_OF_MONTH),
                    monthOfYear = nowCal.get(Calendar.MONTH),
                    year = nowCal.get(Calendar.YEAR),
                    dayActivity = ArrayList())

            //Check end time
            Assert.assertTrue(System.currentTimeMillis() - summary.endTimeMills < 2000 /* 2 seconds delta */)

            //Check for the duration
            val expectedDuration = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis())
            Assert.assertTrue(Math.abs(summary.durationMills - expectedDuration) < 100)
            Assert.assertEquals(summary.durationTimeHours, TimeUtils.convertToHourMinutes(summary.durationMills))
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }


    @Test
    @Throws(IOException::class)
    fun checkForEndTimeBeforeStartTime() {
        try {
            val userActivities = ArrayList<UserActivity>(1)
            userActivities.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                    eventEndTimeMills = System.currentTimeMillis() - 60000,
                    isSynced = true,
                    type = UserActivityType.MOVING.name.toLowerCase()))


            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = userActivities)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkStandingTime() {
        try {
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = getMockUserActivityList())

            Assert.assertEquals(summary.standingTimeMills, STANDING_EVENT_POS.size * DIFF_BETWEEN_END_AND_START)
            Assert.assertEquals(summary.standingTimeHours, "0h ".plus(STANDING_EVENT_POS.size).plus("m"))
            Assert.assertEquals(summary.standingPercent,
                    Utils.calculatePercent(STANDING_EVENT_POS.size.toLong(),
                            (STANDING_EVENT_POS.size + SITTING_EVENT_POS.size).toLong()).toFloat())
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }


    @Test
    @Throws(IOException::class)
    fun checkForZeroStandingTime() {
        try {
            val userActivities = ArrayList<UserActivity>(1)
            userActivities.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 60000,
                    eventEndTimeMills = System.currentTimeMillis(),
                    isSynced = true,
                    type = UserActivityType.SITTING.name.toLowerCase()))


            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = userActivities)
            //Test passed.
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkStandingTimeForEmptyActivityList() {
        try {
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = ArrayList())

            Assert.assertEquals(summary.standingPercent, 0F)
            Assert.assertEquals(summary.standingTimeMills, 0L)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkSittingTime() {
        try {
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = getMockUserActivityList())

            Assert.assertEquals(summary.sittingTimeMills, SITTING_EVENT_POS.size * DIFF_BETWEEN_END_AND_START)
            Assert.assertEquals(summary.sittingTimeHours, "0h ".plus(SITTING_EVENT_POS.size).plus("m"))
            Assert.assertEquals(summary.sittingPercent,
                    Utils.calculatePercent(SITTING_EVENT_POS.size.toLong(),
                            (STANDING_EVENT_POS.size + SITTING_EVENT_POS.size).toLong()).toFloat())
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkForZeroSittingTime() {
        try {
            val userActivities = ArrayList<UserActivity>(1)
            userActivities.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 60000,
                    eventEndTimeMills = System.currentTimeMillis(),
                    isSynced = true,
                    type = UserActivityType.MOVING.name.toLowerCase()))


            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = userActivities)
            //Test passed.
        } catch (e: Exception) {
            Assert.fail()
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkSittingTimeForEmptyActivityList() {
        try {
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = ArrayList())

            Assert.assertEquals(summary.sittingPercent, 0F)
            Assert.assertEquals(summary.sittingTimeMills, 0L)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }


    @Test
    @Throws(IOException::class)
    fun checkNotTrackedDuration() {
        try {
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = getMockUserActivityList())

            Assert.assertEquals(summary.notTrackedTime,
                    86400000 - (STANDING_EVENT_POS.size + SITTING_EVENT_POS.size) * DIFF_BETWEEN_END_AND_START)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkConversionFromDayActivityListWithEmptyList() {
        try {
            DailyActivitySummary.convertToValidUserActivityList(ArrayList())
            //Test passed
        } catch (e: IllegalArgumentException) {
            Assert.fail(e.message)
        }
    }

    @Test
    @Throws(IOException::class)
    fun checkConversionFromDayActivityList() {
        val dayActivity = getMockUserActivityList()
        val originalSize = dayActivity.size

        DailyActivitySummary.convertToValidUserActivityList(dayActivity)
        val summary = DailyActivitySummary.fromDayActivityList(dayActivity)

        //Check the user activity list
        Assert.assertEquals(summary.dayActivity.size, originalSize)
        for (i in 0 until summary.dayActivity.size - 1) {
            Assert.assertTrue(summary.dayActivity[i].eventStartTimeMills < summary.dayActivity[i + 1].eventStartTimeMills)
        }

        //Check day, month, year
        val expectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val expectedMonth = Calendar.getInstance().get(Calendar.MONTH)
        val expectedYear = Calendar.getInstance().get(Calendar.YEAR)
        Assert.assertEquals(summary.dayOfMonth, expectedDay)
        Assert.assertEquals(summary.monthOfYear, expectedMonth)
        Assert.assertEquals(summary.year, expectedYear)
        Assert.assertEquals(summary.monthInitials, TimeUtils.getMonthInitials(expectedMonth))

        //Check the duration
        val expectedDuration = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis())
        Assert.assertTrue(Math.abs(expectedDuration - summary.durationMills) < 5000L /* Allow 5 second difference*/)
        Assert.assertEquals(TimeUtils.convertToHourMinutes(summary.durationMills), summary.durationTimeHours)

        //Check standing time
        val expectedStandingTime = DIFF_BETWEEN_END_AND_START * STANDING_EVENT_POS.size
        Assert.assertTrue(Math.abs(expectedStandingTime - summary.standingTimeMills) < 1000L /* Allow 1 second difference*/)
        Assert.assertEquals("0h ".plus(STANDING_EVENT_POS.size).plus("m"), summary.standingTimeHours)

        //Check sitting time
        val expectedSittingTime = DIFF_BETWEEN_END_AND_START * SITTING_EVENT_POS.size
        Assert.assertTrue(Math.abs(expectedSittingTime - summary.sittingTimeMills) < 1000L /* Allow 1 second difference*/)
        Assert.assertEquals("0h ".plus(SITTING_EVENT_POS.size).plus("m"), summary.sittingTimeHours)
    }


    @Test
    @Throws(IOException::class)
    fun checkMonthInitials() {
        try {
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    dayActivity = getMockUserActivityList())

            Assert.assertEquals(summary.monthInitials, "DEC")
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun checkEquals() {
        val summary1 = DailyActivitySummary(1, 2, 2017, ArrayList())
        val summary2 = DailyActivitySummary(1, 2, 2017, ArrayList())
        val summary3 = DailyActivitySummary(2, 2, 2017, ArrayList())

        Assert.assertEquals(summary1, summary2)
        Assert.assertNotEquals(summary1, summary3)
        Assert.assertNotEquals(summary2, summary3)
        Assert.assertNotEquals(summary1, null)
        Assert.assertNotEquals(summary1, Unit)
    }

    @Test
    fun checkHashCode() {
        val summary1 = DailyActivitySummary(1, 2, 2017, ArrayList())
        Assert.assertEquals(summary1.hashCode(), 1000 + 2 * 100 + 2017 * 10)
    }

    @Test
    fun checkEqualsWithHashCode() {
        val summary1 = DailyActivitySummary(1, 2, 2017, ArrayList())
        val summary2 = DailyActivitySummary(1, 2, 2017, ArrayList())

        Assert.assertEquals(summary1.hashCode(), summary2.hashCode())
    }
}