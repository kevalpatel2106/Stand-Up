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
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

/**
 * Created by Keval on 06/03/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class MidnightCalenderTest {

    @Test
    @Throws(Exception::class)
    fun checkGetMidnightCal_WithMills_LocalTimeZone() {
        val unixMills = System.currentTimeMillis()

        val today12AmCal = Calendar.getInstance(TimeZone.getDefault())
        today12AmCal.timeInMillis = unixMills
        today12AmCal.set(Calendar.HOUR_OF_DAY, 0)
        today12AmCal.set(Calendar.MINUTE, 0)
        today12AmCal.set(Calendar.SECOND, 0)
        today12AmCal.set(Calendar.MILLISECOND, 0)

        Assert.assertEquals(today12AmCal.timeInMillis + TimeZone.getDefault().rawOffset,
                TimeUtils.getMidnightCal(unixMills).timeInMillis)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetMidnightCal_WithMills_UTCTimeZone() {
        val unixMills = System.currentTimeMillis()

        val today12AmCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        today12AmCal.timeInMillis = unixMills
        today12AmCal.set(Calendar.HOUR_OF_DAY, 0)
        today12AmCal.set(Calendar.MINUTE, 0)
        today12AmCal.set(Calendar.SECOND, 0)
        today12AmCal.set(Calendar.MILLISECOND, 0)

        Assert.assertEquals(today12AmCal.timeInMillis,
                TimeUtils.getMidnightCal(unixMills).timeInMillis)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetMidnightCal_WithDate_LocalTimeZone() {
        val testCal = Calendar.getInstance(TimeZone.getDefault())
        testCal.set(Calendar.HOUR_OF_DAY, 0)
        testCal.set(Calendar.MINUTE, 0)
        testCal.set(Calendar.SECOND, 0)
        testCal.set(Calendar.MILLISECOND, 0)

        Assert.assertEquals(testCal.timeInMillis + TimeZone.getDefault().rawOffset,
                TimeUtils.getMidnightCal(dayOfMonth = testCal.get(Calendar.DAY_OF_MONTH),
                        monthOfYear = testCal.get(Calendar.MONTH),
                        year = testCal.get(Calendar.YEAR)).timeInMillis)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetMidnightCal_WithDate_UTCTimeZone() {
        val testCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        testCal.set(Calendar.HOUR_OF_DAY, 0)
        testCal.set(Calendar.MINUTE, 0)
        testCal.set(Calendar.SECOND, 0)
        testCal.set(Calendar.MILLISECOND, 0)

        Assert.assertEquals(testCal.timeInMillis,
                TimeUtils.getMidnightCal(dayOfMonth = testCal.get(Calendar.DAY_OF_MONTH),
                        monthOfYear = testCal.get(Calendar.MONTH),
                        year = testCal.get(Calendar.YEAR)).timeInMillis)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetMilliSecFrom12AM_ZeroMills() {
        try {
            TimeUtils.millsFromMidnight(0)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
        }
    }


    @Test
    @Throws(Exception::class)
    fun checkGetMilliSecFrom12AM_NegativeMills() {
        try {
            TimeUtils.millsFromMidnight(-System.currentTimeMillis())
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
        }
    }
}