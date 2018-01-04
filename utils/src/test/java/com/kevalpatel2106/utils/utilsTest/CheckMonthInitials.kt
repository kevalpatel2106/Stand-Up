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
class CheckMonthInitials {

    @RunWith(Parameterized::class)
    class CheckMonthInitialsParameterize(private val input: Int,
                                         private val expected: String) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                return arrayListOf(
                        arrayOf(0, "JAN"),
                        arrayOf(1, "FEB"),
                        arrayOf(2, "MAR"),
                        arrayOf(3, "APR"),
                        arrayOf(4, "MAY"),
                        arrayOf(5, "JUN"),
                        arrayOf(6, "JUL"),
                        arrayOf(7, "AUG"),
                        arrayOf(8, "SEP"),
                        arrayOf(9, "OCT"),
                        arrayOf(10, "NOV"),
                        arrayOf(11, "DEC")
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun checkMonthInput() {
            Assert.assertEquals(expected, TimeUtils.getMonthInitials(input))
        }
    }

    @RunWith(JUnit4::class)
    class CheckMonthInitialsNotParameterize {

        @Test
        @Throws(IOException::class)
        fun checkInvalidMonthNumber() {
            try {
                TimeUtils.getMonthInitials(12)
                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
                //NO OP
            }
        }

    }


}