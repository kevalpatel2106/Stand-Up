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

    @Test
    @Throws(IOException::class)
    fun checkShouldProcessNotificationWithNullRemoteData() {
        assertFalse(FcmMessagingService().shouldProcessNotification(null))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldProcessNotificationWithEmptyRemoteData() {
        assertFalse(FcmMessagingService().shouldProcessNotification(HashMap()))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldProcessNotificationIfUserNotLoggedIn() {
        val userSessionManager = Mockito.mock(UserSessionManager::class.java)
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(false)

        assertFalse(FcmMessagingService().shouldProcessNotification(HashMap()))
    }


    @Test
    @Throws(IOException::class)
    fun checkShouldProcessNotificationWithoutType() {
        val userSessionManager = Mockito.mock(UserSessionManager::class.java)
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)

        val hashMap = HashMap<String, String>()
        hashMap.put("xyz", "abc")
        assertFalse(FcmMessagingService().shouldProcessNotification(hashMap))
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldProcessNotificationAllGood() {
        val userSessionManager = Mockito.mock(UserSessionManager::class.java)
        Mockito.`when`(userSessionManager.isUserLoggedIn).thenReturn(true)

        val data = HashMap<String, String>()
        data.put("type", "xyz")
        assertFalse(FcmMessagingService().shouldProcessNotification(data))
    }
}
