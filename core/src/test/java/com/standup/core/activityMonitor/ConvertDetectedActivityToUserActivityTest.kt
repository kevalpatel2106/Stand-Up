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

package com.standup.core.activityMonitor

import com.google.android.gms.location.DetectedActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityHelper
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ConvertDetectedActivityToUserActivityTest {

    @Test
    @Throws(IOException::class)
    fun checkConvertToUserActivityWhenUserIsSitting() {
        val activities = arrayListOf(
                DetectedActivity(DetectedActivity.IN_VEHICLE, 90),
                DetectedActivity(DetectedActivity.STILL, 90),
                DetectedActivity(DetectedActivity.ON_FOOT, 85),
                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),
                DetectedActivity(DetectedActivity.RUNNING, 88),
                DetectedActivity(DetectedActivity.WALKING, 14)
        )

        val userActivity = ActivityMonitorHelper.convertToUserActivity(activities)

        Assert.assertNotNull(userActivity)
        Assert.assertEquals(userActivity!!.userActivityType, UserActivityType.SITTING)
        Assert.assertEquals(userActivity.eventEndTimeMills,
                userActivity.eventStartTimeMills + UserActivityHelper.endTimeCorrectionValue)
        Assert.assertEquals(userActivity.isSynced, false)
        Assert.assertEquals(userActivity.remoteId, 0)
        Assert.assertEquals(userActivity.type, UserActivityType.SITTING.name.toLowerCase())
        Assert.assertTrue(System.currentTimeMillis() - userActivity.eventStartTimeMills < 5000)

    }

    @Test
    @Throws(IOException::class)
    fun checkConvertToUserActivityWhenUserIsStanding() {
        val activities = arrayListOf(
                DetectedActivity(DetectedActivity.IN_VEHICLE, 10),
                DetectedActivity(DetectedActivity.STILL, 56),
                DetectedActivity(DetectedActivity.ON_FOOT, 85),
                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),
                DetectedActivity(DetectedActivity.RUNNING, 88),
                DetectedActivity(DetectedActivity.WALKING, 14)
        )

        val userActivity = ActivityMonitorHelper.convertToUserActivity(activities)

        Assert.assertNotNull(userActivity)
        Assert.assertEquals(userActivity!!.userActivityType, UserActivityType.MOVING)
        Assert.assertEquals(userActivity.eventEndTimeMills,
                userActivity.eventStartTimeMills + UserActivityHelper.endTimeCorrectionValue)
        Assert.assertEquals(userActivity.isSynced, false)
        Assert.assertEquals(userActivity.remoteId, 0)
        Assert.assertEquals(userActivity.type, UserActivityType.MOVING.name.toLowerCase())
        Assert.assertTrue(System.currentTimeMillis() - userActivity.eventStartTimeMills < 5000)
    }

    @Test
    @Throws(IOException::class)
    fun checkConvertToUserActivityWhenNotEnoughConfidence() {
        val activities = arrayListOf(
                DetectedActivity(DetectedActivity.IN_VEHICLE, 10),
                DetectedActivity(DetectedActivity.STILL, 29),
                DetectedActivity(DetectedActivity.ON_FOOT, 25),
                DetectedActivity(DetectedActivity.ON_BICYCLE, 21),
                DetectedActivity(DetectedActivity.RUNNING, 8),
                DetectedActivity(DetectedActivity.WALKING, 14)
        )

        val userActivity = ActivityMonitorHelper.convertToUserActivity(activities)

        Assert.assertNull(userActivity)
    }

}
