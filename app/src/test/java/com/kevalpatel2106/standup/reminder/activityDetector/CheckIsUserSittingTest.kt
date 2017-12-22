package com.kevalpatel2106.standup.reminder.activityDetector

import com.google.android.gms.location.DetectedActivity
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.Parameterized
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Enclosed::class)
class CheckIsUserSittingTest {

    @RunWith(Parameterized::class)
    class CheckIsUserSittingParameterizeTest(private val activities: ArrayList<DetectedActivity>,
                                             private val expected: Boolean) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                return arrayListOf(
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, 100), /*Highest*/
                                DetectedActivity(DetectedActivity.STILL, 20),
                                DetectedActivity(DetectedActivity.ON_FOOT, 45),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),
                                DetectedActivity(DetectedActivity.RUNNING, 10),
                                DetectedActivity(DetectedActivity.WALKING, 50)
                        ), true),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, 60),
                                DetectedActivity(DetectedActivity.STILL, 96),       /*Highest*/
                                DetectedActivity(DetectedActivity.ON_FOOT, 85),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),
                                DetectedActivity(DetectedActivity.RUNNING, 10),
                                DetectedActivity(DetectedActivity.WALKING, 50)
                        ), true),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.STILL, 20),
                                DetectedActivity(DetectedActivity.RUNNING, 60),
                                DetectedActivity(DetectedActivity.IN_VEHICLE, 70),  /*Highest*/
                                DetectedActivity(DetectedActivity.ON_FOOT, 33),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),  /*Highest*/
                                DetectedActivity(DetectedActivity.WALKING, 50)
                        ), true),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.RUNNING, 60),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),  /*Highest*/
                                DetectedActivity(DetectedActivity.ON_FOOT, 33),
                                DetectedActivity(DetectedActivity.STILL, 20),
                                DetectedActivity(DetectedActivity.IN_VEHICLE, 70),  /*Highest*/
                                DetectedActivity(DetectedActivity.WALKING, 50)
                        ), true),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, 60),
                                DetectedActivity(DetectedActivity.STILL, 20),
                                DetectedActivity(DetectedActivity.ON_FOOT, 100),    /*Highest*/
                                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),
                                DetectedActivity(DetectedActivity.RUNNING, 10),
                                DetectedActivity(DetectedActivity.WALKING, 50)
                        ), false),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, 60),
                                DetectedActivity(DetectedActivity.STILL, 12),
                                DetectedActivity(DetectedActivity.ON_FOOT, 55),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),  /*Highest*/
                                DetectedActivity(DetectedActivity.RUNNING, 88),
                                DetectedActivity(DetectedActivity.WALKING, 50)
                        ), false),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, 60),
                                DetectedActivity(DetectedActivity.STILL, 12),
                                DetectedActivity(DetectedActivity.ON_FOOT, 85),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),
                                DetectedActivity(DetectedActivity.RUNNING, 88),     /*Highest*/
                                DetectedActivity(DetectedActivity.WALKING, 50)
                        ), false),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, 60),
                                DetectedActivity(DetectedActivity.STILL, 12),
                                DetectedActivity(DetectedActivity.ON_FOOT, 85),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, 70),
                                DetectedActivity(DetectedActivity.RUNNING, 88),
                                DetectedActivity(DetectedActivity.WALKING, 90)      /*Highest*/
                        ), false)
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun checkIsUserSitting() {
            Assert.assertEquals(expected, ActivityDetectionReceiver.isUserSitting(activities))
        }
    }

    @RunWith(JUnit4::class)
    class CheckIsUserSittingNotParameterizeTest {

        @Test
        @Throws(IOException::class)
        fun checkForEmptyArray() {
            try {
                ActivityDetectionReceiver.isUserSitting(ArrayList())
            } catch (e: IllegalStateException) {
                //Test passed
            }
        }
    }

}