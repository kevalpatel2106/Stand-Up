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
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Enclosed::class)
class ConvertToHourMinutesTest {

    @RunWith(Parameterized::class)
    class ConvertToHourMinutesParameterizeTest(private val value: Long,
                                               private val expected: String) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                return arrayListOf(
                        arrayOf(0, "0h 0m"),
                        arrayOf(60000, "0h 1m"),
                        arrayOf(60100, "0h 1m"),
                        arrayOf(3600000, "1h 0m"),
                        arrayOf(4200000, "1h 10m"),
                        arrayOf(86400000, "24h 0m"),
                        arrayOf(86460000, "24h 1m")
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun testConvertToTwoDecimal() {
            Assert.assertEquals(expected, TimeUtils.convertToHourMinutes(value))
        }
    }

    @RunWith(JUnit4::class)
    class ConvertToHourMinutesNonParameterizeTest {

        @Test
        @Throws(IOException::class)
        fun testConvertToTwoDecimalWithNegativeInput() {
            try {
                TimeUtils.convertToHourMinutes(-1000)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
            }
        }
    }
}