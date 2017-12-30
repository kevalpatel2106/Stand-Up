/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.reminder.activityDetector

import com.google.android.gms.location.DetectedActivity
import com.kevalpatel2106.standup.reminder.ActivityDetectionHelper
import com.kevalpatel2106.standup.reminder.ReminderConfig
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
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, ReminderConfig.CONFIDENCE_THRESHOLD), /*Highest*/
                                DetectedActivity(DetectedActivity.STILL, ReminderConfig.CONFIDENCE_THRESHOLD),
                                DetectedActivity(DetectedActivity.ON_FOOT, ReminderConfig.CONFIDENCE_THRESHOLD),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, ReminderConfig.CONFIDENCE_THRESHOLD),
                                DetectedActivity(DetectedActivity.RUNNING, ReminderConfig.CONFIDENCE_THRESHOLD),
                                DetectedActivity(DetectedActivity.WALKING, ReminderConfig.CONFIDENCE_THRESHOLD)
                        ), false),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, ReminderConfig.CONFIDENCE_THRESHOLD - 18),
                                DetectedActivity(DetectedActivity.STILL, ReminderConfig.CONFIDENCE_THRESHOLD + 19),       /*Highest*/
                                DetectedActivity(DetectedActivity.ON_FOOT, ReminderConfig.CONFIDENCE_THRESHOLD - 39)
                        ), false),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, ReminderConfig.CONFIDENCE_THRESHOLD + 20),  /*Highest*/
                                DetectedActivity(DetectedActivity.ON_FOOT, ReminderConfig.CONFIDENCE_THRESHOLD - 17),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, ReminderConfig.CONFIDENCE_THRESHOLD - 45),
                                DetectedActivity(DetectedActivity.WALKING, ReminderConfig.CONFIDENCE_THRESHOLD + 15)
                        ), false),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, ReminderConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.STILL, ReminderConfig.CONFIDENCE_THRESHOLD - 5),
                                DetectedActivity(DetectedActivity.ON_FOOT, ReminderConfig.CONFIDENCE_THRESHOLD - 45),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, ReminderConfig.CONFIDENCE_THRESHOLD - 40),
                                DetectedActivity(DetectedActivity.RUNNING, ReminderConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.WALKING, ReminderConfig.CONFIDENCE_THRESHOLD - 10)
                        ), true),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, ReminderConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.STILL, ReminderConfig.CONFIDENCE_THRESHOLD - 5),
                                DetectedActivity(DetectedActivity.UNKNOWN, ReminderConfig.CONFIDENCE_THRESHOLD + 45),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, ReminderConfig.CONFIDENCE_THRESHOLD - 40),
                                DetectedActivity(DetectedActivity.RUNNING, ReminderConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.WALKING, ReminderConfig.CONFIDENCE_THRESHOLD - 10)
                        ), true),
                        arrayOf(arrayListOf(
                                DetectedActivity(DetectedActivity.IN_VEHICLE, ReminderConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.STILL, ReminderConfig.CONFIDENCE_THRESHOLD - 5),
                                DetectedActivity(DetectedActivity.TILTING, ReminderConfig.CONFIDENCE_THRESHOLD + 45),
                                DetectedActivity(DetectedActivity.ON_BICYCLE, ReminderConfig.CONFIDENCE_THRESHOLD - 40),
                                DetectedActivity(DetectedActivity.RUNNING, ReminderConfig.CONFIDENCE_THRESHOLD - 10),
                                DetectedActivity(DetectedActivity.WALKING, ReminderConfig.CONFIDENCE_THRESHOLD - 10)
                        ), true)
                )
            }
        }


        @Test
        @Throws(IOException::class)
        fun checkShouldIgnoreEvent() {
            Assert.assertEquals(ActivityDetectionHelper.shouldIgnoreThisEvent(activities), expected)
        }
    }

    @RunWith(JUnit4::class)
    class CheckShouldIgnoreEventNotParameterize {

        @Test
        @Throws(IOException::class)
        fun checkForEmptyArray() {
            try {
                ActivityDetectionHelper.shouldIgnoreThisEvent(ArrayList())
            } catch (e: IllegalStateException) {
                //Test passed
            }
        }
    }
}