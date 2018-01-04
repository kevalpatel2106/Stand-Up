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

package com.kevalpatel2106.utils

import com.kevalpatel2106.testutils.MockSharedPreference
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Keval on 04/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class UserSessionManagerTest {

    private val testUserId = 1234567L
    private val testDisplayName = "Test user"
    private val testEmail = "test@example.com"
    private val testToken = "test-token-3487f-5fjdbd"
    private val testPhotoUrl = "http://exmaple.com"
    private val testHeight = 123F
    private val testWeight = 222F

    @Before
    fun setUp() {
        SharedPrefsProvider.init(MockSharedPreference())

        UserSessionManager.setNewSession(
                testUserId,
                testDisplayName,
                testEmail,
                testToken,
                testPhotoUrl,
                true)
    }

    @Test
    fun checkSetNewSessionWithToken() {
        Assert.assertEquals(UserSessionManager.userId, testUserId)
        Assert.assertEquals(UserSessionManager.displayName, testDisplayName)
        Assert.assertEquals(UserSessionManager.email, testEmail)
        Assert.assertEquals(UserSessionManager.token, testToken)
        Assert.assertTrue(UserSessionManager.isUserVerified)
    }

    @Test
    fun checkSetNewSessionWithoutToken() {
        UserSessionManager.setNewSession(
                testUserId,
                testDisplayName,
                testEmail,
                null,
                testPhotoUrl,
                false)

        Assert.assertEquals(UserSessionManager.userId, testUserId)
        Assert.assertEquals(UserSessionManager.displayName, testDisplayName)
        Assert.assertEquals(UserSessionManager.email, testEmail)
        Assert.assertNull(UserSessionManager.token)
        Assert.assertFalse(UserSessionManager.isUserVerified)
    }

    @Test
    fun checkUpdateSession() {
        UserSessionManager.updateSession(
                testDisplayName.plus("1"),
                testPhotoUrl.plus("1"),
                testHeight,
                testWeight,
                false
        )

        Assert.assertEquals(UserSessionManager.userId, testUserId)
        Assert.assertEquals(UserSessionManager.email, testEmail)
        Assert.assertEquals(UserSessionManager.token, testToken)
        Assert.assertTrue(UserSessionManager.isUserVerified)
        Assert.assertEquals(UserSessionManager.displayName, testDisplayName.plus("1"))
        Assert.assertEquals(UserSessionManager.photo, testPhotoUrl.plus("1"))
        Assert.assertEquals(UserSessionManager.height, testHeight)
        Assert.assertEquals(UserSessionManager.weight, testWeight)
        Assert.assertFalse(UserSessionManager.isMale)
    }

    @Test
    fun checkUpdateSessionWithMinParams() {
        UserSessionManager.updateSession(testDisplayName.plus("1"))

        Assert.assertEquals(UserSessionManager.userId, testUserId)
        Assert.assertEquals(UserSessionManager.email, testEmail)
        Assert.assertEquals(UserSessionManager.token, testToken)
        Assert.assertTrue(UserSessionManager.isUserVerified)
        Assert.assertEquals(UserSessionManager.displayName, testDisplayName.plus("1"))
        Assert.assertNull(UserSessionManager.photo)
        Assert.assertEquals(UserSessionManager.height, 0F)
        Assert.assertEquals(UserSessionManager.weight, 0F)
        Assert.assertTrue(UserSessionManager.isMale)
    }

    @Test
    fun checkClearSession() {
        UserSessionManager.clearUserSession()

        Assert.assertEquals(UserSessionManager.userId, -1L)
        Assert.assertNull(UserSessionManager.email)
        Assert.assertNull(UserSessionManager.token)
        Assert.assertFalse(UserSessionManager.isUserVerified)
        Assert.assertNull(UserSessionManager.displayName)
        Assert.assertNull(UserSessionManager.photo)
        Assert.assertEquals(UserSessionManager.height, 0F)
        Assert.assertEquals(UserSessionManager.weight, 0F)
        Assert.assertTrue(UserSessionManager.isMale)
    }

    @Test
    fun checkClearToken() {
        UserSessionManager.clearToken()

        Assert.assertEquals(UserSessionManager.userId, testUserId)
        Assert.assertEquals(UserSessionManager.displayName, testDisplayName)
        Assert.assertEquals(UserSessionManager.email, testEmail)
        Assert.assertNull(UserSessionManager.token)
        Assert.assertTrue(UserSessionManager.isUserVerified)
    }

    @Test
    fun checkIsItMeWithOwnId() {
        Assert.assertTrue(UserSessionManager.isItMe(testUserId))
    }

    @Test
    fun checkIsItMeWithOtherId() {
        Assert.assertFalse(UserSessionManager.isItMe(9876543L))
    }

    @Test
    fun checkEmail() {
        UserSessionManager.email = testEmail
        Assert.assertEquals(UserSessionManager.email, testEmail)
    }

    @Test
    fun checkToken() {
        UserSessionManager.token = testToken
        Assert.assertEquals(UserSessionManager.token, testToken)
    }
}