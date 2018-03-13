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

package com.standup.app.diary.repo

import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.kevalpatel2106.utils.TimeUtils
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

/**
 * Created by Kevalpatel2106 on 08-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

@RunWith(JUnit4::class)
class LoadUserActivityForDayFromCalenderTest {

    @Before
    fun setUp() = DiaryRepoImplHelperTest.setUpForAllTests()

    @After
    fun tearUp() = DiaryRepoImplHelperTest.tearUpForAllTests()

    @Test
    fun checkLoadUserActivityForDayFromCalender_WithEmptyDb() {
        val it = DiaryRepoImplHelperTest.dairyRepoImpl.loadUserActivityForDayFromCalender(Calendar.getInstance())
        Assert.assertEquals(0, it.size)
    }

    @Test
    fun checkLoadUserActivityForDayFromCalender_WithItemsAvailableForDay() {
        DiaryRepoImplHelperTest.insert2EventsInEachDayFor7Days()

        val cal = Calendar.getInstance()
        cal.timeInMillis = DiaryRepoImplHelperTest.userActivityDao.tableItems.first().eventStartTimeMills
        val it = DiaryRepoImplHelperTest.dairyRepoImpl.loadUserActivityForDayFromCalender(cal)

        Assert.assertEquals(2, it.size)

        val cal12Am = TimeUtils.getMidnightCal(cal.timeInMillis)
        //Assert the oldest item has the start time after 12 AM today
        Assert.assertTrue(it.last().eventStartTimeMills >= cal12Am.timeInMillis)

        //Assert the newest item has the start time before 11:59:59PM today
        Assert.assertTrue(it.first().eventEndTimeMills < cal12Am.timeInMillis
                + TimeUtils.ONE_DAY_MILLISECONDS)
    }

    @Test
    fun checkLoadUserActivityForDayFromCalender_WithItemStartingOnPreviousDay() {
        val startTime = System.currentTimeMillis() - TimeUtils.ONE_DAY_MILLISECONDS
        val endTime = System.currentTimeMillis()

        DiaryRepoImplHelperTest.userActivityDao.insert(UserActivity(eventStartTimeMills = startTime,
                eventEndTimeMills = endTime,
                type = UserActivityType.NOT_TRACKED.name.toLowerCase(),
                isSynced = false))

        val it = DiaryRepoImplHelperTest.dairyRepoImpl.loadUserActivityForDayFromCalender(Calendar.getInstance())
        Assert.assertEquals(1, it.size)


        Assert.assertEquals(startTime, it.first().eventStartTimeMills)
        Assert.assertEquals(endTime, it.first().eventEndTimeMills)
    }

    @Test
    fun checkLoadUserActivityForDayFromCalender_WithItemEndingOnNextDay() {
        val startTime = System.currentTimeMillis()
        val endTime = System.currentTimeMillis() + TimeUtils.ONE_DAY_MILLISECONDS

        DiaryRepoImplHelperTest.userActivityDao.insert(UserActivity(eventStartTimeMills = startTime,
                eventEndTimeMills = endTime,
                type = UserActivityType.NOT_TRACKED.name.toLowerCase(),
                isSynced = false))

        val it = DiaryRepoImplHelperTest.dairyRepoImpl.loadUserActivityForDayFromCalender(Calendar.getInstance())

        Assert.assertEquals(1, it.size)
        Assert.assertEquals(startTime, it.first().eventStartTimeMills)
        Assert.assertEquals(endTime, it.first().eventEndTimeMills)
    }

    @Test
    fun checkLoaUserActivityForDayFromCalender_WithItemsNotAvailableForDay() {
        DiaryRepoImplHelperTest.insert2EventsInEachDayFor7Days()

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -30)
        val it = DiaryRepoImplHelperTest.dairyRepoImpl.loadUserActivityForDayFromCalender(cal)

        Assert.assertEquals(0, it.size)
    }
}
