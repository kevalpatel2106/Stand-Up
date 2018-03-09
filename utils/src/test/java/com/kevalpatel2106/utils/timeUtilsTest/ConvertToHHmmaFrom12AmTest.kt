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
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Enclosed::class)
class ConvertToHHmmaFrom12AmTest {

    @RunWith(Parameterized::class)
    class ConvertToHHmmaFrom12AmParameterizeTest(private val value: Long) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Long> {
                return arrayListOf(
                        0,
                        TimeUtils.ONE_MIN_MILLS,
                        TimeUtils.ONE_HOUR_MILLS,
                        TimeUtils.ONE_HOUR_MILLS + TimeUtils.ONE_MIN_MILLS,
                        10 * TimeUtils.ONE_HOUR_MILLS + 10 * TimeUtils.ONE_MIN_MILLS,
                        12 * TimeUtils.ONE_HOUR_MILLS + 10 * TimeUtils.ONE_MIN_MILLS,
                        13 * TimeUtils.ONE_HOUR_MILLS + TimeUtils.ONE_MIN_MILLS,
                        23 * TimeUtils.ONE_HOUR_MILLS + 59 * TimeUtils.ONE_MIN_MILLS
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun testConvertToHHmmaFrom12Am() {
            Assert.assertEquals(
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(value)).replace("12", "00"),
                    TimeUtils.convertToHHmmaFrom12Am(value)
            )
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
