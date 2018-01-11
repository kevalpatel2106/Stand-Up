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

package com.kevalpatel2106.standup.fcm

import com.kevalpatel2106.utils.UserSessionManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class FcmMessagingServiceTest {

    private lateinit var userSessionManager: UserSessionManager

    @Before
    fun setUp() {
        userSessionManager = Mockito.mock(UserSessionManager::class.java)
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldProcessNotificationWithNullRemoteData() {
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)
        assertFalse(FcmMessagingService().shouldProcessNotification(null, userSessionManager))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldProcessNotificationWithEmptyRemoteData() {
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)
        assertFalse(FcmMessagingService().shouldProcessNotification(HashMap(), userSessionManager))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldProcessNotificationIfUserNotLoggedIn() {
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(false)
        assertFalse(FcmMessagingService().shouldProcessNotification(HashMap(), userSessionManager))
    }


    @Test
    @Throws(IOException::class)
    fun checkShouldProcessNotificationWithoutType() {
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)

        val hashMap = HashMap<String, String>()
        hashMap.put("xyz", "abc")
        assertFalse(FcmMessagingService().shouldProcessNotification(hashMap, userSessionManager))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldProcessNotificationAllGood() {
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)

        val data = HashMap<String, String>()
        data.put("type", "xyz")
        assertTrue(FcmMessagingService().shouldProcessNotification(data, userSessionManager))
    }
}
