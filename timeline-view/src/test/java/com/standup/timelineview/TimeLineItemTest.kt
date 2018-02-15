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
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

/**
 * Created by Kevalpatel2106 on 04-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class TimeLineItemTest {
    private val oneDayMills = 8_64_00_000L

    @Test
    fun checkInitWithValidParams() {
        try {
            val startTime = 1800_000L
            val endTime = 3600_000L
            val timelineItem = TimeLineItem(startTime, endTime )

            Assert.assertEquals(startTime, timelineItem.startTimeMillsFrom12Am)
            Assert.assertEquals(endTime, timelineItem.endTimeMillsFrom12Am)
            Assert.assertEquals(0F, timelineItem.endX)
            Assert.assertEquals(0F, timelineItem.startX)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun checkInitWithEndTimeLessThanStartTime() {
        try {
            val currentTime = 1000L
            TimeLineItem(currentTime, currentTime - 1000)

            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
            //NO OP
        }
    }

    @Test
    fun checkInitWithEndTimeMoreThanOneDayMills() {
        try {
            TimeLineItem(1000L, oneDayMills + 1)

            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
            //NO OP
        }
    }

    @Test
    fun checkInitWithStartTimeMoreThanOneDayMills() {
        try {
            TimeLineItem(oneDayMills + 1000, oneDayMills + 2000)

            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
            //NO OP
        }
    }

    @Test
    fun checkInitWithEndTimeLessThanZero() {
        try {
            TimeLineItem(1000L, - 10000L)

            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
            //NO OP
        }
    }

    @Test
    fun checkInitWithStartTimeLessThanZero() {
        try {
            TimeLineItem(-1000, 2000)

            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
            //NO OP
        }
    }

    @Test
    fun checkInitWithStartEndTimeEqualsOneDay() {
        try {
            val timelineItem = TimeLineItem(oneDayMills, oneDayMills)

            Assert.assertEquals(oneDayMills, timelineItem.startTimeMillsFrom12Am)
            Assert.assertEquals(oneDayMills, timelineItem.endTimeMillsFrom12Am)
            Assert.assertEquals(0F, timelineItem.endX)
            Assert.assertEquals(0F, timelineItem.startX)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun checkInitWithEndTimeEqualsStartTime() {
        try {
            val currentTime = 1000L
            val timelineItem = TimeLineItem(currentTime, currentTime)

            Assert.assertEquals(currentTime, timelineItem.startTimeMillsFrom12Am)
            Assert.assertEquals(currentTime, timelineItem.endTimeMillsFrom12Am)
            Assert.assertEquals(0F, timelineItem.endX)
            Assert.assertEquals(0F, timelineItem.startX)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun checkCreate() {
        try {
            val currentTime = TimeLineItem.getMilliSecFrom12AM(System.currentTimeMillis())
            val timelineItem = TimeLineItem(currentTime, currentTime + 1_000L)

            Assert.assertEquals(currentTime, timelineItem.startTimeMillsFrom12Am)
            Assert.assertEquals(currentTime + 1_000L, timelineItem.endTimeMillsFrom12Am)
            Assert.assertEquals(0F, timelineItem.endX)
            Assert.assertEquals(0F, timelineItem.startX)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }


    @Test
    @Throws(Exception::class)
    fun checkGetMilliSecFrom12AM() {
        val today12AmCal = Calendar.getInstance()
        today12AmCal.set(Calendar.HOUR_OF_DAY, 0)
        today12AmCal.set(Calendar.MINUTE, 0)
        today12AmCal.set(Calendar.SECOND, 0)
        today12AmCal.set(Calendar.MILLISECOND, 0)
        today12AmCal.add(Calendar.HOUR, 2)
        Assert.assertEquals(TimeLineItem.getMilliSecFrom12AM(today12AmCal.timeInMillis),
                2L.times(3600L).times(1000L))
    }

    @Test
    @Throws(Exception::class)
    fun checkGetMilliSecFrom12AMWithZeroTime() {
        try {
            TimeLineItem.getMilliSecFrom12AM(0)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
        }
    }
}
