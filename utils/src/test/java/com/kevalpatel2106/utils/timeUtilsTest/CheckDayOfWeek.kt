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
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Enclosed::class)
class CheckDayOfWeek {

    @RunWith(Parameterized::class)
    class CheckDayOfWeekParameterize(private val input: Int,
                                     private val expected: String) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                return arrayListOf(
                        arrayOf(1, "Sunday"),
                        arrayOf(2, "Monday"),
                        arrayOf(3, "Tuesday"),
                        arrayOf(4, "Wednesday"),
                        arrayOf(5, "Thursday"),
                        arrayOf(6, "Friday"),
                        arrayOf(7, "Saturday")
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun checkDayOfWeek() {
            Assert.assertEquals(expected, TimeUtils.getDayOfWeek(input))
        }
    }

    @RunWith(JUnit4::class)
    class CheckDayOfWeekNotParameterize {

        @Test
        @Throws(IOException::class)
        fun checkInvalidDayOfWeekNumber() {
            try {
                TimeUtils.getDayOfWeek(8)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
                //NO OP
            }
        }

    }


}
