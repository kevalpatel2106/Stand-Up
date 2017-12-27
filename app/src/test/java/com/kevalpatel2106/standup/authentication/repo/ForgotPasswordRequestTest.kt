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

package com.kevalpatel2106.standup.authentication.repo

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 04-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ForgotPasswordRequestTest {

    @Test
    @Throws(IOException::class)
    fun checkInit() {
        val forgotPasswordRequest = ForgotPasswordRequest(email = "test@example.com")
        assertEquals(forgotPasswordRequest.email, "test@example.com")
    }

    @Test
    @Throws(IOException::class)
    fun checkEquals() {
        val forgotPasswordRequest = ForgotPasswordRequest(email = "test@example.com")
        val forgotPasswordRequest1 = ForgotPasswordRequest(email = "test@example.com")
        val forgotPasswordRequest2 = ForgotPasswordRequest(email = "test1@example.com")

        assertEquals(forgotPasswordRequest, forgotPasswordRequest1)
        Assert.assertNotEquals(forgotPasswordRequest, forgotPasswordRequest2)
        Assert.assertNotEquals(forgotPasswordRequest1, forgotPasswordRequest2)
        Assert.assertEquals(forgotPasswordRequest, forgotPasswordRequest)
        Assert.assertNotEquals(forgotPasswordRequest, null)
    }
}