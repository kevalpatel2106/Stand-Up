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
class GetMilliSecFrom12AMUnixMillsTest {

    @RunWith(Parameterized::class)
    class GetMilliSecFrom12AMParameterizeTest(private val value: Long,
                                              private val expected: Long) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                return arrayListOf(
                        arrayOf(60_000, 60_000),            // 00:01 hours
                        arrayOf(86_400_000, 0),             // 24:00 hours
                        arrayOf(2 * 86_400_000, 0),         // 48:00 hours
                        arrayOf(86_460_000, 60_000),        // 24:01 hours
                        arrayOf(2 * 86_460_000, 120_000)    // 24:01 hours
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun testGetMilliSecFrom12AM() {
            Assert.assertEquals(expected, TimeUtils.getMilliSecFrom12AM(value))
        }
    }

    @RunWith(JUnit4::class)
    class GetMilliSecFrom12AMNonParameterizeTest {

        @Test
        @Throws(IOException::class)
        fun testGetMilliSecFrom12AM_NegativeMills() {
            try {
                TimeUtils.getMilliSecFrom12AM(-1000)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
            }
        }

        @Test
        @Throws(IOException::class)
        fun testGetMilliSecFrom12AM_ZeroMills() {
            try {
                TimeUtils.getMilliSecFrom12AM(0)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
            }
        }
    }
}
