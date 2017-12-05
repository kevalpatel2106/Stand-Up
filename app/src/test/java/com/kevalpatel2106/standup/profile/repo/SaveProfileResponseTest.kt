package com.kevalpatel2106.standup.profile.repo

import com.kevalpatel2106.standup.constants.AppConfig
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Kevalpatel2106 on 05-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class SaveProfileResponseTest{

    @Test
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
        assertEquals(profile, profile1)
    }
}