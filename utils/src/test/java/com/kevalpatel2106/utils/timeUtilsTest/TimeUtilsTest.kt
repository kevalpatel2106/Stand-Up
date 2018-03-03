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
    fun checkConvertToNano() {
        val timeMills = System.currentTimeMillis()
        assertEquals(TimeUtils.convertToNano(timeMills), timeMills * 10_00_000)
    }

    @Test
    @Throws(Exception::class)
    fun checkConvertToNano_ZeroMills() {
        assertEquals(TimeUtils.convertToNano(0), 0)
    }


    @Test
    @Throws(Exception::class)
    fun checkConvertToNano_NegativeMills() {
        assertEquals(TimeUtils.convertToNano(-1000), -1000 * 10_00_000)
    }

    @Test
    @Throws(Exception::class)
    fun convertToMilli() {
        val timeNano = System.currentTimeMillis() * 1000000
        assertTrue(TimeUtils.convertToMilli(timeNano) == timeNano / 10_00_000)
    }

    @Test
    @Throws(Exception::class)
    fun checkConvertToMilli_ZeroMills() {
        assertEquals(TimeUtils.convertToMilli(0), 0)
    }


    @Test
    @Throws(Exception::class)
    fun checkConvertToMilli_NegativeMills() {
        val timeNano = System.currentTimeMillis() * 1000000
        assertEquals(TimeUtils.convertToMilli(-timeNano), -timeNano / 10_00_000)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetCalender12AMWithMills() {
        val today12AmCal = Calendar.getInstance()
        today12AmCal.set(Calendar.HOUR_OF_DAY, 0)
        today12AmCal.set(Calendar.MINUTE, 0)
        today12AmCal.set(Calendar.SECOND, 0)
        today12AmCal.set(Calendar.MILLISECOND, 0)

        assertEquals(TimeUtils.getCalender12AM(System.currentTimeMillis()).timeInMillis,
                today12AmCal.timeInMillis)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetTodays12AM() {
        val today12AmCal = Calendar.getInstance()
        today12AmCal.set(Calendar.HOUR_OF_DAY, 0)
        today12AmCal.set(Calendar.MINUTE, 0)
        today12AmCal.set(Calendar.SECOND, 0)
        today12AmCal.set(Calendar.MILLISECOND, 0)

        assertEquals(TimeUtils.getTodaysCalender12AM().timeInMillis, today12AmCal.timeInMillis)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetCalender12AM_WithDate() {
        val today12AmCal = Calendar.getInstance()
        today12AmCal.set(Calendar.HOUR_OF_DAY, 0)
        today12AmCal.set(Calendar.MINUTE, 0)
        today12AmCal.set(Calendar.SECOND, 0)
        today12AmCal.set(Calendar.MILLISECOND, 0)

        assertEquals(TimeUtils.getCalender12AM(today12AmCal.get(Calendar.DAY_OF_MONTH),
                today12AmCal.get(Calendar.MONTH),
                today12AmCal.get(Calendar.YEAR)).timeInMillis,
                today12AmCal.timeInMillis)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetMilliSecFrom12AM_WithZeroTime() {
        try {
            TimeUtils.getMilliSecFrom12AM(0)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
        }
    }
}
