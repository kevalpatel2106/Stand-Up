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

/**
 * Created by Kevalpatel2106 on 04-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class TimeLineItemTest {

    @Test
    fun checkInitWithValidParams() {
        try {
            val currentTime = System.currentTimeMillis()
            val timelineItem = TimeLineItem(currentTime, currentTime + 1000, 123)

            Assert.assertEquals(currentTime, timelineItem.startTimeMills)
            Assert.assertEquals(currentTime + 1000, timelineItem.endTimeMills)
            Assert.assertEquals(123, timelineItem.color)
            Assert.assertEquals(0F, timelineItem.endX)
            Assert.assertEquals(0F, timelineItem.startX)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Test
    fun checkInitWithEndTimeLessThanStartTime() {
        try {
            val currentTime = System.currentTimeMillis()
            TimeLineItem(currentTime, currentTime - 1000, 123)

            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
            //NO OP
        }
    }

    @Test
    fun checkInitWithEndTimeEqualsThanStartTime() {
        try {
            val currentTime = System.currentTimeMillis()
            val timelineItem = TimeLineItem(currentTime, currentTime, 123)

            Assert.assertEquals(currentTime, timelineItem.startTimeMills)
            Assert.assertEquals(currentTime, timelineItem.endTimeMills)
            Assert.assertEquals(123, timelineItem.color)
            Assert.assertEquals(0F, timelineItem.endX)
            Assert.assertEquals(0F, timelineItem.startX)
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }
}