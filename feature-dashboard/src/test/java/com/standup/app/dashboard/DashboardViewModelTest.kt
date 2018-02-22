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

package com.standup.app.dashboard

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.TimeUtils
import com.standup.app.dashboard.repo.DashboardRepo
import com.standup.app.dashboard.repo.DashboardRepoImpl
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

/**
 * Created by Kevalpatel2106 on 04-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DashboardViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rule1 = RxSchedulersOverrideRule()

    private val DIFF_BETWEEN_END_AND_START: Int = 30000

    private lateinit var userActivityDao: UserActivityDaoMockImpl
    private val mockServerManager = MockServerManager()
    private lateinit var dashboardRepo: DashboardRepo
    private lateinit var model: DashboardViewModel

    @Before
    fun setUp() {
        mockServerManager.startMockWebServer()
        userActivityDao = UserActivityDaoMockImpl(ArrayList())

        dashboardRepo = DashboardRepoImpl(
                userActivityDao,
                NetworkApi().getRetrofitClient(mockServerManager.getBaseUrl())
        )

        model = DashboardViewModel(dashboardRepo)
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    fun checkGetTodaysSummaryEmptyDb() {
        model.getTodaysSummary()

        Assert.assertNull(model.todaySummary.value)
        Assert.assertEquals(model.todaySummaryErrorCallback.value!!.errorMessageRes,
                R.string.dashboard_today_summary_not_fount)
    }

    @Test
    fun checkGetTodaysSummaryWithEvents() {
        insertEventsForToday(10)

        model.getTodaysSummary()

        model.todaySummary.value?.let {
            Assert.assertEquals(it.year, Calendar.getInstance().get(Calendar.YEAR))
            Assert.assertEquals(it.monthOfYear, Calendar.getInstance().get(Calendar.MONTH))
            Assert.assertEquals(it.dayOfMonth, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            Assert.assertEquals(it.dayActivity.size, userActivityDao.tableItems.size)
        }
    }

    private fun insertEventsForToday(capacity: Int = 10) {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = TimeUtils.getTodaysCalender12AM()

        for (i in capacity downTo 1) {
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
