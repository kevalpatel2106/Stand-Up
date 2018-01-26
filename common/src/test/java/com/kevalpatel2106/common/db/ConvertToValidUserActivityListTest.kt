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

package com.kevalpatel2106.common.db

import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.utils.TimeUtils
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
    fun checkWithInvalidStartTime() {
        val dayActivity = ArrayList<UserActivity>()
        dayActivity.add(UserActivity(eventStartTimeMills = 0,
                eventEndTimeMills = System.currentTimeMillis(),
                type = UserActivityType.SITTING.name,
                isSynced = true))
        //Ending activity
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 60000,
                eventEndTimeMills = System.currentTimeMillis(),
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, 1)
    }

    @Test
    fun checkWithNotEndingActivityInTheMiddle() {
        val dayActivity = ArrayList<UserActivity>()
        //Not ending activity
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 120000,
                eventEndTimeMills = 0,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        //Ending activity
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
    fun checkWithNotEndingLastActivity() {
        val dayActivity = ArrayList<UserActivity>()
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 120000,
                eventEndTimeMills = System.currentTimeMillis() - 60000,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        //Not ending activity
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 60000,
                eventEndTimeMills = 0,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, dayActivity.size - 1)

        //Check the remaining item
        Assert.assertEquals(resultArray[0].eventStartTimeMills, dayActivity[0].eventStartTimeMills)
        Assert.assertEquals(resultArray[0].eventEndTimeMills, dayActivity[0].eventEndTimeMills)
    }

    @Test
    fun checkWithMiddleActivityEndingAnotherDay() {
        val dayActivity = ArrayList<UserActivity>()
        val yesterdayMills = System.currentTimeMillis() - TimeUtils.ONE_DAY_MILLISECONDS

        dayActivity.add(UserActivity(eventStartTimeMills = yesterdayMills - 120000,
                eventEndTimeMills = (yesterdayMills + TimeUtils.ONE_DAY_MILLISECONDS),   //Ending today
                type = UserActivityType.SITTING.name,
                isSynced = true))
        dayActivity.add(UserActivity(eventStartTimeMills = yesterdayMills - 60000,
                eventEndTimeMills = yesterdayMills,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, dayActivity.size)

        //Check the first item
        Assert.assertEquals(resultArray[0].eventStartTimeMills, dayActivity[0].eventStartTimeMills)
        Assert.assertEquals(resultArray[0].eventEndTimeMills, dayActivity[0].eventEndTimeMills)

        //Check the second/last item
        Assert.assertEquals(resultArray[1].eventStartTimeMills, yesterdayMills - 60000)
        Assert.assertEquals(resultArray[1].eventEndTimeMills, yesterdayMills)
    }
}