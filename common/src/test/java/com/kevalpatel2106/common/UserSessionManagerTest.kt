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

package com.kevalpatel2106.common

import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.utils.SharedPrefsProvider
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

    private lateinit var userSessionManager: UserSessionManager

    @Before
    fun setUp() {
        userSessionManager = UserSessionManager(SharedPrefsProvider(MockSharedPreference()))

        userSessionManager.setNewSession(
                testUserId,
                testDisplayName,
                testEmail,
                testToken,
                testPhotoUrl,
                true)
    }

    @Test
    fun checkSetNewSessionWithToken() {
        Assert.assertEquals(userSessionManager.userId, testUserId)
        Assert.assertEquals(userSessionManager.displayName, testDisplayName)
        Assert.assertEquals(userSessionManager.email, testEmail)
        Assert.assertEquals(userSessionManager.token, testToken)
        Assert.assertTrue(userSessionManager.isUserVerified)
    }

    @Test
    fun checkSetNewSessionWithoutToken() {
        userSessionManager.setNewSession(
                testUserId,
                testDisplayName,
                testEmail,
                null,
                testPhotoUrl,
                false)

        Assert.assertEquals(userSessionManager.userId, testUserId)
        Assert.assertEquals(userSessionManager.displayName, testDisplayName)
        Assert.assertEquals(userSessionManager.email, testEmail)
        Assert.assertNull(userSessionManager.token)
        Assert.assertFalse(userSessionManager.isUserVerified)
    }

    @Test
    fun checkUpdateSession() {
        userSessionManager.updateSession(
                testDisplayName.plus("1"),
                testPhotoUrl.plus("1"),
                testHeight,
                testWeight,
                false
        )

        Assert.assertEquals(userSessionManager.userId, testUserId)
        Assert.assertEquals(userSessionManager.email, testEmail)
        Assert.assertEquals(userSessionManager.token, testToken)
        Assert.assertTrue(userSessionManager.isUserVerified)
        Assert.assertEquals(userSessionManager.displayName, testDisplayName.plus("1"))
        Assert.assertEquals(userSessionManager.photo, testPhotoUrl.plus("1"))
        Assert.assertEquals(userSessionManager.height, testHeight)
        Assert.assertEquals(userSessionManager.weight, testWeight)
        Assert.assertFalse(userSessionManager.isMale)
    }

    @Test
    fun checkUpdateSessionWithMinParams() {
        userSessionManager.updateSession(testDisplayName.plus("1"))

        Assert.assertEquals(userSessionManager.userId, testUserId)
        Assert.assertEquals(userSessionManager.email, testEmail)
        Assert.assertEquals(userSessionManager.token, testToken)
        Assert.assertTrue(userSessionManager.isUserVerified)
        Assert.assertEquals(userSessionManager.displayName, testDisplayName.plus("1"))
        Assert.assertNull(userSessionManager.photo)
        Assert.assertEquals(userSessionManager.height, 0F)
        Assert.assertEquals(userSessionManager.weight, 0F)
        Assert.assertTrue(userSessionManager.isMale)
    }

    @Test
    fun checkClearSession() {
        userSessionManager.clearUserSession()

        Assert.assertEquals(userSessionManager.userId, -1L)
        Assert.assertNull(userSessionManager.email)
        Assert.assertNull(userSessionManager.token)
        Assert.assertFalse(userSessionManager.isUserVerified)
        Assert.assertNull(userSessionManager.displayName)
        Assert.assertNull(userSessionManager.photo)
        Assert.assertEquals(userSessionManager.height, 0F)
        Assert.assertEquals(userSessionManager.weight, 0F)
        Assert.assertTrue(userSessionManager.isMale)
    }

    @Test
    fun checkClearToken() {
        userSessionManager.clearToken()

        Assert.assertEquals(userSessionManager.userId, testUserId)
        Assert.assertEquals(userSessionManager.displayName, testDisplayName)
        Assert.assertEquals(userSessionManager.email, testEmail)
        Assert.assertNull(userSessionManager.token)
        Assert.assertTrue(userSessionManager.isUserVerified)
    }

    @Test
    fun checkIsItMeWithOwnId() {
        Assert.assertTrue(userSessionManager.isItMe(testUserId))
    }

    @Test
    fun checkIsItMeWithOtherId() {
        Assert.assertFalse(userSessionManager.isItMe(9876543L))
    }

    @Test
    fun checkEmail() {
        userSessionManager.email = testEmail
        Assert.assertEquals(userSessionManager.email, testEmail)
    }

    @Test
    fun checkToken() {
        userSessionManager.token = testToken
        Assert.assertEquals(userSessionManager.token, testToken)
    }
}
