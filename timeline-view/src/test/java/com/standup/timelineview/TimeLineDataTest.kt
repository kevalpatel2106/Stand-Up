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
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 23-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class TimeLineDataTest {

    @Test
    @Throws(Exception::class)
    fun checkColor() {
        val timeLineData = TimeLineData(Color.BLACK, 12, arrayListOf())
        assertEquals(Color.BLACK, timeLineData.color)
    }

    @Test
    @Throws(Exception::class)
    fun checkHeightPercent() {
        val timeLineData = TimeLineData(Color.BLACK, 12, arrayListOf())
        assertEquals(12, timeLineData.heightPercentage)
    }

    @Test
    @Throws(Exception::class)
    fun checkTimeLineItems() {
        val timeLineData = TimeLineData(Color.BLACK, 12, arrayListOf())
        assertTrue(timeLineData.timelineItems.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun checkStartY() {
        val timeLineData = TimeLineData(Color.BLACK, 12, arrayListOf())
        timeLineData.startY = 12F
        assertEquals(12F, timeLineData.startY)
    }

    @Test
    @Throws(Exception::class)
    fun checkEndY() {
        val timeLineData = TimeLineData(Color.BLACK, 12, arrayListOf())
        timeLineData.endY = 12F
        assertEquals(12F, timeLineData.endY)
    }

    @Test
    @Throws(Exception::class)
    fun checkEqual() {
        val timeLineData = TimeLineData(Color.BLACK, 12, arrayListOf())
        val timeLineData1 = TimeLineData(Color.BLACK, 12, arrayListOf())
        val timeLineData2 = TimeLineData(Color.BLUE, 12, arrayListOf())

        assertEquals(timeLineData, timeLineData1)
        assertNotEquals(timeLineData2, timeLineData)
        assertNotEquals(timeLineData2, timeLineData1)
    }

    @Test
    @Throws(Exception::class)
    fun checkEqualAndHashcode() {
        val timeLineData = TimeLineData(Color.BLACK, 12, arrayListOf())
        val timeLineData1 = TimeLineData(Color.BLACK, 12, arrayListOf())
        val timeLineData2 = TimeLineData(Color.BLUE, 12, arrayListOf())

        assertEquals(timeLineData.hashCode(), timeLineData1.hashCode())
        assertNotEquals(timeLineData2.hashCode(), timeLineData.hashCode())
        assertNotEquals(timeLineData2.hashCode(), timeLineData1.hashCode())
    }
}
