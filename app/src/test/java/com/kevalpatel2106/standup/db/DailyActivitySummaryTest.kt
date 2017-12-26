package com.kevalpatel2106.standup.db

import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import com.kevalpatel2106.utils.TimeUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

/**
 * Created by Kevalpatel2106 on 26-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DailyActivitySummaryTest {
    private val DIFF_BETWEEN_END_AND_START = 60000L   //60 Sec
    private val NUMBER_OF_ITEM = 10
    private val STANDING_EVENT_POS = arrayOf(2, 6, 8)
    private val SITTING_EVENT_POS = arrayOf(1, 4, 7, 9)
    private val NOT_TRACKED_EVENT_POS = arrayOf(3, 5, 10)

    fun getMockUserActivityList(): ArrayList<UserActivity> {
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
    fun checkForInvalidDate() {
        try {
            DailyActivitySummary(dayOfMonth = 0,
                    monthOfYear = 0,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - 3600000,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = 1800000,
                    standingTimeMills = 1800000)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    fun checkForInvalidMonth() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 12,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - 3600000,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = 1800000,
                    standingTimeMills = 1800000)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    fun checkForInvalidYear() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 1899,
                    startTimeMills = System.currentTimeMillis() - 3600000,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = 1800000,
                    standingTimeMills = 1800000)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    fun checkForInvalidStartTime() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = 0,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = 1800000,
                    standingTimeMills = 1800000)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    fun checkForEndTimeBeforeStartTime() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis(),
                    endTimeMills = System.currentTimeMillis() - 3600000,
                    sittingTimeMills = 1800000,
                    standingTimeMills = 1800000)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    fun checkForInvalidStandingTime() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - 3600000,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = 1800000,
                    standingTimeMills = -1)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    fun checkForInvalidSittingTime() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - 3600000,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = -1,
                    standingTimeMills = 1800000)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    fun checkForDurationMoreThanADay() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - DailyActivitySummary.MAX_DURATION - 3600000,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = 1800000,
                    standingTimeMills = 1800000)
            Assert.fail()
        } catch (e: IllegalStateException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    fun checkForDurationLessThanTotalOfSittingAndStandingTime() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - 3600000,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = 3600000,
                    standingTimeMills = 1800000)
            Assert.fail()
        } catch (e: IllegalStateException) {
            //Test passed.
            //NO OP
        }
    }

    @Test
    fun checkForInitWithValidParams() {
        try {
            DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - 3600000,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = 900000,
                    standingTimeMills = 1800000)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun checkDuration() {
        try {
            val expectedDuration = 3600000L
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - expectedDuration,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = 900000,
                    standingTimeMills = 1800000)

            Assert.assertEquals(summary.durationMills, expectedDuration)
            Assert.assertEquals(summary.durationTimeHours, "1h 0m")
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun checkStandingTime() {
        try {
            val standingTimeMills = 900000L
            val sittingTimeMills = 1800000L
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - 3600000L,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = sittingTimeMills,
                    standingTimeMills = standingTimeMills)

            Assert.assertEquals(summary.standingTimeMills, standingTimeMills)
            Assert.assertEquals(summary.standingTimeHours, "0h 15m")
            Assert.assertEquals(summary.standingPercent, 25.0F)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun checkSittingTime() {
        try {
            val standingTimeMills = 900000L
            val sittingTimeMills = 1800000L
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - 3600000L,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = sittingTimeMills,
                    standingTimeMills = standingTimeMills)

            Assert.assertEquals(summary.sittingTimeMills, sittingTimeMills)
            Assert.assertEquals(summary.sittingTimeHours, "0h 30m")
            Assert.assertEquals(summary.sittingPercent, 50.0F)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun checkNotTracked() {
        try {
            val standingTimeMills = 900000L
            val sittingTimeMills = 1800000L
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - 3600000L,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = sittingTimeMills,
                    standingTimeMills = standingTimeMills)

            Assert.assertEquals(summary.notTrackedTime, 900000L)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun checkMonthInitials() {
        try {
            val standingTimeMills = 900000L
            val sittingTimeMills = 1800000L
            val summary = DailyActivitySummary(dayOfMonth = 1,
                    monthOfYear = 11,
                    year = 2017,
                    startTimeMills = System.currentTimeMillis() - 3600000L,
                    endTimeMills = System.currentTimeMillis(),
                    sittingTimeMills = sittingTimeMills,
                    standingTimeMills = standingTimeMills)

            Assert.assertEquals(summary.monthInitials, "DEC")
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun checkConvertToValidUserActivityList() {
        val dayActivity = getMockUserActivityList()
        dayActivity.add(UserActivity(eventStartTimeMills = 0,
                eventEndTimeMills = System.currentTimeMillis(),
                type = UserActivityType.SITTING.name,
                isSynced = true))
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = 0,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.CREATOR.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        for (i in 0 until resultArray.size - 1) {
            Assert.assertEquals(resultArray.size, dayActivity.size - 2)
            Assert.assertTrue(resultArray[i].eventStartTimeMills < resultArray[i + 1].eventStartTimeMills)
        }
    }

    @Test
    fun checkUserActivityList() {
        val dayActivity = getMockUserActivityList()
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = 0,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val originalSize = dayActivity.size
        val standingTimeMills = 900000L
        val sittingTimeMills = 1800000L
        val summary = DailyActivitySummary(dayOfMonth = 1,
                monthOfYear = 11,
                year = 2017,
                startTimeMills = System.currentTimeMillis() - 3600000L,
                endTimeMills = System.currentTimeMillis(),
                sittingTimeMills = sittingTimeMills,
                standingTimeMills = standingTimeMills)
        summary.userActivities = dayActivity

        //Check the user activity list
        Assert.assertEquals(summary.userActivities!!.size, originalSize - 1)
        for (i in 0 until summary.userActivities!!.size - 1) {
            Assert.assertTrue(summary.userActivities!![i].eventStartTimeMills < summary.userActivities!![i + 1].eventStartTimeMills)
        }
    }

    @Test
    fun checkConversionFromDayActivityListWithEmptyList() {
        val summary = DailyActivitySummary.fromDayActivityList(ArrayList())
        Assert.assertNull(summary)
    }

    @Test
    fun checkConversionFromDayActivityListWithAllNotEndingUserActivity() {
        val activity = ArrayList<UserActivity>()
        (1..10).mapTo(activity) {
            UserActivity(eventStartTimeMills = System.currentTimeMillis() + it * 1000,
                    eventEndTimeMills = 0,
                    type = UserActivityType.SITTING.name,
                    isSynced = true)
        }

        val summary = DailyActivitySummary.fromDayActivityList(ArrayList())
        Assert.assertNull(summary)
    }

    @Test
    fun checkConversionFromDayActivityList() {
        val dayActivity = getMockUserActivityList()
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = 0,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        val originalSize = dayActivity.size
        val summary = DailyActivitySummary.fromDayActivityList(dayActivity)

        Assert.assertNotNull(summary)

        //Check the user activity list
        Assert.assertEquals(summary!!.userActivities!!.size, originalSize - 1)
        for (i in 0 until summary.userActivities!!.size - 1) {
            Assert.assertTrue(summary.userActivities!![i].eventStartTimeMills < summary.userActivities!![i + 1].eventStartTimeMills)
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
        val expectedDuration = DIFF_BETWEEN_END_AND_START * NUMBER_OF_ITEM
        Assert.assertTrue(Math.abs(expectedDuration - summary.durationMills) < 1000L /* Allow 1 second difference*/)
        Assert.assertEquals("0h ".plus(NUMBER_OF_ITEM).plus("m"), summary.durationTimeHours)

        //Check standing time
        val expectedStandingTime = DIFF_BETWEEN_END_AND_START * STANDING_EVENT_POS.size
        Assert.assertTrue(Math.abs(expectedStandingTime - summary.standingTimeMills) < 1000L /* Allow 1 second difference*/)
        Assert.assertEquals("0h ".plus(STANDING_EVENT_POS.size).plus("m"), summary.standingTimeHours)

        //Check sitting time
        val expectedSittingTime = DIFF_BETWEEN_END_AND_START * SITTING_EVENT_POS.size
        Assert.assertTrue(Math.abs(expectedSittingTime - summary.sittingTimeMills) < 1000L /* Allow 1 second difference*/)
        Assert.assertEquals("0h ".plus(SITTING_EVENT_POS.size).plus("m"), summary.sittingTimeHours)
    }
}