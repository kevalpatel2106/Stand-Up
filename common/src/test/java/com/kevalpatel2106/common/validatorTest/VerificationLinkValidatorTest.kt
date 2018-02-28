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

package com.kevalpatel2106.common.validatorTest

import com.kevalpatel2106.common.Validator
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Created by Kevalpatel2106 on 04-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Parameterized::class)
class VerificationLinkValidatorTest(private val input: String, private val expected: Boolean) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            return arrayListOf(
                    arrayOf("", false),
                    arrayOf("http://google.com", false),
                    arrayOf("http://www.google.com", false),
                    arrayOf("http://www.google.com/path1", false),
                    arrayOf("http://www.google.com/path1/path2", false),
                    arrayOf("http://www.google.com/path1/path2/path3", true),
                    arrayOf("http://www.google.com/path1/path2/path3?param=test", true),
                    arrayOf("http://www.google.com/path1/path2/path3/path4", false)
            )
        }
    }

    @Test
    fun testEmailVerificationLink() {
        assertEquals(expected, Validator.isValidVerificationLink(input))
    }
}
