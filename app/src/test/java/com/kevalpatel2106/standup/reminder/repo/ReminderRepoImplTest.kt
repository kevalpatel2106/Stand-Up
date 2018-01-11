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

package com.kevalpatel2106.standup.reminder.repo

import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import com.kevalpatel2106.testutils.MockServerManager
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 01-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ReminderRepoImplTest {

    private lateinit var reminderRepo: ReminderRepoImpl
    private lateinit var userActivityDao: UserActivityDaoMockImpl
    private lateinit var mockWebServerManager: MockServerManager

    @Before
    fun setUp() {
        //Mock network set
        mockWebServerManager = MockServerManager()
        mockWebServerManager.startMockWebServer()

        //Mock database table
        userActivityDao = UserActivityDaoMockImpl(ArrayList())

        reminderRepo = ReminderRepoImpl(
                userActivityDao,
                ApiProvider().getRetrofitClient(mockWebServerManager.getBaseUrl())
        )
    }

    @After
    fun tearUp() {
        mockWebServerManager.close()
    }

    @Test
    fun checkInsertNewAndTerminatePreviousActivityWithNoLatestActivity() {
        val startTime = System.currentTimeMillis() - 3600000
        val endTime = System.currentTimeMillis()
        val userActivityToInsert = UserActivity(
                eventStartTimeMills = startTime,
                eventEndTimeMills = endTime,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase()
        )

        val testObserver = TestObserver<Long>()
        reminderRepo.insertNewAndTerminatePreviousActivity(userActivityToInsert)
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
    fun checkInsertNewAndTerminatePreviousActivityWithLatestActivityOfSameType() {
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
                eventEndTimeMills = 0,
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
        reminderRepo.insertNewAndTerminatePreviousActivity(userActivityToInsert)
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { it == 0L }
                .assertComplete()
        Assert.assertEquals(userActivityDao.tableItems.size, 2)
        Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTime)
        Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, 0 /* Event not ended yet */)
        Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.MOVING)
    }

    @Test
    fun checkInsertNewAndTerminatePreviousActivityWithLatestActivityOfDifferentType() {
        //Set fake data.
        val startTime = System.currentTimeMillis() - 3600000
        val startTimeOfNewEvent = System.currentTimeMillis() - 180000
        val endTimeOfNewEvent = System.currentTimeMillis()
        userActivityDao.insert(UserActivity(
                eventStartTimeMills = System.currentTimeMillis() - 4200000,
                eventEndTimeMills = System.currentTimeMillis() - 3600000,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase())
        )
        userActivityDao.insert(UserActivity(
                eventStartTimeMills = startTime,
                eventEndTimeMills = 0,
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
        reminderRepo.insertNewAndTerminatePreviousActivity(userActivityToInsert)
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { it == 2L }
                .assertComplete()

        Assert.assertEquals(userActivityDao.tableItems.size, 3)

        Assert.assertEquals(userActivityDao.tableItems[1].eventStartTimeMills, startTime)
        Assert.assertEquals(userActivityDao.tableItems[1].eventEndTimeMills, startTimeOfNewEvent)
        Assert.assertEquals(userActivityDao.tableItems[1].userActivityType, UserActivityType.MOVING)

        Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTimeOfNewEvent)
        Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, endTimeOfNewEvent)
        Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.SITTING)
    }

}
