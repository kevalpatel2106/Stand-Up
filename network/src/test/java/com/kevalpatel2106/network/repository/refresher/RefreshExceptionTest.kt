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

package com.kevalpatel2106.network.repository.refresher

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 15-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class RefreshExceptionTest {
    private val TEST_ERROR_MESSAGE = "test_error_message"

    @Test
    @Throws(Exception::class)
    fun checkWithNegativeErrorCode() {
        val exception = RefreshException(TEST_ERROR_MESSAGE, -1)

        assertEquals(TEST_ERROR_MESSAGE, exception.message)
        assertEquals(-1, exception.errorCode)
    }

    @Test
    @Throws(Exception::class)
    fun checkWithPositiveErrorCode() {
        val exception = RefreshException(TEST_ERROR_MESSAGE, 0)

        assertEquals(TEST_ERROR_MESSAGE, exception.message)
        assertEquals(0, exception.errorCode)
    }

    @Test
    @Throws(Exception::class)
    fun checkWithNullErrorMessage() {
        val exception = RefreshException(null, 0)

        assertNull(exception.message)
        assertEquals(0, exception.errorCode)
    }
}
