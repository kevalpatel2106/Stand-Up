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

package com.kevalpatel2106.common.userActivity

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
    fun checkWithEmptyList() {
        try {
            val emptyList = ArrayList<UserActivity>()
            DailyActivitySummary.convertToValidUserActivityList(emptyList)

            Assert.assertTrue(emptyList.isEmpty())
        } catch (e: Exception) {
            Assert.fail()
        }
    }

    @Test
    fun checkWithAllInvalidActivities() {
        val dayActivity = ArrayList<UserActivity>()
        //Not starting activity
        dayActivity.add(UserActivity(eventStartTimeMills = 0,
                eventEndTimeMills = System.currentTimeMillis() - 60_000,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        //Not ending activity
        dayActivity.add(UserActivity(eventStartTimeMills = 30_000,
                eventEndTimeMills = 0,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertTrue(resultArray.isEmpty())
    }


    @Test
    fun checkWithInvalidStartTime_AtStart() {
        val dayActivity = ArrayList<UserActivity>()
        //Not starting activity
        dayActivity.add(UserActivity(eventStartTimeMills = 0,
                eventEndTimeMills = System.currentTimeMillis() - 60000,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        //Ending activity
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 60000,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, 1)
    }


    @Test
    fun checkWithInvalidEndTime_AtStart() {
        val dayActivity = ArrayList<UserActivity>()
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 60000,
                eventEndTimeMills = 0,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        //Not ending activity
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 60000,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, 1)
    }

    @Test
    fun checkWithNotEndingActivity_InTheMiddle() {
        val dayActivity = ArrayList<UserActivity>()
        //Ending activity
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 300_000,
                eventEndTimeMills = System.currentTimeMillis() - 240_000,
                type = UserActivityType.SITTING.name,
                isSynced = true))
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
        Assert.assertEquals(resultArray[0].eventStartTimeMills, dayActivity[0].eventStartTimeMills)
        Assert.assertEquals(resultArray[0].eventEndTimeMills, dayActivity[0].eventEndTimeMills)

        Assert.assertEquals(resultArray[1].eventStartTimeMills, dayActivity[2].eventStartTimeMills)
        Assert.assertEquals(resultArray[1].eventEndTimeMills, dayActivity[2].eventEndTimeMills)
    }

    @Test
    fun checkWithNotStartingActivity_InTheMiddle() {
        val dayActivity = ArrayList<UserActivity>()
        //Ending activity
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 300_000,
                eventEndTimeMills = System.currentTimeMillis() - 240_000,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        //Not starting activity
        dayActivity.add(UserActivity(eventStartTimeMills = 0,
                eventEndTimeMills = System.currentTimeMillis() - 120_000,
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
        Assert.assertEquals(resultArray[0].eventStartTimeMills, dayActivity[0].eventStartTimeMills)
        Assert.assertEquals(resultArray[0].eventEndTimeMills, dayActivity[0].eventEndTimeMills)

        //Check the remaining item
        Assert.assertEquals(resultArray[1].eventStartTimeMills, dayActivity[2].eventStartTimeMills)
        Assert.assertEquals(resultArray[1].eventEndTimeMills, dayActivity[2].eventEndTimeMills)
    }

    @Test
    fun checkWithNotEndingActivity_Last() {
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
    fun checkWithNotStartingActivity_Last() {
        val dayActivity = ArrayList<UserActivity>()
        dayActivity.add(UserActivity(eventStartTimeMills = System.currentTimeMillis() - 120000,
                eventEndTimeMills = System.currentTimeMillis() - 60000,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        //Not starting activity
        dayActivity.add(UserActivity(eventStartTimeMills = 0,
                eventEndTimeMills = System.currentTimeMillis() - 120_000,
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
    fun checkWithListOfActivity_StretchingMoreThanADay() {
        val dayActivity = ArrayList<UserActivity>()
        val yesterdayMills = System.currentTimeMillis() - TimeUtils.ONE_DAY_MILLISECONDS

        dayActivity.add(UserActivity(eventStartTimeMills = yesterdayMills - 120_000,
                eventEndTimeMills = (yesterdayMills + TimeUtils.ONE_DAY_MILLISECONDS + 120_000 /* Buffer */),   //Ending today
                type = UserActivityType.SITTING.name,
                isSynced = true))
        dayActivity.add(UserActivity(eventStartTimeMills = yesterdayMills - 60_000,
                eventEndTimeMills = yesterdayMills,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        try {
            DailyActivitySummary.convertToValidUserActivityList(dayActivity)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkWithSingleActivity_StretchingMoreThanADay() {
        val dayActivity = ArrayList<UserActivity>()

        val nowMills = System.currentTimeMillis() - 120_000
        val yesterdayMills = nowMills - TimeUtils.ONE_DAY_MILLISECONDS

        dayActivity.add(UserActivity(eventStartTimeMills = yesterdayMills - 240_000 /* Buffer */,
                eventEndTimeMills = (nowMills + 120_000 /* Buffer */),
                type = UserActivityType.SITTING.name,
                isSynced = true))

        try {
            DailyActivitySummary.convertToValidUserActivityList(dayActivity)

            //Check the remaining item
            Assert.assertTrue(Math.abs(TimeUtils.getMidnightCal(nowMills).timeInMillis
                    - dayActivity[0].eventStartTimeMills) < 2_000 /* 2 sec*/)

        } catch (e: IllegalArgumentException) {
            Assert.fail()
        }
    }

    @Test
    fun checkWithFirstActivity_OnPreviousDay() {
        val dayActivity = ArrayList<UserActivity>()
        val nowMills = System.currentTimeMillis() - 300_000
        val yesterdayMills = nowMills - TimeUtils.ONE_DAY_MILLISECONDS

        dayActivity.add(UserActivity(eventStartTimeMills = yesterdayMills,  //Started yesterday
                eventEndTimeMills = nowMills,   //Ending today
                type = UserActivityType.SITTING.name,
                isSynced = true))
        dayActivity.add(UserActivity(eventStartTimeMills = nowMills + 60_000L,
                eventEndTimeMills = nowMills + 120_000L,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, dayActivity.size)

        //Check the remaining item
        Assert.assertTrue(Math.abs(TimeUtils.getMidnightCal(nowMills).timeInMillis
                - resultArray[0].eventStartTimeMills) < 2_000 /* 2 sec*/)
        Assert.assertEquals(resultArray[0].eventEndTimeMills, dayActivity[0].eventEndTimeMills)

        Assert.assertEquals(resultArray[1].eventStartTimeMills, dayActivity[1].eventStartTimeMills)
        Assert.assertEquals(resultArray[1].eventEndTimeMills, dayActivity[1].eventEndTimeMills)
    }

    @Test
    fun checkWith_LastActivity_OnNextDay() {
        val dayActivity = ArrayList<UserActivity>()
        val nowMills = System.currentTimeMillis() - 300_000
        val tomorrowMills = nowMills + TimeUtils.ONE_DAY_MILLISECONDS

        dayActivity.add(UserActivity(eventStartTimeMills = nowMills,
                eventEndTimeMills = nowMills + 60_000,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        dayActivity.add(UserActivity(eventStartTimeMills = nowMills + 120_000,
                eventEndTimeMills = tomorrowMills,
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
        Assert.assertTrue(Math.abs(TimeUtils.getMidnightCal(tomorrowMills).timeInMillis -
                resultArray[1].eventEndTimeMills) < 2_000 /* 2 sec*/)
    }

    @Test
    fun checkWithLastActivity_OnNextDay() {
        val dayActivity = ArrayList<UserActivity>()
        val nowMills = System.currentTimeMillis() - 300_000
        val yesterdayMills = nowMills - TimeUtils.ONE_DAY_MILLISECONDS
        val tomorrowMills = nowMills + TimeUtils.ONE_DAY_MILLISECONDS

        dayActivity.add(UserActivity(eventStartTimeMills = yesterdayMills,  //Started yesterday
                eventEndTimeMills = nowMills,   //Ending today
                type = UserActivityType.SITTING.name,
                isSynced = true))
        dayActivity.add(UserActivity(eventStartTimeMills = nowMills,
                eventEndTimeMills = nowMills + 60_000,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        dayActivity.add(UserActivity(eventStartTimeMills = nowMills + 120_000,  //Started today
                eventEndTimeMills = tomorrowMills,  //Ending next day
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, dayActivity.size)

        //Check the remaining item
        Assert.assertTrue(Math.abs(TimeUtils.getMidnightCal(nowMills).timeInMillis
                - resultArray[0].eventStartTimeMills) < 2_000 /* 2 sec*/)
        Assert.assertEquals(resultArray[0].eventEndTimeMills, dayActivity[0].eventEndTimeMills)

        Assert.assertEquals(resultArray[1].eventStartTimeMills, dayActivity[1].eventStartTimeMills)
        Assert.assertEquals(resultArray[1].eventEndTimeMills, dayActivity[1].eventEndTimeMills)

        Assert.assertEquals(resultArray[2].eventStartTimeMills, dayActivity[2].eventStartTimeMills)
        Assert.assertTrue(Math.abs(TimeUtils.getMidnightCal(tomorrowMills).timeInMillis -
                resultArray[2].eventEndTimeMills) < 2_000 /* 2 sec*/)
    }

    @Test
    fun checkWithActivity_OnSameDay() {
        val dayActivity = ArrayList<UserActivity>()
        val nowMills = System.currentTimeMillis() - 400_000

        //Second activity
        dayActivity.add(UserActivity(eventStartTimeMills = nowMills + 120_000,
                eventEndTimeMills = nowMills + 240_000,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        //First activity
        dayActivity.add(UserActivity(eventStartTimeMills = nowMills,
                eventEndTimeMills = nowMills + 60_000,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        //Third activity
        dayActivity.add(UserActivity(eventStartTimeMills = nowMills + 300_000,
                eventEndTimeMills = nowMills + 360_000,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, dayActivity.size)

        //Check the remaining item
        Assert.assertEquals(resultArray[0].eventStartTimeMills, dayActivity[1].eventStartTimeMills)
        Assert.assertEquals(resultArray[0].eventEndTimeMills, dayActivity[1].eventEndTimeMills)

        Assert.assertEquals(resultArray[1].eventStartTimeMills, dayActivity[0].eventStartTimeMills)
        Assert.assertEquals(resultArray[1].eventEndTimeMills, dayActivity[0].eventEndTimeMills)

        Assert.assertEquals(resultArray[2].eventStartTimeMills, dayActivity[2].eventStartTimeMills)
        Assert.assertEquals(resultArray[2].eventEndTimeMills, dayActivity[2].eventEndTimeMills)

    }

    @Test
    fun checkWithActivity_OnSameDay_ExactOneDayScratch() {
        val dayActivity = ArrayList<UserActivity>()
        val endMills = TimeUtils.getMidnightCal(System.currentTimeMillis()).timeInMillis
        val startMills = endMills - TimeUtils.ONE_DAY_MILLISECONDS

        //First activity
        dayActivity.add(UserActivity(eventStartTimeMills = startMills - 60_000,
                eventEndTimeMills = startMills,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        //Second activity
        dayActivity.add(UserActivity(eventStartTimeMills = startMills + 120_000,
                eventEndTimeMills = startMills + 240_000,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        //Third activity
        dayActivity.add(UserActivity(eventStartTimeMills = endMills,
                eventEndTimeMills = endMills + 300_000,
                type = UserActivityType.SITTING.name,
                isSynced = true))

        val resultArray = ArrayList<UserActivity>(dayActivity)
        DailyActivitySummary.convertToValidUserActivityList(resultArray)

        //Measure the stretch
        Assert.assertEquals(dayActivity.last().eventStartTimeMills - dayActivity.first().eventEndTimeMills,
                TimeUtils.ONE_DAY_MILLISECONDS)

        //Check the user activity list
        Assert.assertEquals(resultArray.size, dayActivity.size)

        //Check the remaining item
        Assert.assertEquals(resultArray[0].eventStartTimeMills, dayActivity[0].eventStartTimeMills)
        Assert.assertEquals(resultArray[0].eventEndTimeMills, dayActivity[0].eventEndTimeMills)

        Assert.assertEquals(resultArray[1].eventStartTimeMills, dayActivity[1].eventStartTimeMills)
        Assert.assertEquals(resultArray[1].eventEndTimeMills, dayActivity[1].eventEndTimeMills)

        Assert.assertEquals(resultArray[2].eventStartTimeMills, dayActivity[2].eventStartTimeMills)
        Assert.assertEquals(resultArray[2].eventEndTimeMills, dayActivity[2].eventEndTimeMills)
    }
}
