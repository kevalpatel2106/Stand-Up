/*
 *  Copyright 2017 Keval Patel.
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

import java.util.concurrent.TimeUnit

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object TimeUtils {
    private val ONE_MIN_MILLS = 60000
    private val ONE_HOUR_MINS = 60

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
}