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

package com.standup.app.profile.repo

import com.standup.app.constants.AppConfig
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 05-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SaveProfileResponseTest {

    @Test
    @Throws(IOException::class)
    fun checkFields() {
        val profile = SaveProfileResponse(name = "Test User",
                gender = AppConfig.GENDER_MALE,
                photo = "http://google.com",
                height = "123.45",
                weight = "67.5",
                email = "test@example.com",
                userId = 12345678,
                isVerified = true)

        assertEquals(profile.userId, 12345678)
        assertEquals(profile.photo, "http://google.com")
        assertEquals(profile.email, "test@example.com")
        assertEquals(profile.gender, AppConfig.GENDER_MALE)
        assertEquals(profile.name, "Test User")
        assertEquals(profile.height, "123.45")
        assertEquals(profile.weight, "67.5")
        assertTrue(profile.isVerified)
    }


    @Test
    @Throws(IOException::class)
    fun checkEquals() {
        val profile = SaveProfileResponse(name = "Test User",
                gender = AppConfig.GENDER_MALE,
                photo = "http://google.com",
                height = "123.45",
                weight = "67.5",
                email = "test@example.com",
                userId = 12345678,
                isVerified = true)
        val profile1 = SaveProfileResponse(name = "Test User",
                gender = AppConfig.GENDER_MALE,
                photo = "http://google.com",
                height = "123.45",
                weight = "67.5",
                email = "test@example.com",
                userId = 12345678,
                isVerified = true)
        val profile2 = SaveProfileResponse(name = "Test User",
                gender = AppConfig.GENDER_MALE,
                photo = "http://google.com",
                height = "123.45",
                weight = "67.5",
                email = "test1@example.com",
                userId = 12345678,
                isVerified = false)

        assertEquals(profile, profile1)
        assertEquals(profile, profile)
        assertNotEquals(profile, profile2)
        assertNotEquals(profile1, profile2)
        assertNotEquals(profile1, null)
    }
}