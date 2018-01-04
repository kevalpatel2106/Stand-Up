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

package com.kevalpatel2106.standup.timelineview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.annotation.VisibleForTesting
import android.util.TypedValue

/**
 * Created by Kevalpatel2106 on 18-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object Utils {

    @VisibleForTesting
    internal fun convertTimeLineLengthToSeconds(timeLineLength: TimeLineLength): Long {

        return when (timeLineLength) {
            TimeLineLength.AN_HOUR -> 3600
            TimeLineLength.SIX_HOUR -> 6 * 3600
            TimeLineLength.TWELVE_HOUR -> 12 * 3600
            TimeLineLength.SIXTEEN_HOURS -> 16 * 3600
            TimeLineLength.A_DAY -> 24 * 3600
        }
    }

    @VisibleForTesting
    internal fun getNumberOfIndicatorBlocks(timeLineLength: TimeLineLength): Int = when (timeLineLength) {
        TimeLineLength.AN_HOUR -> 6
        TimeLineLength.SIX_HOUR -> 6
        TimeLineLength.TWELVE_HOUR -> 12
        TimeLineLength.SIXTEEN_HOURS -> 16
        TimeLineLength.A_DAY -> 24
    }

    fun convertToSeconds(timeInMills: Long): Long = timeInMills / 1000

    @SuppressLint("VisibleForTests")
    fun getIndicatorBlockList(timeLineLength: TimeLineLength): ArrayList<TimeLineItem> {
        val timeLineItems: ArrayList<TimeLineItem>

        return when (timeLineLength) {
            TimeLineLength.AN_HOUR -> {
                timeLineItems = ArrayList(6 /*Number of hours in a day*/)
                with(timeLineItems) {
                    (0..23).mapTo(this) {
                        TimeLineItem(startTime = it * 360L,
                                endTime = (it + 1) * 360L,
                                color = if (it % 2 == 0) Color.GRAY else Color.LTGRAY
                        )
                    }
                }
            }
            TimeLineLength.SIX_HOUR, TimeLineLength.TWELVE_HOUR, TimeLineLength.SIXTEEN_HOURS, TimeLineLength.A_DAY -> {

                val capacity: Int = Utils.getNumberOfIndicatorBlocks(timeLineLength)
                timeLineItems = ArrayList(capacity)
                with(timeLineItems) {
                    (0..capacity).mapTo(this) {
                        TimeLineItem(startTime = it * 3600L,
                                endTime = (it + 1) * 3600L,
                                color = if (it % 2 == 0) {
                                    Color.parseColor(TimeLineConfig.INDICATOR_DARK_COLOR)
                                } else {
                                    Color.parseColor(TimeLineConfig.INDICATOR_LIGHT_COLOR)
                                }
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("VisibleForTests")
    fun calculateBlockCoordinates(viewWidth: Int,
                                  items: ArrayList<TimeLineItem>,
                                  timelineDuration: TimeLineLength): ArrayList<TimeLineItem> {

        val timeLineLengthSec = Utils.convertTimeLineLengthToSeconds(timelineDuration)
        val eachSecondWidth = viewWidth.toFloat() / timeLineLengthSec

        items.forEach {
            it.startX = it.startTime * eachSecondWidth
            it.endX = it.endTime * eachSecondWidth
        }

        return items
    }


    @JvmStatic
    fun toPx(context: Context, dp: Int): Int = TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)
            .toInt()
}