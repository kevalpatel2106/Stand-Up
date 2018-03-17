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

import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.TimeUtils
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

    private lateinit var coreRepoImpl: CoreRepoImpl
    private lateinit var userActivityDao: UserActivityDaoMockImpl
    private lateinit var mockWebServerManager: MockServerManager

    @Before
    fun setUp() {
        //Mock network set
        mockWebServerManager = MockServerManager()
        mockWebServerManager.startMockWebServer()

        //Mock database table
        userActivityDao = UserActivityDaoMockImpl(ArrayList())

        coreRepoImpl = CoreRepoImpl(userActivityDao)
    }

    @After
    fun tearUp() {
        mockWebServerManager.close()
    }

    @Test
    fun checkLoadYesterdaySummary_NoActivityInDb() {
        try {
            val testSubscribe = TestSubscriber<DailyActivitySummary>()
            coreRepoImpl.loadYesterdaySummary().subscribe(testSubscribe)
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
            val today12am = TimeUtils.todayMidnightMills()

            //Set fake data.
            //Todays activity
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = today12am,
                    eventEndTimeMills = today12am + 4200_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            //3 days before activity
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = today12am - 2 * TimeUtils.ONE_DAY_MILLISECONDS,
                    eventEndTimeMills = today12am - 2 * TimeUtils.ONE_DAY_MILLISECONDS + 3600_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            val testSubscribe = TestSubscriber<DailyActivitySummary>()
            coreRepoImpl.loadYesterdaySummary().subscribe(testSubscribe)
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
            val yesterday12am = TimeUtils.todayMidnightMills() - TimeUtils.ONE_DAY_MILLISECONDS

            //Set fake data.
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = yesterday12am,
                    eventEndTimeMills = yesterday12am + 3600_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = yesterday12am + 3700_000,
                    eventEndTimeMills = yesterday12am + 4000_000,
                    isSynced = false,
                    type = UserActivityType.SITTING.toString().toLowerCase())
            )

            val testSubscribe = TestSubscriber<DailyActivitySummary>()
            coreRepoImpl.loadYesterdaySummary().subscribe(testSubscribe)
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
