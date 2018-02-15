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

import android.graphics.Color
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
class CalculateYBoundTest(private val viewWidth: Int,
                          private val heightPercent: Int,
                          private val startY: Float,
                          private val endY: Float) {


    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            return arrayListOf(
                    arrayOf(0, 100, 0F, 0F),
                    arrayOf(0, 0, 0F, 0F),
                    arrayOf(3600, 100, 0F, 3600F),
                    arrayOf(5400, 50, 2700F, 5400F),
                    arrayOf(4320, 30, 3024F, 4320F),
                    arrayOf(2880, 80, 576F, 2880F),
                    arrayOf(1728, 0, 1728F, 1728F)
            )
        }
    }


    @Test
    fun checkCalculateBlockCoordinates() {
        val list = TimeLineData(Color.RED, heightPercent, TestingUtils.getTestTimelineItems())
        list.calculateYBound(viewWidth)

        assertEquals(startY, list.startY, 0.1F)
        assertEquals(endY, list.endY - TimeLineConfig.LABLE_AREA_HEIGHT, 0.1F)
    }
}
