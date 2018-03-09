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

import com.kevalpatel2106.common.db.DailyActivitySummary
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

/**
 * Created by Kevalpatel2106 on 08-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class LoadDaysSummaryListTest {

    @Before
    fun setUp() = DiaryRepoImplHelperTest.setUpForAllTests()

    @After
    fun tearUp() = DiaryRepoImplHelperTest.tearUpForAllTests()

    @Test
    fun checkLoadDaysSummaryList_WhenEmptyDb() {
        DiaryRepoImplHelperTest.mockWebServerManager.enqueueResponse(File(DiaryRepoImplHelperTest.RESPONSE_DIR_PATH
                + "/load_days_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(System.currentTimeMillis())
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount(0)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_With1DayInDb() {
        DiaryRepoImplHelperTest.insert5EventsInEachDayFor1Day()

        DiaryRepoImplHelperTest.mockWebServerManager.enqueueResponse(File(DiaryRepoImplHelperTest.RESPONSE_DIR_PATH
                + "/load_days_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(DiaryRepoImplHelperTest.userActivityDao.tableItems.last().eventEndTimeMills)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_With15DaysInDb() {
        DiaryRepoImplHelperTest.insertOneEventsInEachDayFor15Days()

        DiaryRepoImplHelperTest.mockWebServerManager.enqueueResponse(File(DiaryRepoImplHelperTest.RESPONSE_DIR_PATH
                + "/load_days_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(DiaryRepoImplHelperTest.userActivityDao.tableItems.first().eventEndTimeMills)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount(DiaryRepo.PAGE_SIZE)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_With7DaysInDb() {
        DiaryRepoImplHelperTest.insert2EventsInEachDayFor7Days()

        DiaryRepoImplHelperTest.mockWebServerManager.enqueueResponse(File(DiaryRepoImplHelperTest.RESPONSE_DIR_PATH
                + "/load_days_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(DiaryRepoImplHelperTest.userActivityDao.tableItems.first().eventEndTimeMills)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount((DiaryRepo.PAGE_SIZE + 4) / 2)
                .assertComplete()
    }
}
