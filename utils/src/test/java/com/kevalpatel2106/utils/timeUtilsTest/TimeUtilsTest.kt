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
    fun checkCurrentTimeMills() {
        assertTrue(Math.abs(System.currentTimeMillis() - TimeUtils.currentTimeMills()) < 10 /* 10 ms */)
    }

    @Test
    @Throws(Exception::class)
    fun checkCurrentMillsFromMidnight() {
        assertEquals(System.currentTimeMillis() % TimeUtils.ONE_DAY_MILLISECONDS,
                TimeUtils.currentMillsFromMidnight())
    }

    @Test
    @Throws(Exception::class)
    fun checkTodayMidnightMills() {
        val nowUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val midnightCal = TimeUtils.todayMidnightCal()
        assertEquals(midnightCal.timeInMillis % TimeUtils.ONE_DAY_MILLISECONDS, 0)

        assertEquals(midnightCal.timeZone.rawOffset, 0) //UTC

        assertEquals(midnightCal.get(Calendar.HOUR_OF_DAY), 0)
        assertEquals(midnightCal.get(Calendar.MILLISECOND), 0)
        assertEquals(midnightCal.get(Calendar.MINUTE), 0)
        assertEquals(midnightCal.get(Calendar.SECOND), 0)

        assertEquals(midnightCal.get(Calendar.DAY_OF_YEAR), nowUtc.get(Calendar.DAY_OF_YEAR))
        assertEquals(midnightCal.get(Calendar.MONTH), nowUtc.get(Calendar.MONTH))
        assertEquals(midnightCal.get(Calendar.YEAR), nowUtc.get(Calendar.YEAR))
    }

    @Test
    @Throws(Exception::class)
    fun checkTodayMidnightMills_And_CurrentMillsFromMidnight() {
        val midnightCal = TimeUtils.todayMidnightCal()
        assertEquals(System.currentTimeMillis() - TimeUtils.currentMillsFromMidnight(),
                midnightCal.timeInMillis)
    }

    @Test
    @Throws(Exception::class)
    fun checkTodayMidnightMills_And_GetMidnightCal() {
        assertTrue(Math.abs(TimeUtils.getMidnightCal(System.currentTimeMillis()).timeInMillis
                - TimeUtils.todayMidnightCal().timeInMillis) < 1000)
    }
}
