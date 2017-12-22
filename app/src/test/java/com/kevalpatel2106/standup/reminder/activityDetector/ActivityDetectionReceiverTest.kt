package com.kevalpatel2106.standup.reminder.activityDetector

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
class ActivityDetectionReceiverTest {

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

        ActivityDetectionReceiver.sortDescendingByConfidence(activities)

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

        ActivityDetectionReceiver.sortDescendingByConfidence(activities)

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

        ActivityDetectionReceiver.sortDescendingByConfidence(activities)

        //Assert
        Assert.assertEquals(activities[0].type, DetectedActivity.IN_VEHICLE)
        Assert.assertEquals(activities[1].type, DetectedActivity.STILL)
        Assert.assertEquals(activities[2].type, DetectedActivity.WALKING)
        Assert.assertEquals(activities[3].type, DetectedActivity.RUNNING)
        Assert.assertEquals(activities[4].type, DetectedActivity.ON_FOOT)
        Assert.assertEquals(activities[5].type, DetectedActivity.ON_BICYCLE)
    }

}