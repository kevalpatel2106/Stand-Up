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

package com.kevalpatel2106.standup.core.activityMonitor

import com.google.android.gms.location.DetectedActivity
import com.kevalpatel2106.standup.core.CoreConfig
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
class ShouldIgnoreEventTest {

    @RunWith(Parameterized::class)
    class CheckShouldIgnoreEventParameterize(private val activities: ArrayList<DetectedActivity>,
                                             private val expected: Boolean) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                return arrayListOf(
                        // Every activities has same threshold confidence
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, CoreConfig.CONFIDENCE_THRESHOLD),
                                DetectedActivity(DetectedActivity.STILL, CoreConfig.CONFIDENCE_THRESHOLD),
                                DetectedActivity(DetectedActivity.ON_FOOT, CoreConfig.CONFIDENCE_THRESHOLD),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, CoreConfig.CONFIDENCE_THRESHOLD),
                                DetectedActivity(DetectedActivity.RUNNING, CoreConfig.CONFIDENCE_THRESHOLD),
                                DetectedActivity(DetectedActivity.WALKING, CoreConfig.CONFIDENCE_THRESHOLD)
                        ), false),

                        // STILL activity with highest confidence
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, CoreConfig.CONFIDENCE_THRESHOLD - 18),
                                DetectedActivity(DetectedActivity.STILL, CoreConfig.CONFIDENCE_THRESHOLD + 19),       /*Highest*/
                                DetectedActivity(DetectedActivity.ON_FOOT, CoreConfig.CONFIDENCE_THRESHOLD - 39)
                        ), false),

                        // IN_VEHICLE activity with highest confidence
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, CoreConfig.CONFIDENCE_THRESHOLD + 20),  /*Highest*/
                                DetectedActivity(DetectedActivity.ON_FOOT, CoreConfig.CONFIDENCE_THRESHOLD - 17),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, CoreConfig.CONFIDENCE_THRESHOLD - 45),
                                DetectedActivity(DetectedActivity.WALKING, CoreConfig.CONFIDENCE_THRESHOLD + 15)
                        ), false),

                        // ON_FOOT  activity with highest confidence
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.STILL, CoreConfig.CONFIDENCE_THRESHOLD - 5),
                                DetectedActivity(DetectedActivity.ON_FOOT, CoreConfig.CONFIDENCE_THRESHOLD + 45),    /*Highest*/
                                DetectedActivity(DetectedActivity.ON_BICYCLE, CoreConfig.CONFIDENCE_THRESHOLD + 40),
                                DetectedActivity(DetectedActivity.RUNNING, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.WALKING, CoreConfig.CONFIDENCE_THRESHOLD - 10)
                        ), false),

                        // WALKING activity with highest confidence
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.STILL, CoreConfig.CONFIDENCE_THRESHOLD + 40),
                                DetectedActivity(DetectedActivity.WALKING, CoreConfig.CONFIDENCE_THRESHOLD + 45),    /*Highest*/
                                DetectedActivity(DetectedActivity.ON_BICYCLE, CoreConfig.CONFIDENCE_THRESHOLD + 40),
                                DetectedActivity(DetectedActivity.RUNNING, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.ON_FOOT, CoreConfig.CONFIDENCE_THRESHOLD - 10)
                        ), false),

                        // WALKING activity with highest confidence
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.STILL, CoreConfig.CONFIDENCE_THRESHOLD + 40),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, CoreConfig.CONFIDENCE_THRESHOLD + 45),    /*Highest*/
                                DetectedActivity(DetectedActivity.WALKING, CoreConfig.CONFIDENCE_THRESHOLD + 40),
                                DetectedActivity(DetectedActivity.RUNNING, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.ON_FOOT, CoreConfig.CONFIDENCE_THRESHOLD - 10)
                        ), false),

                        // Unknown activity with highest confidence
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.STILL, CoreConfig.CONFIDENCE_THRESHOLD - 5),
                                DetectedActivity(DetectedActivity.UNKNOWN, CoreConfig.CONFIDENCE_THRESHOLD + 45),    /*Highest*/
                                DetectedActivity(DetectedActivity.ON_BICYCLE, CoreConfig.CONFIDENCE_THRESHOLD - 40),
                                DetectedActivity(DetectedActivity.RUNNING, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.WALKING, CoreConfig.CONFIDENCE_THRESHOLD - 10)
                        ), true),

                        // Tilting activity with highest confidence
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.STILL, CoreConfig.CONFIDENCE_THRESHOLD - 5),
                                DetectedActivity(DetectedActivity.TILTING, CoreConfig.CONFIDENCE_THRESHOLD + 45),    /*Highest*/
                                DetectedActivity(DetectedActivity.ON_BICYCLE, CoreConfig.CONFIDENCE_THRESHOLD - 40),
                                DetectedActivity(DetectedActivity.RUNNING, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.WALKING, CoreConfig.CONFIDENCE_THRESHOLD - 10)
                        ), true),

                        //All of below threshold
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.STILL, CoreConfig.CONFIDENCE_THRESHOLD - 5),
                                DetectedActivity(DetectedActivity.ON_FOOT, CoreConfig.CONFIDENCE_THRESHOLD - 45),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, CoreConfig.CONFIDENCE_THRESHOLD - 40),
                                DetectedActivity(DetectedActivity.RUNNING, CoreConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.WALKING, CoreConfig.CONFIDENCE_THRESHOLD - 10)
                        ), true)
                )
            }
        }


        @Test
        @Throws(IOException::class)
        fun checkShouldIgnoreEvent() {
            Assert.assertEquals(ActivityMonitorHelper.shouldIgnoreThisEvent(activities), expected)
        }
    }

    @RunWith(JUnit4::class)
    class CheckShouldIgnoreEventNotParameterize {

        @Test
        @Throws(IOException::class)
        fun checkForEmptyArray() {
            try {
                ActivityMonitorHelper.shouldIgnoreThisEvent(ArrayList())
            } catch (e: IllegalStateException) {
                //Test passed
            }
        }
    }
}
