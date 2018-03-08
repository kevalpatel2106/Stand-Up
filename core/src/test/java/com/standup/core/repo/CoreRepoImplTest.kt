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

package com.standup.core.repo

import com.kevalpatel2106.common.db.DailyActivitySummary
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.common.db.userActivity.UserActivityHelper
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.network.NetworkModule
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.TimeUtils
import com.standup.core.CoreConfig
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

/**
 * Created by Kevalpatel2106 on 01-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class CoreRepoImplTest {

    private lateinit var reminderRepo: CoreRepoImpl
    private lateinit var userActivityDao: UserActivityDaoMockImpl
    private lateinit var mockWebServerManager: MockServerManager

    @Before
    fun setUp() {
        //Mock network set
        mockWebServerManager = MockServerManager()
        mockWebServerManager.startMockWebServer()

        //Mock database table
        userActivityDao = UserActivityDaoMockImpl(ArrayList())

        reminderRepo = CoreRepoImpl(
                userActivityDao,
                NetworkModule().getRetrofitClient(mockWebServerManager.getBaseUrl())
        )
    }

    @After
    fun tearUp() {
        mockWebServerManager.close()
    }

    @Test
    fun checkInsertNewUserActivity_WithInvalidStartTime() {
        try {

            val endTime = System.currentTimeMillis()
            val userActivityToInsert = UserActivity(
                    eventStartTimeMills = 0,
                    eventEndTimeMills = endTime,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase()
            )

            val testObserver = TestObserver<Long>()
            reminderRepo.insertNewUserActivity(userActivityToInsert).subscribe(testObserver)

            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
            //NO OP
        }
    }

    @Test
    fun checkInsertNewUserActivity_WithInvalidEndTime() {
        try {

            val startTime = System.currentTimeMillis() - 300_000L
            val userActivityToInsert = UserActivity(
                    eventStartTimeMills = startTime,
                    eventEndTimeMills = 0,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase()
            )

            val testObserver = TestObserver<Long>()
            reminderRepo.insertNewUserActivity(userActivityToInsert).subscribe(testObserver)

            testObserver.assertNoErrors()
                    .assertValueCount(1)
                    .assertValueAt(0) { it == 0L }
                    .assertComplete()
            Assert.assertEquals(userActivityDao.tableItems.size, 1)
            Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTime)
            Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, startTime
                    + UserActivityHelper.endTimeCorrectionValue)

            Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.MOVING)
        } catch (e: IllegalArgumentException) {
            Assert.fail()
        }
    }

    @Test
    fun checkInsertNewUserActivity_WithNoLatestActivity() {
        val startTime = System.currentTimeMillis() - 3600000
        val endTime = System.currentTimeMillis()
        val userActivityToInsert = UserActivity(
                eventStartTimeMills = startTime,
                eventEndTimeMills = endTime,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase()
        )

        val testObserver = TestObserver<Long>()
        reminderRepo.insertNewUserActivity(userActivityToInsert)
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { it == 0L }
                .assertComplete()
        Assert.assertEquals(userActivityDao.tableItems.size, 1)
        Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTime)
        Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, endTime)
        Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.MOVING)
    }

    @Test
    fun checkInsertNewUserActivity_WithLatestActivityOfSameType() {
        val startTime = System.currentTimeMillis() - 3600000
        //Set fake data.
        userActivityDao.insert(UserActivity(
                eventStartTimeMills = System.currentTimeMillis() - 4200000,
                eventEndTimeMills = System.currentTimeMillis() - 3600000,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase())
        )
        userActivityDao.insert(UserActivity(
                eventStartTimeMills = startTime,
                eventEndTimeMills = startTime + 10_000L,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase())
        )

        val userActivityToInsert = UserActivity(
                eventStartTimeMills = System.currentTimeMillis() - 180000,
                eventEndTimeMills = System.currentTimeMillis(),
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase()
        )

        val testObserver = TestObserver<Long>()
        reminderRepo.insertNewUserActivity(userActivityToInsert)
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { it == userActivityDao.tableItems[0].localId }
                .assertComplete()
        Assert.assertEquals(userActivityDao.tableItems.size, 2)
        Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTime)
        Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, userActivityToInsert.eventEndTimeMills)
        Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.MOVING)
    }

    @Test
    fun checkInsertNewUserActivity_DoNotMeagre_WithLatestActivityOfSameType() {
        val now = System.currentTimeMillis()

        //Set fake data.
        //Old activity
        userActivityDao.insert(UserActivity(
                eventStartTimeMills = now - 4200_000,
                eventEndTimeMills = now - 3600_000,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase())
        )

        //Latest activity
        userActivityDao.insert(UserActivity(
                eventStartTimeMills = now - 1800_000,
                eventEndTimeMills = now + 90_000L,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase())
        )

        //Activity to insert
        val userActivityToInsert = UserActivity(
                eventStartTimeMills = now - 60_000,
                eventEndTimeMills = now,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase()
        )

        val testObserver = TestObserver<Long>()
        reminderRepo.insertNewUserActivity(
                newActivity = userActivityToInsert,
                doNotMergeWithPrevious = true
        ).subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { it == userActivityDao.tableItems[0].localId }
                .assertComplete()
        Assert.assertEquals(userActivityDao.tableItems.size, 3)

        Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, userActivityToInsert.eventStartTimeMills)
        Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, userActivityToInsert.eventEndTimeMills)
        Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.MOVING)

        Assert.assertEquals(userActivityDao.tableItems[1].eventStartTimeMills, now - 1800_000)
        Assert.assertEquals(userActivityDao.tableItems[1].eventEndTimeMills, now + 90_000L)
        Assert.assertEquals(userActivityDao.tableItems[1].userActivityType, UserActivityType.MOVING)
    }

    @Test
    fun checkInsertNewUserActivity_WithLatestActivityOfDifferentType() {
        //Set fake data.
        val endTimeOfNewEvent = System.currentTimeMillis()
        val startTimeOfNewEvent = endTimeOfNewEvent - 180000

        userActivityDao.insert(UserActivity(
                eventStartTimeMills = endTimeOfNewEvent - 4200000,
                eventEndTimeMills = endTimeOfNewEvent - 3600000,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase())
        )
        userActivityDao.insert(UserActivity(
                eventStartTimeMills = endTimeOfNewEvent - 3600000,
                eventEndTimeMills = startTimeOfNewEvent - CoreConfig.MONITOR_SERVICE_PERIOD,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase())
        )

        val userActivityToInsert = UserActivity(
                eventStartTimeMills = startTimeOfNewEvent,
                eventEndTimeMills = endTimeOfNewEvent,
                isSynced = false,
                type = UserActivityType.SITTING.toString().toLowerCase()
        )

        val testObserver = TestObserver<Long>()
        reminderRepo.insertNewUserActivity(userActivityToInsert)
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { it == 2L }
                .assertComplete()

        Assert.assertEquals(userActivityDao.tableItems.size, 3)

        Assert.assertEquals(userActivityDao.tableItems[1].eventStartTimeMills, endTimeOfNewEvent - 3600000)
        Assert.assertEquals(userActivityDao.tableItems[1].eventEndTimeMills, startTimeOfNewEvent)
        Assert.assertEquals(userActivityDao.tableItems[1].userActivityType, UserActivityType.MOVING)

        Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTimeOfNewEvent)
        Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, endTimeOfNewEvent)
        Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.SITTING)
    }

    @Test
    fun checkInsertNewUserActivity_WithLatestActivityOfDifferentTypeAndNotTracked() {
        //Set fake data.
        val endTimeOfNewEvent = System.currentTimeMillis()
        val startTimeOfNewEvent = endTimeOfNewEvent - 180000

        userActivityDao.insert(UserActivity(
                eventStartTimeMills = endTimeOfNewEvent - 4200000,
                eventEndTimeMills = endTimeOfNewEvent - 3600000,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase())
        )
        userActivityDao.insert(UserActivity(
                eventStartTimeMills = endTimeOfNewEvent - 3600000,
                eventEndTimeMills = startTimeOfNewEvent - 3 * CoreConfig.MONITOR_SERVICE_PERIOD,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase())
        )

        val userActivityToInsert = UserActivity(
                eventStartTimeMills = startTimeOfNewEvent,
                eventEndTimeMills = endTimeOfNewEvent,
                isSynced = false,
                type = UserActivityType.SITTING.toString().toLowerCase()
        )

        val testObserver = TestObserver<Long>()
        reminderRepo.insertNewUserActivity(userActivityToInsert)
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { it == 2L }
                .assertComplete()

        Assert.assertEquals(userActivityDao.tableItems.size, 3)

        Assert.assertEquals(userActivityDao.tableItems[1].eventStartTimeMills,
                endTimeOfNewEvent - 3600000)
        Assert.assertEquals(userActivityDao.tableItems[1].eventEndTimeMills,
                startTimeOfNewEvent - 3 * CoreConfig.MONITOR_SERVICE_PERIOD)
        Assert.assertEquals(userActivityDao.tableItems[1].userActivityType,
                UserActivityType.MOVING)

        Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTimeOfNewEvent)
        Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, endTimeOfNewEvent)
        Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.SITTING)
    }


    @Test
    fun checkLoadYesterdaySummary_NoActivityInDb() {
        try {
            val testSubscribe = TestSubscriber<DailyActivitySummary>()
            reminderRepo.loadYesterdaySummary().subscribe(testSubscribe)
            testSubscribe.awaitTerminalEvent()

            testSubscribe.assertNoErrors()
                    .assertComplete()
                    .assertValueCount(0)
        } catch (e: IllegalArgumentException) {
            Assert.fail()
        }
    }

    @Test
    fun checkLoadYesterdaySummary_NoActivityInDbForYesterday() {
        try {
            val now = TimeUtils.todayMidnightCal(false).timeInMillis

            //Set fake data.
            //Todays activity
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now,
                    eventEndTimeMills = now + 4200_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            //3 days before activity
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS,
                    eventEndTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS + 3600_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            val testSubscribe = TestSubscriber<DailyActivitySummary>()
            reminderRepo.loadYesterdaySummary().subscribe(testSubscribe)
            testSubscribe.awaitTerminalEvent()

            testSubscribe.assertNoErrors()
                    .assertComplete()
                    .assertValueCount(0)
        } catch (e: IllegalArgumentException) {
            Assert.fail(e.printStackTrace().toString())
        }
    }

    @Test
    fun checkLoadYesterdaySummary_ActivityInDbForYesterday() {
        try {
            val todayMidnightMills = TimeUtils.todayMidnightCal(false).timeInMillis

            //Set fake data.
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = todayMidnightMills - TimeUtils.ONE_DAY_MILLISECONDS,
                    eventEndTimeMills = todayMidnightMills - TimeUtils.ONE_DAY_MILLISECONDS + 3600_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = todayMidnightMills - TimeUtils.ONE_DAY_MILLISECONDS + 3600_000,
                    eventEndTimeMills = todayMidnightMills - TimeUtils.ONE_DAY_MILLISECONDS + 4000_000,
                    isSynced = false,
                    type = UserActivityType.SITTING.toString().toLowerCase())
            )

            val testSubscribe = TestSubscriber<DailyActivitySummary>()
            reminderRepo.loadYesterdaySummary().subscribe(testSubscribe)
            testSubscribe.awaitTerminalEvent()

            val dayCal = Calendar.getInstance()
            dayCal.add(Calendar.DAY_OF_MONTH, -1)       //Previous day

            testSubscribe.assertNoErrors()
                    .assertComplete()
                    .assertValueCount(1)
                    .assertValueAt(0, { it.dayActivity.size == userActivityDao.tableItems.size })
                    .assertValueAt(0, { it.dayOfMonth == dayCal.get(Calendar.DAY_OF_MONTH) })
                    .assertValueAt(0, { it.monthOfYear == dayCal.get(Calendar.MONTH) })
                    .assertValueAt(0, { it.year == dayCal.get(Calendar.YEAR) })
        } catch (e: IllegalArgumentException) {
            Assert.fail(e.printStackTrace().toString())
        }
    }
}
