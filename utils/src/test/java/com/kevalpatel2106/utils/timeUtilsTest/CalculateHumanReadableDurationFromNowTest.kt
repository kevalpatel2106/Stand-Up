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

/**
 * Created by Kevalpatel2106 on 01-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Enclosed::class)
class CalculateHumanReadableDurationFromNowTest {

    @RunWith(Parameterized::class)
    class CalculateHumanReadableDurationFromNowParameterizeTest(private val currentTime: Long,
                                                                private val value: Long,
                                                                private val expected: String) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {

                val currentTime = System.currentTimeMillis()

                return arrayListOf(
                        arrayOf(currentTime,
                                currentTime,
                                ""),
                        arrayOf(currentTime,
                                currentTime - 1_000 /* 1 sec */,
                                "1 seconds"),
                        arrayOf(currentTime,
                                currentTime - TimeUtils.ONE_MIN_MILLS - 1_000 /* 1 sec */,
                                "1 minutes 1 seconds"),

                        arrayOf(currentTime,
                                currentTime - TimeUtils.ONE_HOUR_MILLS - 1_000 /* 1 sec */,
                                "1 hours 1 seconds"),
                        arrayOf(currentTime,
                                currentTime - TimeUtils.ONE_DAY_MILLISECONDS - 1_000 /* 1 sec */,
                                "24 hours 1 seconds"),

                        arrayOf(currentTime,
                                currentTime - TimeUtils.ONE_HOUR_MILLS - TimeUtils.ONE_MIN_MILLS,
                                "1 hours 1 minutes"),
                        arrayOf(currentTime,
                                currentTime - TimeUtils.ONE_DAY_MILLISECONDS - TimeUtils.ONE_MIN_MILLS,
                                "24 hours 1 minutes"),

                        arrayOf(currentTime,
                                currentTime - TimeUtils.ONE_HOUR_MILLS - TimeUtils.ONE_MIN_MILLS - 1_000 /* 1 sec */,
                                "1 hours 1 minutes 1 seconds"),
                        arrayOf(currentTime,
                                currentTime - TimeUtils.ONE_DAY_MILLISECONDS - TimeUtils.ONE_MIN_MILLS - 1_000 /* 1 sec */,
                                "24 hours 1 minutes 1 seconds"),

                        arrayOf(currentTime,
                                currentTime - TimeUtils.ONE_DAY_MILLISECONDS,
                                "24 hours")
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun testCalculateHumanReadableDurationFromNow() {
            Assert.assertEquals(expected, TimeUtils.calculateHumanReadableDurationFromNow(value, currentTime))
        }
    }

    @RunWith(JUnit4::class)
    class CalculateHumanReadableDurationFromNowNonParameterizeTest {

        @Test
        @Throws(IOException::class)
        fun testCalculateHumanReadableDurationFromNow_FutureTime() {
            try {
                TimeUtils.calculateHumanReadableDurationFromNow(System.currentTimeMillis() + 60_000)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
            }
        }

        @Test
        @Throws(IOException::class)
        fun testCalculateHumanReadableDurationFromNow_NegativeTime() {
            try {
                TimeUtils.calculateHumanReadableDurationFromNow(-60_000)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
            }
        }
    }
}
