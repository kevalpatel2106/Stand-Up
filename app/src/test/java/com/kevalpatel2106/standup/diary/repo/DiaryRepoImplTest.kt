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

package com.kevalpatel2106.standup.diary.repo

import com.kevalpatel2106.standup.db.DailyActivitySummary
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.TimeUtils
import io.reactivex.subscribers.TestSubscriber
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.nio.file.Paths
import java.util.*

/**
 * Created by Keval on 02/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DiaryRepoImplTest {
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/kevalpatel2106/standup/diary/repo",
            Paths.get("").toAbsolutePath().toString())

    private lateinit var dairyRepoImpl: DiaryRepoImpl
    private lateinit var userActivityDao: UserActivityDaoMockImpl
    private lateinit var mockWebServerManager: MockServerManager

    private val DIFF_BETWEEN_END_AND_START = 60000L   //60 Sec

    @Before
    fun setUp() {
        //Mock network set
        mockWebServerManager = MockServerManager()
        mockWebServerManager.startMockWebServer()

        //Mock database table
        userActivityDao = UserActivityDaoMockImpl(ArrayList())

        dairyRepoImpl = DiaryRepoImpl(userActivityDao, mockWebServerManager.getBaseUrl())
    }

    @After
    fun tearUp() {
        mockWebServerManager.close()
    }

    @Test
    fun checkLoadDaysSummaryListWhenEmptyDb() {
        mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                + "/load_days_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        dairyRepoImpl.loadDaysSummaryList(System.currentTimeMillis())
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount(0)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryListWith1DayInDb() {
        insert5EventsInEachDayFor1Day()

        mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                + "/load_days_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        dairyRepoImpl.loadDaysSummaryList(userActivityDao.tableItems.last().eventEndTimeMills)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryListWith15DaysInDb() {
        insertOneEventsInEachDayFor15Days()

        mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                + "/load_days_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        dairyRepoImpl.loadDaysSummaryList(userActivityDao.tableItems.first().eventEndTimeMills)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount(DiaryRepo.PAGE_SIZE)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryListWith7DaysInDb() {
        insert2EventsInEachDayFor7Days()

        mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                + "/load_days_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        dairyRepoImpl.loadDaysSummaryList(userActivityDao.tableItems.first().eventEndTimeMills)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount((DiaryRepo.PAGE_SIZE + 4) / 2)
                .assertComplete()
    }


    @Test
    fun checkLoadUserActivityByDayWithEmptyDb() {
        val it = dairyRepoImpl.loadUserActivityByDay(Calendar.getInstance())
        Assert.assertEquals(0, it.size)
    }


    @Test
    fun checkLoadUserActivityByDayWithItemsAvailableForDay() {
        insert2EventsInEachDayFor7Days()

        val cal = Calendar.getInstance()
        cal.timeInMillis = userActivityDao.tableItems.first().eventStartTimeMills
        val it = dairyRepoImpl.loadUserActivityByDay(cal)

        Assert.assertEquals(2, it.size)

        val cal12Am = TimeUtils.getCalender12AM(cal.timeInMillis)
        //Assert the oldest item has the start time after 12 AM today
        Assert.assertTrue(it.last().eventStartTimeMills >= cal12Am.timeInMillis)

        //Assert the newest item has the start time before 11:59:59PM today
        Assert.assertTrue(it.first().eventEndTimeMills < cal12Am.timeInMillis
                + TimeUtils.ONE_DAY_MILLISECONDS)
    }

    @Test
    fun checkLoadUserActivityByDayWithItemStartingOnPreviousDay() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -1)

        val startTime = cal.timeInMillis
        val endTime = System.currentTimeMillis()

        userActivityDao.insert(UserActivity(eventStartTimeMills = startTime,
                eventEndTimeMills = endTime,
                type = UserActivityType.NOT_TRACKED.name.toLowerCase(),
                isSynced = false))

        val it = dairyRepoImpl.loadUserActivityByDay(Calendar.getInstance())
        Assert.assertEquals(0, it.size)
    }

    @Test
    fun checkLoadUserActivityByDayWithItemEndingOnNextDay() {
        val cal = Calendar.getInstance()
        val startTime = cal.timeInMillis

        cal.add(Calendar.DAY_OF_MONTH, 1)
        val endTime = cal.timeInMillis

        userActivityDao.insert(UserActivity(eventStartTimeMills = startTime,
                eventEndTimeMills = endTime,
                type = UserActivityType.NOT_TRACKED.name.toLowerCase(),
                isSynced = false))

        val it = dairyRepoImpl.loadUserActivityByDay(Calendar.getInstance())

        Assert.assertEquals(1, it.size)
        Assert.assertEquals(it.last().eventStartTimeMills, startTime)
        Assert.assertEquals(it.first().eventEndTimeMills, endTime)
    }

    @Test
    fun checkLoadUserActivityByDayWithItemsNotAvailableForDay() {
        insert2EventsInEachDayFor7Days()

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -30)
        val it = dairyRepoImpl.loadUserActivityByDay(cal)

        Assert.assertEquals(0, it.size)
    }

    private fun insert2EventsInEachDayFor7Days() {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = TimeUtils.getTodaysCalender12AM()

        for (i in DiaryRepo.PAGE_SIZE + 4 downTo 1) {

            if (i.rem(2) == 0) cal.add(Calendar.DAY_OF_MONTH, -1)

            val startTime = cal.timeInMillis + if (i.rem(2) == 0) DIFF_BETWEEN_END_AND_START else 0
            val endTime = startTime + DIFF_BETWEEN_END_AND_START

            userActivityDao.insert(UserActivity(eventStartTimeMills = startTime,
                    eventEndTimeMills = endTime,
                    type = when {
                        i.rem(2) == 0 -> UserActivityType.SITTING.name.toLowerCase()
                        i.rem(3) == 0 -> UserActivityType.MOVING.name.toLowerCase()
                        else -> UserActivityType.NOT_TRACKED.name.toLowerCase()
                    },
                    isSynced = false))
        }
    }

    private fun insert5EventsInEachDayFor1Day() {
        //Set fake db items so that we have all the 5 activities in one single day
        val cal = TimeUtils.getTodaysCalender12AM()

        for (i in 15 downTo 1) {
            val startTime = cal.timeInMillis + (i * DIFF_BETWEEN_END_AND_START)
            val endTime = startTime + DIFF_BETWEEN_END_AND_START

            userActivityDao.insert(UserActivity(eventStartTimeMills = startTime,
                    eventEndTimeMills = endTime,
                    type = when {
                        i.rem(2) == 0 -> UserActivityType.SITTING.name.toLowerCase()
                        i.rem(3) == 0 -> UserActivityType.MOVING.name.toLowerCase()
                        else -> UserActivityType.NOT_TRACKED.name.toLowerCase()
                    },
                    isSynced = false))
        }
    }

    private fun insertOneEventsInEachDayFor15Days() {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = TimeUtils.getTodaysCalender12AM()

        for (i in DiaryRepo.PAGE_SIZE + 5 downTo 1) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
            val startTime = cal.timeInMillis
            val endTime = startTime + DIFF_BETWEEN_END_AND_START

            userActivityDao.insert(UserActivity(eventStartTimeMills = startTime,
                    eventEndTimeMills = endTime,
                    type = when {
                        i.rem(2) == 0 -> UserActivityType.SITTING.name.toLowerCase()
                        i.rem(3) == 0 -> UserActivityType.MOVING.name.toLowerCase()
                        else -> UserActivityType.NOT_TRACKED.name.toLowerCase()
                    },
                    isSynced = false))
        }
    }
}