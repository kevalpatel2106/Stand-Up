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

package com.standup.app.dashboard.repo

import com.kevalpatel2106.common.db.DailyActivitySummary
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.TimeUtils
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

/**
 * Created by Kevalpatel2106 on 04-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DashboardRepoImplTest {
    private val DIFF_BETWEEN_END_AND_START: Int = 30000

    private lateinit var userActivityDao: UserActivityDaoMockImpl
    private val mockServerManager = MockServerManager()
    private lateinit var dashboardRepo: DashboardRepo

    @Before
    fun setUp() {
        mockServerManager.startMockWebServer()

        userActivityDao = UserActivityDaoMockImpl(ArrayList())
        dashboardRepo = DashboardRepoImpl(
                userActivityDao,
                ApiProvider().getRetrofitClient(mockServerManager.getBaseUrl())
        )
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    fun checkTodaySummaryEmptyDb() {
        val testSubscriber = TestSubscriber<DailyActivitySummary>()

        dashboardRepo.getTodaySummary().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
                .assertValueCount(0)
                .assertComplete()
    }

    @Test
    fun checkTodaySummary() {
        insertEventsForToday()
        val testSubscriber = TestSubscriber<DailyActivitySummary>()

        dashboardRepo.getTodaySummary().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) {
                    it.year == Calendar.getInstance().get(Calendar.YEAR)
                }
                .assertValueAt(0) {
                    it.dayActivity.size == userActivityDao.tableItems.size
                }
                .assertValueAt(0) {
                    it.monthOfYear == Calendar.getInstance().get(Calendar.MONTH)
                }
                .assertValueAt(0) {
                    it.dayOfMonth == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                }
                .assertComplete()
    }

    private fun insertEventsForToday() {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = TimeUtils.getTodaysCalender12AM()

        for (i in 10 downTo 1) {
            cal.add(Calendar.HOUR_OF_DAY, 1)

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
