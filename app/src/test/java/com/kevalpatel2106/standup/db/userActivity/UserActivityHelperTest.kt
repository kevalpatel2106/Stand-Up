package com.kevalpatel2106.standup.db.userActivity

import org.junit.Assert
import org.junit.Test
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class UserActivityHelperTest {

    @Test
    @Throws(IOException::class)
    fun checkGetActivityTypeForSitting() {
        Assert.assertEquals(UserActivityHelper.getActivityType("sitting"), UserActivityType.SITTING)
    }

    @Test
    @Throws(IOException::class)
    fun checkGetActivityTypeForStanding() {
        Assert.assertEquals(UserActivityHelper.getActivityType("moving"), UserActivityType.MOVING)
    }

    @Test
    @Throws(IOException::class)
    fun checkGetActivityTypeForSleeping() {
        Assert.assertEquals(UserActivityHelper.getActivityType("not_tracked"), UserActivityType.NOT_TRACKED)
    }

    @Test
    @Throws(IOException::class)
    fun checkGetActivityTypeForInvalidString() {
        Assert.assertEquals(UserActivityHelper.getActivityType("xyz"), UserActivityType.NOT_TRACKED)
    }

    @Test
    @Throws(IOException::class)
    fun checkCreateLocalSittingUserActivity() {
        val localUserActivity = UserActivityHelper.createLocalUserActivity(UserActivityType.SITTING)

        Assert.assertTrue(System.currentTimeMillis() - localUserActivity.eventStartTimeMills <= 10000/*10 seconds delta*/)
        Assert.assertEquals(localUserActivity.eventEndTimeMills, 0)
        Assert.assertEquals(localUserActivity.localId, 0)
        Assert.assertEquals(localUserActivity.remoteId, 0)
        Assert.assertFalse(localUserActivity.isSynced)
        Assert.assertEquals(localUserActivity.userActivityType, UserActivityType.SITTING)
    }

    @Test
    @Throws(IOException::class)
    fun checkCreateLocNotTrackedUserActivity() {
        val localUserActivity = UserActivityHelper.createLocalUserActivity(UserActivityType.NOT_TRACKED)

        Assert.assertTrue(System.currentTimeMillis() - localUserActivity.eventStartTimeMills <= 10000/*10 seconds delta*/)
        Assert.assertEquals(localUserActivity.eventEndTimeMills, 0)
        Assert.assertEquals(localUserActivity.localId, 0)
        Assert.assertEquals(localUserActivity.remoteId, 0)
        Assert.assertFalse(localUserActivity.isSynced)
        Assert.assertEquals(localUserActivity.userActivityType, UserActivityType.NOT_TRACKED)
    }

    @Test
    @Throws(IOException::class)
    fun checkCreateLocalMovingUserActivity() {
        val localUserActivity = UserActivityHelper.createLocalUserActivity(UserActivityType.MOVING)

        Assert.assertTrue(System.currentTimeMillis() - localUserActivity.eventStartTimeMills <= 10000/*10 seconds delta*/)
        Assert.assertEquals(localUserActivity.eventEndTimeMills, 0)
        Assert.assertEquals(localUserActivity.localId, 0)
        Assert.assertEquals(localUserActivity.remoteId, 0)
        Assert.assertFalse(localUserActivity.isSynced)
        Assert.assertEquals(localUserActivity.userActivityType, UserActivityType.MOVING)
    }

}