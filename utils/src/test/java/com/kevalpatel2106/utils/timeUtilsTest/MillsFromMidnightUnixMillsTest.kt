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
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.Parameterized
import java.io.IOException
import java.util.*

/**
 * Created by Kevalpatel2106 on 01-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Enclosed::class)
class MillsFromMidnightUnixMillsTest {

    @RunWith(Parameterized::class)
    class MillsFromMidnightParameterizeTest(private val value: Long,
                                            private val expected: Long) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                val offset = TimeZone.getDefault().rawOffset

                return arrayListOf(
                        arrayOf(60_000, 60_000 + offset),            // 00:01 hours
                        arrayOf(86_400_000, 0 + offset),             // 24:00 hours
                        arrayOf(2 * 86_400_000, 0 + offset),         // 48:00 hours
                        arrayOf(86_460_000, 60_000 + offset),        // 24:01 hours
                        arrayOf(2 * 86_460_000, 120_000 + offset),   // 24:01 hours
                        arrayOf(1537533123000 /* Fri Sep 21 2018 12:32:03 UTC */,
                                1537533123000 /* Fri Sep 21 2018 12:32:03 UTC */ - 1537488000000 /* Fri Sep 21 2018 00:00:00 UTC */ + offset)
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun testMillsFromMidnight() {
            Assert.assertEquals(expected, TimeUtils.millsFromMidnight(value))
        }
    }

    @RunWith(JUnit4::class)
    class MillsFromMidnightNonParameterizeTest {


        //TODO Fix test
//        @Test
//        @Throws(IOException::class)
//        fun testMillsFromMidnight_LocalTimeZone() {
//            try {
//                val testCal = Calendar.getInstance(TimeZone.getDefault())
//                testCal.set(Calendar.HOUR_OF_DAY, 1)
//                testCal.set(Calendar.MINUTE, 10)
//                testCal.set(Calendar.SECOND, 10)
//                testCal.set(Calendar.MILLISECOND, 0)
//
//                Assert.assertEquals(1 * TimeUtils.ONE_HOUR_MILLS
//                        + 10 * TimeUtils.ONE_MIN_MILLS
//                        + 10 * 1000,
//                        TimeUtils.millsFromMidnight(testCal.timeInMillis))
//            } catch (e: IllegalArgumentException) {
//                Assert.fail()
//            }
//        }


        @Test
        @Throws(IOException::class)
        fun testMillsFromMidnight_UTCTimeZone() {
            try {
                val testCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                testCal.set(Calendar.HOUR_OF_DAY, 1)
                testCal.set(Calendar.MINUTE, 10)
                testCal.set(Calendar.SECOND, 10)
                testCal.set(Calendar.MILLISECOND, 0)

                Assert.assertEquals(1 * TimeUtils.ONE_HOUR_MILLS
                        + 10 * TimeUtils.ONE_MIN_MILLS
                        + 10 * 1000 + TimeZone.getDefault().rawOffset,
                        TimeUtils.millsFromMidnight(testCal.timeInMillis))
            } catch (e: IllegalArgumentException) {
                Assert.fail()
            }
        }

        @Test
        @Throws(IOException::class)
        fun testMillsFromMidnight_NegativeMills() {
            try {
                TimeUtils.millsFromMidnight(-1000)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
            }
        }

        @Test
        @Throws(IOException::class)
        fun testMillsFromMidnight_ZeroMills() {
            try {
                TimeUtils.millsFromMidnight(0)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
            }
        }
    }
}
