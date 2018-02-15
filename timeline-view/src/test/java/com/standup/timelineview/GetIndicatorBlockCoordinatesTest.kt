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

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Created by Kevalpatel2106 on 05-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(Parameterized::class)
class GetIndicatorBlockCoordinatesTest(private val timeLineLength: TimeLineLength,
                                       private val startEndDiff: Long,
                                       private val expectedSize: Int) {


    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            return arrayListOf(
                    arrayOf(TimeLineLength.AN_HOUR, 360L /* 10 mins */, 6),
                    arrayOf(TimeLineLength.SIX_HOUR, 3600L /* 1 hour */, 6),
                    arrayOf(TimeLineLength.TWELVE_HOUR, 3600L /* 1 hour */, 12),
                    arrayOf(TimeLineLength.SIXTEEN_HOURS, 3600L /* 1 hour */, 16),
                    arrayOf(TimeLineLength.A_DAY, 3600L /* 1 hour */, 24)
            )
        }
    }

    @Test
    fun checkGetIndicatorBlockCoordinatesCheckSize() {
        val indicators = Utils.getIndicatorBlockList(timeLineLength)
        Assert.assertEquals(indicators.size, expectedSize)
    }

    @Test
    fun checkGetIndicatorBlockCoordinatesCheckDifference() {
        val indicators = Utils.getIndicatorBlockList(timeLineLength)
        indicators.forEach { it -> assertEquals(it.endTimeMillsFrom12Am - it.startTimeMillsFrom12Am, startEndDiff) }
    }

    @Test
    fun checkGetIndicatorBlockCoordinatesCheckDiffWithPrevious() {
        val indicators = Utils.getIndicatorBlockList(timeLineLength)
        for (index in 1 until indicators.size) {
            assertEquals(indicators[index - 1].endX - indicators[index].startX, 0F)
        }
    }
}