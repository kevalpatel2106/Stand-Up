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

package com.kevalpatel2106.standup.reminder.repo

import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityDaoUnitTestImpl
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.TimeUtils
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 01-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ReminderRepoImplTest {

    private lateinit var reminderRepo: ReminderRepoImpl
    private lateinit var userActivityDao: UserActivityDaoUnitTestImpl
    private lateinit var mockWebServerManager: MockServerManager

    @Before
    fun setUp() {
        mockWebServerManager = MockServerManager()
        mockWebServerManager.startMockWebServer()

        userActivityDao = UserActivityDaoUnitTestImpl()

        reminderRepo = ReminderRepoImpl(userActivityDao, mockWebServerManager.getBaseUrl())
    }

    @After
    fun tearUp(){
        mockWebServerManager.close()
    }

    @Test
    fun checkInsertNewAndTerminatePreviousActivityWithNoLatestActivity() {

        //Set userActivityDao data.
        userActivityDao.latestUserActivity = null
        userActivityDao.insertItemId = 1234567L
        userActivityDao.numberOfUpdatedItem = 1

        val userActivityToInsert = UserActivity(
                eventStartTimeMills = System.currentTimeMillis() - 3600000,
                eventEndTimeMills = System.currentTimeMillis(),
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase()
        )

        val testObserver = TestObserver<Long> ()
        reminderRepo.insertNewAndTerminatePreviousActivity(userActivityToInsert)
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0){ it == 1234567L }
                .assertComplete()
    }

    @Test
    fun checkInsertNewAndTerminatePreviousActivityWithLatestActivityOfSameType() {

        //Set userActivityDao data.
        userActivityDao.latestUserActivity =  UserActivity(
                eventStartTimeMills = System.currentTimeMillis() - 3600000,
                eventEndTimeMills = 0,
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase()
        )
        userActivityDao.insertItemId = 1234567L
        userActivityDao.numberOfUpdatedItem = 1

        val userActivityToInsert = UserActivity(
                eventStartTimeMills = System.currentTimeMillis() - 180000,
                eventEndTimeMills = System.currentTimeMillis(),
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase()
        )

        val testObserver = TestObserver<Long> ()
        reminderRepo.insertNewAndTerminatePreviousActivity(userActivityToInsert)
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0){ it == 0L }
                .assertComplete()
    }

    @Test
    fun checkInsertNewAndTerminatePreviousActivityWithLatestActivityOfDifferentType() {

        //Set userActivityDao data.
        userActivityDao.latestUserActivity =  UserActivity(
                eventStartTimeMills = System.currentTimeMillis() - 3600000,
                eventEndTimeMills = 0,
                isSynced = false,
                type = UserActivityType.SITTING.toString().toLowerCase()
        )
        userActivityDao.insertItemId = 1234567L
        userActivityDao.numberOfUpdatedItem = 1

        val userActivityToInsert = UserActivity(
                eventStartTimeMills = System.currentTimeMillis() - 180000,
                eventEndTimeMills = System.currentTimeMillis(),
                isSynced = false,
                type = UserActivityType.MOVING.toString().toLowerCase()
        )

        val testObserver = TestObserver<Long> ()
        reminderRepo.insertNewAndTerminatePreviousActivity(userActivityToInsert)
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0){ it == 1234567L }
                .assertComplete()
    }

}