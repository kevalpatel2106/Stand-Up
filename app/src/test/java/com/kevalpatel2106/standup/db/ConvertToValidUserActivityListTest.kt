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

package com.kevalpatel2106.standup.db

import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Keval on 28/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class ConvertToValidUserActivityListTest {

    @Test
    fun checkConvertToValidUserActivityListWithInValidStartTime() {
        val dayActivity = ArrayList<UserActivity>()
        dayActivity.add(UserActivity(eventStartTimeMills = 0,
                eventEndTimeMills = System.currentTimeMillis(),
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, 0)
    }

    @Test
    fun checkConvertToValidUserActivityListWithNotEndingMiddleEvent() {
        val dayActivity = ArrayList<UserActivity>()
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 120000,
                eventEndTimeMills = 0,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 60000,
                eventEndTimeMills = System.currentTimeMillis(),
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, dayActivity.size - 1)

        //Check the remaining item
        Assert.assertEquals(resultArray[0].eventStartTimeMills, dayActivity[1].eventStartTimeMills)
        Assert.assertEquals(resultArray[0].eventEndTimeMills, dayActivity[1].eventEndTimeMills)
    }

    @Test
    fun checkConvertToValidUserActivityListWithNotEndingLastEvent() {
        val dayActivity = ArrayList<UserActivity>()
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 120000,
                eventEndTimeMills = System.currentTimeMillis() - 60000,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 60000,
                eventEndTimeMills = 0,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, dayActivity.size)

        //Check the remaining item
        Assert.assertEquals(resultArray[0].eventStartTimeMills, dayActivity[0].eventStartTimeMills)
        Assert.assertEquals(resultArray[0].eventEndTimeMills, dayActivity[0].eventEndTimeMills)
        Assert.assertEquals(resultArray[1].eventStartTimeMills, dayActivity[1].eventStartTimeMills)
        Assert.assertTrue(System.currentTimeMillis() - resultArray[1].eventEndTimeMills < 5000 /* 5 seconds of delta*/)
    }

    @Test
    fun checkConversionFromDayActivityListWithAllNotEndingUserActivity() {
        val activity = ArrayList<UserActivity>()
        (1..10).mapTo(activity) {
            UserActivity(eventStartTimeMills = System.currentTimeMillis() + it * 1000,
                    eventEndTimeMills = 0,
                    type = UserActivityType.SITTING.name,
                    isSynced = true)
        }

        try {
            DailyActivitySummary.fromDayActivityList(ArrayList())
            Assert.fail()
        } catch (e: IllegalStateException) {
            //Test passed
        }
    }
}