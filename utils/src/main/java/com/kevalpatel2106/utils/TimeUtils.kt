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
}