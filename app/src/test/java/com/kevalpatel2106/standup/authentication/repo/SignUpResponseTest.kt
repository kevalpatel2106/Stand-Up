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

package com.kevalpatel2106.standup.authentication.repo

import org.junit.Assert
import org.junit.Assert.*
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
class SignUpResponseTest {

    @Test
    @Throws(IOException::class)
    fun checkInitWithDefaultParams() {
        val signUpResponseData = SignUpResponse(uid = 0, name = "Test User", email = "test@example.com")

        assertEquals(signUpResponseData.uid, 0)
        assertEquals(signUpResponseData.name, "Test User")
        assertEquals(signUpResponseData.email, "test@example.com")
        assertFalse(signUpResponseData.isNewUser)
        assertFalse(signUpResponseData.isVerified)
        assertNull(signUpResponseData.photoUrl)
    }

    @Test
    @Throws(IOException::class)
    fun checkInitWithParams() {
        val signUpResponseData = SignUpResponse(uid = 0,
                isVerified = true,
                isNewUser = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")

        assertEquals(signUpResponseData.uid, 0)
        assertEquals(signUpResponseData.name, "Test User")
        assertEquals(signUpResponseData.email, "test@example.com")
        assertEquals(signUpResponseData.photoUrl, "http://google.com")
        assertTrue(signUpResponseData.isNewUser)
        assertTrue(signUpResponseData.isVerified)
    }


    @Test
    @Throws(IOException::class)
    fun checkEquals() {
        val signUpResponse = SignUpResponse(uid = 0,
                isVerified = true,
                isNewUser = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")
        val signUpResponse1 = SignUpResponse(uid = 0,
                isVerified = true,
                isNewUser = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")
        val signUpResponse2 = SignUpResponse(uid = 1234,
                isVerified = true,
                isNewUser = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")

        assertEquals(signUpResponse, signUpResponse1)
        Assert.assertNotEquals(signUpResponse, signUpResponse2)
        Assert.assertNotEquals(signUpResponse1, signUpResponse2)
        Assert.assertEquals(signUpResponse, signUpResponse)
        Assert.assertNotEquals(signUpResponse, null)
        Assert.assertNotEquals(signUpResponse, Unit)
    }

    @Test
    @Throws(IOException::class)
    fun checkHashCode() {
        val signUpResponse2 = SignUpResponse(uid = 1234,
                isVerified = true,
                isNewUser = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")
        Assert.assertEquals(1234, signUpResponse2.hashCode())
    }

    @Test
    @Throws(IOException::class)
    fun checkIfEqualsHasSameHashCode() {
        val signUpResponse = SignUpResponse(uid = 0,
                isVerified = true,
                isNewUser = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")
        val signUpResponse1 = SignUpResponse(uid = 0,
                isVerified = true,
                isNewUser = true,
                photoUrl = "http://google.com",
                name = "Test User",
                email = "test@example.com")
        Assert.assertEquals(signUpResponse.hashCode(), signUpResponse1.hashCode())
    }
}