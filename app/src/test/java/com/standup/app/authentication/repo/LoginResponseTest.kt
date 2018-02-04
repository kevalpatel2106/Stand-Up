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

package com.standup.app.authentication.repo

import org.junit.Assert
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
class LoginResponseTest {


    @Test
    @Throws(IOException::class)
    fun checkInitWithDefaultParams() {
        val loginResponse = LoginResponse(uid = 0, name = "Test User", email = "test@example.com")

        Assert.assertEquals(loginResponse.uid, 0)
        Assert.assertEquals(loginResponse.name, "Test User")
        Assert.assertEquals(loginResponse.email, "test@example.com")
        Assert.assertFalse(loginResponse.isVerified)
        Assert.assertNull(loginResponse.photoUrl)
    }

    @Test
    @Throws(IOException::class)
    fun checkInitWithParams() {
        val loginResponse = LoginResponse(uid = 0,
                isVerified = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")

        Assert.assertEquals(loginResponse.uid, 0)
        Assert.assertEquals(loginResponse.name, "Test User")
        Assert.assertEquals(loginResponse.email, "test@example.com")
        Assert.assertEquals(loginResponse.photoUrl, "http://google.com")
        Assert.assertTrue(loginResponse.isVerified)
    }
}