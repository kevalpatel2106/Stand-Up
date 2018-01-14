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

package com.kevalpatel2106.utils

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object TimeUtils {
    const val ONE_MIN_MILLS = 60000L
    const val ONE_HOUR_MILLS = 3600000L
    const val ONE_HOUR_MINS = 60

    val ONE_DAY_MILLISECONDS = 86400000L

    /**
     * Converts [timeMills] into (hour)h (minutes)m format. Here hour will be in 24 hours format.
     */
    @JvmStatic
    fun convertToHourMinutes(timeMills: Long): String {
        if (timeMills < 0) throw IllegalArgumentException("Time cannot be negative.")

        val totalMins = timeMills / ONE_MIN_MILLS
        val hours = (totalMins.div(ONE_HOUR_MINS)).toInt()
        return "${hours}h ${totalMins - (hours * ONE_HOUR_MINS)}m"
    }

    fun convertToNano(timeInMills: Long): Long = TimeUnit.MILLISECONDS.toNanos(timeInMills)

    fun convertToMilli(timeInNano: Long): Long = TimeUnit.NANOSECONDS.toMillis(timeInNano)

    fun getMonthInitials(monthOfYear: Int): String = when (monthOfYear) {
        0 -> "JAN"
        1 -> "FEB"
        2 -> "MAR"
        3 -> "APR"
        4 -> "MAY"
        5 -> "JUN"
        6 -> "JUL"
        7 -> "AUG"
        8 -> "SEP"
        9 -> "OCT"
        10 -> "NOV"
        11 -> "DEC"
        else -> throw IllegalArgumentException("Invalid month: ".plus(monthOfYear))
    }

    fun getMonthName(monthOfYear: Int): String = when (monthOfYear) {
        0 -> "January"
        1 -> "February"
        2 -> "March"
        3 -> "April"
        4 -> "May"
        5 -> "June"
        6 -> "July"
        7 -> "August"
        8 -> "September"
        9 -> "October"
        10 -> "November"
        11 -> "December"
        else -> throw IllegalArgumentException("Invalid month: ".plus(monthOfYear))
    }

    fun getMilliSecFrom12AM(unixMills: Long): Long {
        if (unixMills <= 0)
            throw IllegalArgumentException("Invalid unix time: ".plus(unixMills))

        val today12AmCal = Calendar.getInstance()
        today12AmCal.timeInMillis = unixMills
        today12AmCal.set(Calendar.HOUR_OF_DAY, 0)
        today12AmCal.set(Calendar.MINUTE, 0)
        today12AmCal.set(Calendar.SECOND, 0)
        today12AmCal.set(Calendar.MILLISECOND, 0)
        return unixMills - today12AmCal.timeInMillis
    }

    fun getCalender12AM(dayOfMonth: Int, monthOfYear: Int, year: Int): Calendar {
        val calender12Am = getTodaysCalender12AM()
        calender12Am.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calender12Am.set(Calendar.MONTH, monthOfYear)
        calender12Am.set(Calendar.YEAR, year)
        return calender12Am
    }

    fun getCalender12AM(unixMills: Long): Calendar {
        val calender12Am = Calendar.getInstance()
        calender12Am.timeInMillis = unixMills
        calender12Am.set(Calendar.HOUR_OF_DAY, 0)
        calender12Am.set(Calendar.MINUTE, 0)
        calender12Am.set(Calendar.SECOND, 0)
        calender12Am.set(Calendar.MILLISECOND, 0)
        return calender12Am
    }

    fun getTodaysCalender12AM(): Calendar {
        val calender12Am = Calendar.getInstance()
        calender12Am.set(Calendar.HOUR_OF_DAY, 0)
        calender12Am.set(Calendar.MINUTE, 0)
        calender12Am.set(Calendar.SECOND, 0)
        calender12Am.set(Calendar.MILLISECOND, 0)
        return calender12Am
    }

    fun getTimeAgo(timeToCalculate: Long): String {
        var diff = (System.currentTimeMillis() - timeToCalculate)
        if (diff < 0) throw IllegalArgumentException("Cannot pass future time in argument.")

        val hours = diff.div(ONE_HOUR_MILLS).toInt()

        diff -= hours.times(ONE_HOUR_MILLS)
        val mins = diff.div(ONE_MIN_MILLS).toInt()

        diff -= mins.times(ONE_MIN_MILLS)
        val secs = diff.div(1000).toInt()


        var result = ""
        if (hours != 0) {
            result = result.plus(hours).plus(" hours ")
        }
        if (mins != 0) {
            result = result.plus(mins).plus(" minutes ")
        }
        if (secs != 0) {
            result = result.plus(secs).plus(" seconds ")
        }
        return result
    }
}