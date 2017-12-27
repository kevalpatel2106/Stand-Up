/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.utils.utilsTest

import com.kevalpatel2106.utils.TimeUtils
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.Parameterized
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Enclosed::class)
class CheckMonthName {

    @RunWith(Parameterized::class)
    class CheckMonthNameParameterize(private val input: Int,
                                     private val expected: String) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                return arrayListOf(
                        arrayOf(0, "January"),
                        arrayOf(1, "February"),
                        arrayOf(2, "March"),
                        arrayOf(3, "April"),
                        arrayOf(4, "May"),
                        arrayOf(5, "June"),
                        arrayOf(6, "July"),
                        arrayOf(7, "August"),
                        arrayOf(8, "September"),
                        arrayOf(9, "October"),
                        arrayOf(10, "November"),
                        arrayOf(11, "December")
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun checkMonthInput() {
            Assert.assertEquals(expected, TimeUtils.getMonthName(input))
        }
    }

    @RunWith(JUnit4::class)
    class CheckMonthNameNotParameterize {

        @Test
        @Throws(IOException::class)
        fun checkInvalidMonthNumber() {
            try {
                TimeUtils.getMonthName(12)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
                //NO OP
            }
        }

    }


}