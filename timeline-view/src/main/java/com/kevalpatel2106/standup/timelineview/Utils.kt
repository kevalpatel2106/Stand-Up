package com.kevalpatel2106.standup.timelineview

/**
 * Created by Kevalpatel2106 on 18-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object Utils {

    fun convertTimeLineLengthToSeconds(timeLineLength: TimeLineLength): Long {

        return when (timeLineLength) {
            TimeLineLength.AN_HOUR -> 3600
            TimeLineLength.SIX_HOUR -> 6 * 3600
            TimeLineLength.TWELVE_HOUR -> 12 * 3600
            TimeLineLength.SIXTEEN_HOURS -> 16 * 3600
            TimeLineLength.A_DAY -> 24 * 3600
            else -> {
                throw IllegalArgumentException("Invalid argument type.")
            }
        }
    }

    fun convertToSeconds(timeInMills: Long): Long = timeInMills / 1000
}