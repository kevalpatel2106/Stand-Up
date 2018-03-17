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
    fun checkGetMidnightCal_WithMiils_WithUTCMidnightOffset() {
        val unixMills = System.currentTimeMillis()

        val utc12Am = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utc12Am.set(Calendar.HOUR_OF_DAY, 0)
        utc12Am.set(Calendar.MINUTE, 0)
        utc12Am.set(Calendar.SECOND, 0)
        utc12Am.set(Calendar.MILLISECOND, 0)

        Assert.assertEquals(utc12Am.timeInMillis - TimeZone.getDefault().rawOffset,
                TimeUtils.getMidnightCal(unixMills).timeInMillis)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetMidnightCal_WithMills() {
        val unixMills = System.currentTimeMillis()
        val cal12Am = TimeUtils.getMidnightCal(unixMills)

        Assert.assertEquals(cal12Am.get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        Assert.assertEquals(cal12Am.get(Calendar.MONTH), Calendar.getInstance().get(Calendar.MONTH))
        Assert.assertEquals(cal12Am.get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR))
        Assert.assertEquals(cal12Am.get(Calendar.HOUR), 0)
        Assert.assertEquals(cal12Am.get(Calendar.MINUTE), 0)
        Assert.assertEquals(cal12Am.get(Calendar.SECOND), 0)
        Assert.assertEquals(cal12Am.get(Calendar.MILLISECOND), 0)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetMidnightCal_WithDate_UtcOffset() {
        var testCal = Calendar.getInstance(TimeZone.getDefault())

        val midnightCal = TimeUtils.getMidnightCal(dayOfMonth = testCal.get(Calendar.DAY_OF_MONTH),
                monthOfYear = testCal.get(Calendar.MONTH),
                year = testCal.get(Calendar.YEAR))

        testCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        testCal.set(Calendar.HOUR_OF_DAY, 0)
        testCal.set(Calendar.MINUTE, 0)
        testCal.set(Calendar.SECOND, 0)
        testCal.set(Calendar.MILLISECOND, 0)

        Assert.assertEquals(testCal.timeInMillis - TimeZone.getDefault().rawOffset, midnightCal.timeInMillis)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetMidnightCal_WithDate() {

        val midnightCal = TimeUtils.getMidnightCal(
                dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                monthOfYear = Calendar.getInstance().get(Calendar.MONTH),
                year = Calendar.getInstance().get(Calendar.YEAR)
        )

        Assert.assertEquals(midnightCal.get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        Assert.assertEquals(midnightCal.get(Calendar.MONTH), Calendar.getInstance().get(Calendar.MONTH))
        Assert.assertEquals(midnightCal.get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR))
        Assert.assertEquals(midnightCal.get(Calendar.HOUR), 0)
        Assert.assertEquals(midnightCal.get(Calendar.MINUTE), 0)
        Assert.assertEquals(midnightCal.get(Calendar.SECOND), 0)
        Assert.assertEquals(midnightCal.get(Calendar.MILLISECOND), 0)
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
