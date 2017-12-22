package com.kevalpatel2106.standup.userActivity

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class UserActivityTest {

    @Test
    fun checkActivityType() {
        val currentTime = System.currentTimeMillis()

        //Check for sitting
        val userActivity = UserActivity(
                eventEndTimeMills = currentTime,
                eventStartTimeMills = currentTime,
                type = UserActivityType.SITTING.toString().toLowerCase(),
                isSynced = false,
                remoteId = currentTime
        )

        Assert.assertEquals(userActivity.eventEndTimeMills, currentTime)
        Assert.assertEquals(userActivity.eventStartTimeMills, currentTime)
        Assert.assertEquals(userActivity.localId, 0)
        Assert.assertEquals(userActivity.remoteId, currentTime)
        Assert.assertFalse(userActivity.isSynced)
        Assert.assertEquals(userActivity.userActivityType, UserActivityType.SITTING)
        Assert.assertEquals(userActivity.type, UserActivityType.SITTING.toString().toLowerCase())
    }

    @Test
    fun checkActivityTypeEndingBeforeStartTime() {
        val currentTime = System.currentTimeMillis()

        try {
            //Check for sitting
            UserActivity(
                    eventEndTimeMills = currentTime - 1000,
                    eventStartTimeMills = currentTime,
                    type = UserActivityType.SITTING.toString().toLowerCase(),
                    isSynced = false,
                    remoteId = currentTime
            )

            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

}