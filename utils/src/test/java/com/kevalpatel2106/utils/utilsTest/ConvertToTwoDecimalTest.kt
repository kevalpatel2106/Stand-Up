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

import com.kevalpatel2106.utils.Utils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Parameterized::class)
class ConvertToTwoDecimalTest(private val input: Double,
                              private val expected: Double) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            return arrayListOf(
                    arrayOf(1, 1.0),
                    arrayOf(1.12, 1.12),
                    arrayOf(1.125, 1.13),
                    arrayOf(1.122, 1.12),
                    arrayOf(1.127, 1.13),
                    arrayOf(1.10, 1.10),

                    //Negative number
                    arrayOf(-1, -1.0),
                    arrayOf(-1.12, -1.12),
                    arrayOf(-1.125, -1.13),
                    arrayOf(-1.122, -1.12),
                    arrayOf(-1.127, -1.13),
                    arrayOf(-1.10, -1.10),

                    arrayOf(0, 0)
            )
        }
    }

    @Test
    @Throws(IOException::class)
    fun testConvertToTwoDecimal() {
        Assert.assertEquals(expected, Utils.convertToTwoDecimal(input), 0.0)
    }
}