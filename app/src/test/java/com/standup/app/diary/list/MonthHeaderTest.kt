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

package com.standup.app.diary.list

import org.junit.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.Parameterized
import java.io.IOException

/**
 * Created by Keval on 03/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(Enclosed::class)
class MonthHeaderTest {

    @RunWith(Parameterized::class)
    class MonthHeaderParameterizeTest(private val monthOfYear: Int,
                                      private val year: Int,
                                      private val expected: String) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                return arrayListOf(
                        arrayOf(0, 2017, "JAN, 2017"),
                        arrayOf(1, 2016, "FEB, 2016"),
                        arrayOf(2, 2011, "MAR, 2011"),
                        arrayOf(3, 2017, "APR, 2017"),
                        arrayOf(4, 2017, "MAY, 2017"),
                        arrayOf(5, 2017, "JUN, 2017"),
                        arrayOf(6, 2017, "JUL, 2017"),
                        arrayOf(7, 2017, "AUG, 2017"),
                        arrayOf(8, 2010, "SEP, 2010"),
                        arrayOf(9, 2017, "OCT, 2017"),
                        arrayOf(10, 2017, "NOV, 2017"),
                        arrayOf(11, 2017, "DEC, 2017")
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun checkMonthInitials() {
            val monthInitials = MonthHeader(monthOfYear, year)
            Assert.assertEquals(monthInitials.getMonthHeader(), expected)
        }
    }

    @RunWith(JUnit4::class)
    class MonthHeaderNonParameterizeTest {

        @Test
        @Throws(IOException::class)
        fun checkMonthInitialsWithInvalidMonth() {
            try {
                MonthHeader(12, 2018)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test Passed
            }
        }

        @Test
        @Throws(IOException::class)
        fun checkMonthInitialsWithInvalidYear() {
            try {
                MonthHeader(1, 1833)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test Passed
            }
        }

        @Test
        @Throws(IOException::class)
        fun checkMonthInitialsFutureDate() {
            try {
                MonthHeader(1, 2050)
                Assert.fail()
            } catch (e: IllegalStateException) {
                //Test Passed
            }
        }
    }
}