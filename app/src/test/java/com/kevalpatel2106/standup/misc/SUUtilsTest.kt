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

package com.kevalpatel2106.standup.misc

import com.kevalpatel2106.standup.constants.AppConfig
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException
import java.util.*

/**
 * Created by Kevalpatel2106 on 27-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SUUtilsTest {

    @Test
    @Throws(IOException::class)
    fun checkCreateTimeLineItemFromSittingUserActivity() {
        val today12AmCal = Calendar.getInstance()
        today12AmCal.set(Calendar.HOUR_OF_DAY, 0)
        today12AmCal.set(Calendar.MINUTE, 0)
        today12AmCal.set(Calendar.SECOND, 0)
        today12AmCal.set(Calendar.MILLISECOND, 0)
        today12AmCal.add(Calendar.HOUR, 2)

        val userActivity = UserActivity(eventStartTimeMills = today12AmCal.timeInMillis,
                eventEndTimeMills = today12AmCal.timeInMillis + 3600.times(1000),
                type = UserActivityType.SITTING.name.toLowerCase(),
                isSynced = true)

        val timelineItem = SUUtils.createTimeLineItemFromUserActivity(userActivity)
        assertEquals(timelineItem.startTime, 2.times(3600000))
        assertEquals(timelineItem.endTime, 3.times(3600000))
        assertEquals(timelineItem.color, AppConfig.COLOR_SITTING)
    }

    @Test
    @Throws(IOException::class)
    fun checkCreateTimeLineItemFromStandingUserActivity() {
        val today12AmCal = Calendar.getInstance()
        today12AmCal.set(Calendar.HOUR_OF_DAY, 0)
        today12AmCal.set(Calendar.MINUTE, 0)
        today12AmCal.set(Calendar.SECOND, 0)
        today12AmCal.set(Calendar.MILLISECOND, 0)
        today12AmCal.add(Calendar.HOUR, 2)

        val userActivity = UserActivity(eventStartTimeMills = today12AmCal.timeInMillis,
                eventEndTimeMills = today12AmCal.timeInMillis + 3600.times(1000),
                type = UserActivityType.MOVING.name.toLowerCase(),
                isSynced = true)

        val timelineItem = SUUtils.createTimeLineItemFromUserActivity(userActivity)
        assertEquals(timelineItem.startTime, 2.times(3600000))
        assertEquals(timelineItem.endTime, 3.times(3600000))
        assertEquals(timelineItem.color, AppConfig.COLOR_STANDING)
    }

    @Test
    @Throws(IOException::class)
    fun checkCreateTimeLineItemFromNotTrackedUserActivity() {
        val today12AmCal = Calendar.getInstance()
        today12AmCal.set(Calendar.HOUR_OF_DAY, 0)
        today12AmCal.set(Calendar.MINUTE, 0)
        today12AmCal.set(Calendar.SECOND, 0)
        today12AmCal.set(Calendar.MILLISECOND, 0)
        today12AmCal.add(Calendar.HOUR, 2)

        val userActivity = UserActivity(eventStartTimeMills = today12AmCal.timeInMillis,
                eventEndTimeMills = today12AmCal.timeInMillis + 3600.times(1000),
                type = UserActivityType.NOT_TRACKED.name.toLowerCase(),
                isSynced = true)

        val timelineItem = SUUtils.createTimeLineItemFromUserActivity(userActivity)
        assertEquals(timelineItem.startTime, 2.times(3600000))
        assertEquals(timelineItem.endTime, 3.times(3600000))
        assertEquals(timelineItem.color, AppConfig.COLOR_NOT_TRACKED)
    }
}