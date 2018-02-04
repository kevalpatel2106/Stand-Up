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
class CalculateBlockCoordinatesTest(private val viewWidth: Int,
                                    private val timeLineLength: TimeLineLength,
                                    private val oneSecWidth: Float) {


    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            return arrayListOf(
                    arrayOf(0, TimeLineLength.AN_HOUR, 0F),
                    arrayOf(0, TimeLineLength.SIX_HOUR, 0F),
                    arrayOf(0, TimeLineLength.TWELVE_HOUR, 0F),
                    arrayOf(0, TimeLineLength.SIXTEEN_HOURS, 0F),
                    arrayOf(0, TimeLineLength.A_DAY, 0F),
                    arrayOf(3600, TimeLineLength.AN_HOUR, 1F),
                    arrayOf(5400, TimeLineLength.SIX_HOUR, 0.25F),
                    arrayOf(4320, TimeLineLength.TWELVE_HOUR, 0.1F),
                    arrayOf(2880, TimeLineLength.SIXTEEN_HOURS, 0.05F),
                    arrayOf(1728, TimeLineLength.A_DAY, 0.02F)
            )
        }
    }


    @Test
    fun checkCalculateBlockCoordinates() {
        val list = TestingUtils.getTestTimelineItems()
        val blocks = Utils.calculateBlockCoordinates(viewWidth, list, timeLineLength)

        Assert.assertEquals(list.size, blocks.size)

        blocks.forEach { it ->
            assertEquals(it.startX, it.startTimeMills.div(1000).times(oneSecWidth))
            assertEquals(it.endX, it.endTimeMills.div(1000).times(oneSecWidth))
        }
    }
}