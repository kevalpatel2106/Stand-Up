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

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.util.TypedValue
import java.util.*

/**
 * Created by Kevalpatel2106 on 18-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object Utils {

    @VisibleForTesting
    internal fun getNumberOfIndicatorBlocks(timeLineLength: TimeLineLength): Int = when (timeLineLength) {
        TimeLineLength.AN_HOUR -> 6
        TimeLineLength.SIX_HOUR -> 6
        TimeLineLength.TWELVE_HOUR -> 12
        TimeLineLength.SIXTEEN_HOURS -> 16
        TimeLineLength.A_DAY -> 24
    }

    @SuppressLint("VisibleForTests")
    internal fun prepareLabels(viewWidth: Int, timeLineLength: TimeLineLength): ArrayList<Label> {

        val labelsCount = getNumberOfIndicatorBlocks(timeLineLength)
        val singleLabelBlockWidth: Float = (viewWidth / labelsCount).toFloat()
        val labels = ArrayList<Label>(labelsCount)

        return when (timeLineLength) {
            TimeLineLength.AN_HOUR -> {
                (1 until labelsCount).forEach {
                    labels.add(Label("${it.times(10)}", (it * singleLabelBlockWidth) + 10))
                }

                labels
            }
            TimeLineLength.SIX_HOUR, TimeLineLength.TWELVE_HOUR, TimeLineLength.SIXTEEN_HOURS, TimeLineLength.A_DAY -> {
                (1 until labelsCount).forEach {
                    labels.add(Label("$it".plus(":00"), (it * singleLabelBlockWidth) + 10))
                }
                labels
            }
        }
    }

    @JvmStatic
    internal fun toPx(context: Context, dp: Int): Int = TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)
            .toInt()

    internal fun getTouchLabel(touchX: Float, viewWidth: Int, timeLineLength: TimeLineLength): String {
        val totalSec = touchX / getWidthForEachSecond(viewWidth, timeLineLength)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = (totalSec * 1000L).toLong()
        return "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
    }

    internal fun isTouchable(
            touchX: Float,
            touchY: Float,
            viewX: Float,
            viewY: Float,
            viewWidth: Int,
            viewHeight: Int,
            labelAreaHeight: Float
    ): Boolean {

        //Check for the x coordinate
        if (touchX in viewX..(viewX + viewWidth)) {

            //Check for the y coordinate
            if (touchY in viewY..(viewY + viewHeight - labelAreaHeight)) {
                return true
            }
        }
        return false
    }
}
