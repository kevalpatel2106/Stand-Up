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

import com.kevalpatel2106.utils.annotations.OnlyForTesting
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
    const val ONE_DAY_MILLISECONDS = 86400000L

    fun convertToNano(timeInMills: Long): Long = TimeUnit.MILLISECONDS.toNanos(timeInMills)

    fun convertToMilli(timeInNano: Long): Long = TimeUnit.NANOSECONDS.toMillis(timeInNano)

    fun getMidnightCal(dayOfMonth: Int, monthOfYear: Int, year: Int): Calendar {
        val midnightCal = Calendar.getInstance(TimeZone.getDefault())
        midnightCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        midnightCal.set(Calendar.MONTH, monthOfYear)
        midnightCal.set(Calendar.YEAR, year)

        midnightCal.set(Calendar.HOUR_OF_DAY, 0)
        midnightCal.set(Calendar.MINUTE, 0)
        midnightCal.set(Calendar.SECOND, 0)
        midnightCal.set(Calendar.MILLISECOND, 0)

        return midnightCal
    }

    fun getMidnightCal(unixMills: Long): Calendar {
        val midnightCal = Calendar.getInstance(TimeZone.getDefault())
        midnightCal.timeInMillis = unixMills

        midnightCal.set(Calendar.HOUR_OF_DAY, 0)
        midnightCal.set(Calendar.MINUTE, 0)
        midnightCal.set(Calendar.SECOND, 0)
        midnightCal.set(Calendar.MILLISECOND, 0)

        return midnightCal
    }

    fun todayMidnightCal(): Calendar {
        return getMidnightCal(System.currentTimeMillis())
    }

    fun tomorrowMidnightCal(): Calendar {
        val cal = getMidnightCal(System.currentTimeMillis())
        cal.add(Calendar.DAY_OF_YEAR, 1)
        return cal
    }

    fun millsFromMidnight(unixMills: Long): Long {
        if (unixMills <= 0)
            throw IllegalArgumentException("Invalid unix time: ".plus(unixMills))
        return unixMills - getMidnightCal(unixMills).timeInMillis
    }

    fun millsFromMidnight(hourOfTheDay: Int, minutes: Int): Long {
        if (hourOfTheDay !in 0..23 || minutes !in 0..59)
            throw IllegalArgumentException("Invalid hours: $hourOfTheDay:$minutes")
        return hourOfTheDay * ONE_HOUR_MILLS + minutes * ONE_MIN_MILLS
    }

    fun todayMidnightMills(): Long {
        return todayMidnightCal().timeInMillis
    }

    fun currentMillsFromMidnight(): Long {
        return millsFromMidnight(System.currentTimeMillis())
    }

    //*********** Human readable month formats *********//

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

    fun getDayOfWeek(dayOfWeek: Int): String = when (dayOfWeek) {
        1 -> "Sunday"
        2 -> "Monday"
        3 -> "Tuesday"
        4 -> "Wednesday"
        5 -> "Thursday"
        6 -> "Friday"
        7 -> "Saturday"
        else -> throw IllegalArgumentException("Invalid day of week: ".plus(dayOfWeek))
    }

    //*********** Human readable time formats *********//

    fun calculateHumanReadableDurationFromNow(timeToCalculate: Long,
                                              @OnlyForTesting currentTime: Long = System.currentTimeMillis()): String {
        if (timeToCalculate < 0)
            throw IllegalArgumentException("Invalid negative time: $timeToCalculate")

        var diff = (currentTime - timeToCalculate)
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
        return result.trim()
    }


    /**
     * Converts [timeMills] into (hour)h (minutes)m format. Here hour will be in 24 hours format. If
     * the hour is 0, it will display (minutes)m (seconds)s format.
     */
    @JvmStatic
    fun convertToHourMinutes(timeMills: Long): String {
        if (timeMills < 0) throw IllegalArgumentException("Time cannot be negative.")

        val totalMins = timeMills / ONE_MIN_MILLS

        val hours = (totalMins.div(ONE_HOUR_MINS)).toInt()
        val mins = totalMins - (hours * ONE_HOUR_MINS)

        return if (hours > 0) {
            "${hours}h ${mins}m"
        } else {
            val seconds: Int = ((timeMills - totalMins * ONE_MIN_MILLS) / 1000).toInt()
            "${mins}m ${seconds}s"
        }

    }

    fun convertToHHmmaFrom12Am(millsFrom12Am: Long): String {
        if (millsFrom12Am !in 0 until TimeUtils.ONE_DAY_MILLISECONDS)
            throw IllegalArgumentException("Time is invalid: $millsFrom12Am")

        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = millsFrom12Am

        return String.format("%s:%s %s",
                if (cal.get(Calendar.HOUR) < 10) "0${cal.get(Calendar.HOUR)}" else "${cal.get(Calendar.HOUR)}",
                if (cal.get(Calendar.MINUTE) < 10) "0${cal.get(Calendar.MINUTE)}" else "${cal.get(Calendar.MINUTE)}",
                if (cal.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM")
    }
}
