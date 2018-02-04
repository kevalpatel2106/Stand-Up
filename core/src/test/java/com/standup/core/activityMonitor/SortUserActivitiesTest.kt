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
class SortUserActivitiesTest {

    @Test
    @Throws(IOException::class)
    fun checkSorting() {
        val activities = arrayListOf(
                DetectedActivity(DetectedActivity.IN_VEHICLE, 60),
                DetectedActivity(DetectedActivity.STILL, 12),
                DetectedActivity(DetectedActivity.ON_FOOT, 85),
                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),
                DetectedActivity(DetectedActivity.RUNNING, 88),
                DetectedActivity(DetectedActivity.WALKING, 90)      /*Highest*/
        )

        ActivityMonitorHelper.sortDescendingByConfidence(activities)

        //Assert
        Assert.assertEquals(activities[0].type, DetectedActivity.WALKING)
        Assert.assertEquals(activities[1].type, DetectedActivity.RUNNING)
        Assert.assertEquals(activities[2].type, DetectedActivity.ON_FOOT)
        Assert.assertEquals(activities[3].type, DetectedActivity.ON_BICYCLE)
        Assert.assertEquals(activities[4].type, DetectedActivity.IN_VEHICLE)
        Assert.assertEquals(activities[5].type, DetectedActivity.STILL)
    }

    @Test
    @Throws(IOException::class)
    fun checkSortingDuplicateConfidence() {
        val activities = arrayListOf(
                DetectedActivity(DetectedActivity.IN_VEHICLE, 60),
                DetectedActivity(DetectedActivity.STILL, 90),
                DetectedActivity(DetectedActivity.ON_FOOT, 85),
                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),
                DetectedActivity(DetectedActivity.RUNNING, 88),
                DetectedActivity(DetectedActivity.WALKING, 90)
        )

        ActivityMonitorHelper.sortDescendingByConfidence(activities)

        //Assert
        Assert.assertEquals(activities[0].type, DetectedActivity.STILL)
        Assert.assertEquals(activities[1].type, DetectedActivity.WALKING)
        Assert.assertEquals(activities[2].type, DetectedActivity.RUNNING)
        Assert.assertEquals(activities[3].type, DetectedActivity.ON_FOOT)
        Assert.assertEquals(activities[4].type, DetectedActivity.ON_BICYCLE)
        Assert.assertEquals(activities[5].type, DetectedActivity.IN_VEHICLE)
    }

    @Test
    @Throws(IOException::class)
    fun checkSortingTripleSameConfidence() {
        val activities = arrayListOf(
                DetectedActivity(DetectedActivity.IN_VEHICLE, 90),
                DetectedActivity(DetectedActivity.STILL, 90),
                DetectedActivity(DetectedActivity.ON_FOOT, 85),
                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),
                DetectedActivity(DetectedActivity.RUNNING, 88),
                DetectedActivity(DetectedActivity.WALKING, 90)
        )

        ActivityMonitorHelper.sortDescendingByConfidence(activities)

        //Assert
        Assert.assertEquals(activities[0].type, DetectedActivity.IN_VEHICLE)
        Assert.assertEquals(activities[1].type, DetectedActivity.STILL)
        Assert.assertEquals(activities[2].type, DetectedActivity.WALKING)
        Assert.assertEquals(activities[3].type, DetectedActivity.RUNNING)
        Assert.assertEquals(activities[4].type, DetectedActivity.ON_FOOT)
        Assert.assertEquals(activities[5].type, DetectedActivity.ON_BICYCLE)
    }
}