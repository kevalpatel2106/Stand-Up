package com.kevalpatel2106.standup.fcm

import com.kevalpatel2106.standup.UnitTestUtils
import com.kevalpatel2106.utils.UserSessionManager
import org.junit.Assert.assertFalse
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

    @Before
    fun setUp() {
        UnitTestUtils.initApp()
    }

    @Test
    @Throws(IOException::class)
    fun checkShouldProcessNotificationWithNullRemoteData() {
        assertFalse(FcmMessagingService().shouldProcessNotification(null))
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

        assertFalse(FcmMessagingService().shouldProcessNotification(HashMap()))
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