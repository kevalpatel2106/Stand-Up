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
@file:JvmName("TimeLineDataExt")

package com.standup.timelineview

import android.support.annotation.VisibleForTesting
import java.util.concurrent.TimeUnit

/**
 * Created by Kevalpatel2106 on 15-Feb-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

/**
 * Total milliseconds for one second.
 */
private const val ONE_SECOND_MILLS = 1000L

/**
 * Calculate the start and end bounds based on the [timelineDuration] and the [viewWidth].
 */
internal fun TimeLineData.calculateXBound(viewWidth: Int,
                                          timelineDuration: TimeLineLength) {
    val eachSecondWidth = getWidthForEachSecond(viewWidth, timelineDuration)

    timelineItems.forEach {

        //Calculate start bound
        it.startX = it.startTimeMillsFrom12Am.div(ONE_SECOND_MILLS) * eachSecondWidth

        //Calculate end bounds.
        it.endX = it.endTimeMillsFrom12Am.div(ONE_SECOND_MILLS) * eachSecondWidth
    }
}

/**
 * Calculate the start and end bounds based on the [TimeLineData.heightPercentage] and the [viewHeight].
 */
internal fun TimeLineData.calculateYBound(viewHeight: Int) {
    val heightPx = viewHeight * (1 - heightPercentage.toFloat() / 100)
    startY = heightPx

    endY = viewHeight.toFloat() - TimeLineConfig.LABLE_AREA_HEIGHT
}

@VisibleForTesting
internal fun getWidthForEachSecond(viewWidth: Int, timeLineLength: TimeLineLength): Float {
    if (viewWidth < 0) throw IllegalArgumentException("View width cannot be negative.")
    return viewWidth.toFloat() / getTotalSeconds(timeLineLength)
}

@VisibleForTesting
internal fun getTotalSeconds(timeLineLength: TimeLineLength): Long {

    return when (timeLineLength) {
        TimeLineLength.AN_HOUR -> TimeUnit.SECONDS.convert(1, TimeUnit.HOURS)
        TimeLineLength.SIX_HOUR -> TimeUnit.SECONDS.convert(6, TimeUnit.HOURS)
        TimeLineLength.TWELVE_HOUR -> TimeUnit.SECONDS.convert(12, TimeUnit.HOURS)
        TimeLineLength.SIXTEEN_HOURS -> TimeUnit.SECONDS.convert(16, TimeUnit.HOURS)
        TimeLineLength.A_DAY -> TimeUnit.SECONDS.convert(1, TimeUnit.DAYS)
    }
}
