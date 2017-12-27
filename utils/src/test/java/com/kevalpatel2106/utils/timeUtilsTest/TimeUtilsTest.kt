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

package com.kevalpatel2106.utils.timeUtilsTest

import com.kevalpatel2106.utils.TimeUtils
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class TimeUtilsTest {

    @Test
    @Throws(Exception::class)
    fun convertToNano() {
        val timeMills = System.currentTimeMillis()
        assertTrue(TimeUtils.convertToNano(timeMills) == timeMills * 1000000)
    }

    @Test
    @Throws(Exception::class)
    fun convertToMilli() {
        val timeNano = System.currentTimeMillis() * 1000000
        assertTrue(TimeUtils.convertToMilli(timeNano) == timeNano / 1000000)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetMilliSecFrom12AM() {
        val today12AmCal = Calendar.getInstance()
        today12AmCal.set(Calendar.HOUR, 0)
        today12AmCal.set(Calendar.MINUTE, 0)
        today12AmCal.set(Calendar.SECOND, 0)
        today12AmCal.set(Calendar.MILLISECOND, 0)
        today12AmCal.add(Calendar.HOUR, 2)
        assertEquals(TimeUtils.getMilliSecFrom12AM(today12AmCal.timeInMillis),
                2L.times(3600L).times(1000L))
    }

    @Test
    @Throws(Exception::class)
    fun checkGetMilliSecFrom12AMWithZeroTime() {
        try {
            TimeUtils.getMilliSecFrom12AM(0)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
        }
    }
}