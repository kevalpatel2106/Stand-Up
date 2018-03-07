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

package com.kevalpatel2106.common.db

import android.annotation.SuppressLint
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.Validator
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.Utils
import com.kevalpatel2106.utils.annotations.OnlyForTesting
import java.io.Serializable
import java.util.*

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
open class DailyActivitySummary
@Deprecated("Required to use fromDayActivityList() to generate summary from day activity.")
constructor(

        /**
         * Day of the month between 1 to 31.
         */
        val dayOfMonth: Int,

        /**
         * Month number from 0 to 11.
         */
        val monthOfYear: Int,

        /**
         * Year of the day.
         */
        val year: Int,

        /**
         * List od the [UserActivity] for given day.
         */
        val dayActivity: ArrayList<UserActivity>
) : Serializable {

    /**
     * 3 days initials of the month from [monthOfYear].
     */
    val monthInitials: String

    /**
     * Total sitting activity time in milliseconds.
     */
    val sittingTimeMills: Long
    val sittingTimeHours: String
    val sittingPercent: Float


    /**
     * Total standing activity time in milliseconds.
     */
    val standingTimeMills: Long
    val standingPercent: Float
    val standingTimeHours: String

    val totalDuration: Long
    val notTrackedDuration: Long
    val trackedDuration: Long
    val trackedDurationHours: String

    @VisibleForTesting
    @OnlyForTesting
    val startTimeMills: Long

    @VisibleForTesting
    @OnlyForTesting
    val endTimeMills: Long

    init {
        validateInputs()

        monthInitials = TimeUtils.getMonthInitials(monthOfYear)

        //----------------//
        // Calculate start and end time for the summary.
        // The start time will always be 12 AM of the day of summary/
        // The end time will be either the 23:59:59 of that day if the summary day is not current day
        // or current time if the summary is for today.
        //----------------//
        val summaryDayCal = TimeUtils.getMidnightCal(dayOfMonth, monthOfYear, year, false)
        val todayCal = TimeUtils.todayMidnightCal(false)

        //Calculate start time for the event.
        startTimeMills = summaryDayCal.timeInMillis

        //Calculate the ending time
        endTimeMills = when {
            summaryDayCal.after(todayCal) -> throw IllegalStateException("Future date.")
            summaryDayCal.before(todayCal) -> startTimeMills + TimeUtils.ONE_DAY_MILLISECONDS
            else -> System.currentTimeMillis()
        }

        if (endTimeMills < startTimeMills) {
            throw IllegalStateException("End time cannot be less than start time.")
        }

        //----------------//
        // Calculate Sitting, standing time
        //----------------//
        var tempStandingMills = 0L
        var tempSittingMills = 0L
        dayActivity.forEach({
            when (it.userActivityType) {
                UserActivityType.SITTING -> {
                    tempSittingMills += (it.eventEndTimeMills - it.eventStartTimeMills)
                }
                UserActivityType.MOVING -> {
                    tempStandingMills += (it.eventEndTimeMills - it.eventStartTimeMills)
                }
                UserActivityType.NOT_TRACKED -> {
                    //NO OP
                }
            }
        })

        standingTimeMills = tempStandingMills
        sittingTimeMills = tempSittingMills

        if (standingTimeMills < 0) {
            throw IllegalArgumentException("Standing time must be positive number or 0. Current: "
                    .plus(standingTimeMills))
        }

        if (sittingTimeMills < 0) {
            throw IllegalArgumentException("Sitting time must be positive number or 0. Current: "
                    .plus(sittingTimeMills))
        }
        sittingTimeHours = TimeUtils.convertToHourMinutes(sittingTimeMills)
        standingTimeHours = TimeUtils.convertToHourMinutes(standingTimeMills)

        //----------------//
        // Calculate duration
        //----------------//
        totalDuration = endTimeMills - startTimeMills
        if ((standingTimeMills + sittingTimeMills) > totalDuration) {
            throw IllegalStateException("Total of standing and sitting time is more than tracking duration.")
        }
        trackedDuration = sittingTimeMills + standingTimeMills
        trackedDurationHours = TimeUtils.convertToHourMinutes(trackedDuration)
        notTrackedDuration = totalDuration - (sittingTimeMills + standingTimeMills)


        //----------------//
        // Calculate percentages
        //----------------//
        if ((sittingTimeMills + standingTimeMills) == 0L) {
            sittingPercent = 0F
            standingPercent = 0F
        } else {
            sittingPercent = Utils.calculatePercent(sittingTimeMills, trackedDuration).toFloat()
            standingPercent = Utils.calculatePercent(standingTimeMills, trackedDuration).toFloat()
        }

    }

    private fun validateInputs() {
        if (!Validator.isValidDate(dayOfMonth)) {
            throw IllegalArgumentException("Day of month must be between 1 to 31. Current: "
                    .plus(dayOfMonth))
        }

        if (!Validator.isValidMonth(monthOfYear)) {
            throw IllegalArgumentException("Month must be between 0 to 11. Current: ".plus(monthOfYear))
        }

        if (!Validator.isValidYear(year)) {
            throw IllegalArgumentException("Year must be between 1900 to 2100. Current: ".plus(year))
        }

        //Process the user activity list for malfunctions
        convertToValidUserActivityList(dayActivity)
    }


    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is DailyActivitySummary) return false
        if (other.dayOfMonth == dayOfMonth && other.monthOfYear == monthOfYear && other.year == year)
            return true
        return false
    }

    override fun hashCode(): Int = dayOfMonth * 1000 + monthOfYear * 100 + year * 10

    companion object {

        /**
         * Create the [DailyActivitySummary] object from the list of [UserActivity] for the
         * given day. Make sure that [dayActivity] should contain the [UserActivity] which
         * happened on the same day (i.e. between 12 AM to 11:59:59 PM of the day). Generate
         * valid [UserActivity] list by using [convertToValidUserActivityList] and check that it's
         * not empty before passing it in the parameter [dayActivity]
         *
         * @throws IllegalArgumentException if the [dayActivity] doesn't contain [UserActivity] for
         * the same day.
         */
        @SuppressLint("VisibleForTests")
        @JvmStatic
        fun fromDayActivityList(dayActivity: ArrayList<UserActivity>): DailyActivitySummary {
            convertToValidUserActivityList(dayActivity)
            if (dayActivity.isEmpty()) throw IllegalStateException("Activity list cannot be null. Check first.")

            //Get calender
            val dayCal = Calendar.getInstance()
            dayCal.timeInMillis = dayActivity.first().eventStartTimeMills

            @Suppress("DEPRECATION")
            return DailyActivitySummary(
                    dayOfMonth = dayCal.get(Calendar.DAY_OF_MONTH),
                    monthOfYear = dayCal.get(Calendar.MONTH),
                    year = dayCal.get(Calendar.YEAR),
                    dayActivity = dayActivity)
        }

        fun convertToValidUserActivityList(dayActivity: ArrayList<UserActivity>) {
            //Sort by the event start time in descending order
            dayActivity.sortWith(Comparator { o1, o2 ->
                (o1.eventStartTimeMills - o2.eventStartTimeMills).toInt()
            })

            //Remove the last incomplete event
            val iter = dayActivity.iterator()
            if (!iter.hasNext()) return //List is empty

            val thisDayStartMills = TimeUtils.getMidnightCal(dayActivity.first().eventEndTimeMills, false).timeInMillis
            val thisDayEndingMills = thisDayStartMills + TimeUtils.ONE_DAY_MILLISECONDS - 1_000L

            while (iter.hasNext()) {
                val value = iter.next()

                //=============  Starting event time modifications ============= //
                //Remove invalid event
                //If the start time is 0, than it's invalid data. Remove it.
                if (value.eventStartTimeMills <= 0L) {
                    iter.remove()
                    continue
                }
                //If the event is started on previous day...
                if (value.eventStartTimeMills < thisDayStartMills) {
                    //Consider it starting on this day 12 AM.
                    value.eventStartTimeMills = thisDayStartMills
                }

                //=============  Ending event time modifications ============= //
                //If it is not last event and this event is not ended yet...it's invalid event
                if (value.eventEndTimeMills <= 0L) {
                    iter.remove()
                    continue
                }

                //If the event is ending next day...
                if (value.eventEndTimeMills > thisDayEndingMills) {
                    //Consider it ending on next day 12 AM.
                    value.eventEndTimeMills = thisDayEndingMills
                }
            }

        }
    }
}
