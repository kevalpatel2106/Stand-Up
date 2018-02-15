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

package com.standup.timelineview

import java.util.*

/**
 * Created by Kevalpatel2106 on 18-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
data class TimeLineItem(
        val startTimeMillsFrom12Am: Long,    //Mills
        val endTimeMillsFrom12Am: Long      //Mills
) {

    init {
        //Validate
        if (endTimeMillsFrom12Am < startTimeMillsFrom12Am)
            throw IllegalArgumentException("End time cannot be less than start time.")

        val oneDayMills = 8_64_00_000L

        if (endTimeMillsFrom12Am !in 0..oneDayMills)
            throw IllegalArgumentException("End time cannot more than a day.")

        if (startTimeMillsFrom12Am !in 0..oneDayMills)
            throw IllegalArgumentException("Start time cannot more than a day.")
    }

    companion object {

        fun create(startTimeUnixMills: Long, endTimeUnixMills: Long) =
                TimeLineItem(getMilliSecFrom12AM(startTimeUnixMills), getMilliSecFrom12AM(endTimeUnixMills))

        internal fun getMilliSecFrom12AM(unixMills: Long): Long {
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
    }

    internal var startX: Float = 0F

    internal var endX: Float = 0F
}