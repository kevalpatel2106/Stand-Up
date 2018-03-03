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
class ConvertToHHmmaFrom12AmTest {

    @RunWith(Parameterized::class)
    class ConvertToHHmmaFrom12AmParameterizeTest(private val value: Long,
                                                 private val expected: String) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                return arrayListOf(
                        arrayOf(0, "00:00 AM"),
                        arrayOf(TimeUtils.ONE_MIN_MILLS, "00:01 AM"),
                        arrayOf(TimeUtils.ONE_HOUR_MILLS, "01:00 AM"),
                        arrayOf(TimeUtils.ONE_HOUR_MILLS + TimeUtils.ONE_MIN_MILLS, "01:01 AM"),
                        arrayOf(10 * TimeUtils.ONE_HOUR_MILLS + 10 * TimeUtils.ONE_MIN_MILLS, "10:10 AM"),
                        arrayOf(12 * TimeUtils.ONE_HOUR_MILLS + 10 * TimeUtils.ONE_MIN_MILLS, "00:10 PM"),
                        arrayOf(13 * TimeUtils.ONE_HOUR_MILLS + TimeUtils.ONE_MIN_MILLS, "01:01 PM"),
                        arrayOf(23 * TimeUtils.ONE_HOUR_MILLS + 59 * TimeUtils.ONE_MIN_MILLS, "11:59 PM")
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun testConvertToHHmmaFrom12Am() {
            Assert.assertEquals(expected, TimeUtils.convertToHHmmaFrom12Am(value))
        }
    }

    @RunWith(JUnit4::class)
    class ConvertToHHmmaFrom12AmNonParameterizeTest {

        @Test
        @Throws(IOException::class)
        fun testConvertToHHmmaFrom12Am_NegativeMills() {
            try {
                TimeUtils.convertToHHmmaFrom12Am(-1000)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
            }
        }

        @Test
        @Throws(IOException::class)
        fun testConvertToHHmmaFrom12Am_MoreThanOneDayMills() {
            try {
                TimeUtils.convertToHHmmaFrom12Am(25 * TimeUtils.ONE_HOUR_MILLS)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
            }
        }

        @Test
        @Throws(IOException::class)
        fun testConvertToHHmmaFrom12Am_OneDayMills() {
            try {
                TimeUtils.convertToHHmmaFrom12Am(24 * TimeUtils.ONE_HOUR_MILLS)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
            }
        }
    }
}
