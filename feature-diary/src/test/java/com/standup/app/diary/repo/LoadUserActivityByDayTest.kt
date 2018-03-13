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
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.util.*

/**
 * Created by Kevalpatel2106 on 08-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class LoadUserActivityByDayTest {

    @Before
    fun setUp() = DiaryRepoImplHelperTest.setUpForAllTests()

    @After
    fun tearUp() = DiaryRepoImplHelperTest.tearUpForAllTests()

    @Test
    fun checkLoadUserActivityByDay_WithEmptyDb() {
        DiaryRepoImplHelperTest.mockWebServerManager.enqueueResponse(File(DiaryRepoImplHelperTest.RESPONSE_DIR_PATH
                + "/load_days_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<ArrayList<UserActivity>>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadUserActivityByDay(12, 2, 2018)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount(0)
                .assertComplete()
    }

    @Test
    fun checkLoadUserActivityByDay_With5EventsFor1DayInDb() {
        DiaryRepoImplHelperTest.insert5EventsInEachDayFor1Day()

        val calendar = Calendar.getInstance()
        val testSubscriber = TestSubscriber<ArrayList<UserActivity>>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadUserActivityByDay(
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
                month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR)
        ).subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0, { it.size == DiaryRepoImplHelperTest.userActivityDao.tableItems.size })
                .assertComplete()
    }

    @Test
    fun checkLoadUserActivityByDay_With2EventsFor7DayInDb() {
        DiaryRepoImplHelperTest.insert2EventsInEachDayFor7Days()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)

        val testSubscriber = TestSubscriber<ArrayList<UserActivity>>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadUserActivityByDay(
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
                month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR)
        ).subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0, { it.size == 2 })
                .assertComplete()
    }
}
