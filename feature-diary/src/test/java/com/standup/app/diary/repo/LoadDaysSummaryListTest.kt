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

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.TimeUtils
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Rule
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
class LoadDaysSummaryListTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rule1 = RxSchedulersOverrideRule()

    @Before
    fun setUp() = DiaryRepoImplHelperTest.setUpForAllTests()

    @After
    fun tearUp() = DiaryRepoImplHelperTest.tearUpForAllTests()

    @Test
    fun checkLoadDaysSummaryList_WhenEmptyDb_NoDataFromServer() {
        //Prepare the data
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.mockWebServerManager.getResponsesPath()
                        + "/get_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(
                TimeUtils.todayMidnightMills())
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount(0)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_WhenEmptyDb_DataFromServerForOlderDay() {
        //Prepare the data
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.mockWebServerManager.getResponsesPath()
                        + "/get_activity_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(System.currentTimeMillis())
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0, {
                    it.dayActivity.size == 2
                })
                .assertValueAt(0, {
                    it.dayActivity[0].remoteId == 5110359589388288L /* From response */
                })
                .assertValueAt(0, {
                    it.dayActivity[1].remoteId == 5653294995210240L /* From response */
                })
                .assertValueAt(0, {
                    TimeUtils.getMidnightCal(1520585420827).get(Calendar.DAY_OF_MONTH) == it.dayOfMonth
                })
                .assertValueAt(0, {
                    TimeUtils.getMidnightCal(1520585420827).get(Calendar.MONTH) == it.monthOfYear
                })
                .assertValueAt(0, {
                    TimeUtils.getMidnightCal(1520585420827).get(Calendar.YEAR) == it.year
                })
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_WhenEmptyDb_DataFromServerForNewDays() {
        //Prepare the data
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.mockWebServerManager.getResponsesPath()
                        + "/get_activity_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(
                TimeUtils.getMidnightCal(1, 3, 2016).timeInMillis)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount(0)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_WhenEmptyDb_ErrorServerFor() {
        //Prepare the data
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.mockWebServerManager.getResponsesPath()
                        + "/authentication_field_missing.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(
                TimeUtils.getMidnightCal(1, 3, 2016).timeInMillis)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount(0)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_With1DayInDb_NoDataFromServer() {
        //Prepare the data
        DiaryRepoImplHelperTest.insert5EventsInEachDayFor1Day()
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.mockWebServerManager.getResponsesPath()
                + "/get_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl
                .loadDaysSummaryList(DiaryRepoImplHelperTest.userActivityDao.tableItems.last().eventEndTimeMills)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertComplete()
                .assertValueAt(0, {
                    it.dayActivity.size == 5
                })
                .assertValueAt(0, {
                    TimeUtils.todayMidnightCal().get(Calendar.DAY_OF_MONTH) == it.dayOfMonth
                })
                .assertValueAt(0, {
                    TimeUtils.todayMidnightCal().get(Calendar.MONTH) == it.monthOfYear
                })
                .assertValueAt(0, {
                    TimeUtils.todayMidnightCal().get(Calendar.YEAR) == it.year
                })
    }

    @Test
    fun checkLoadDaysSummaryList_With1DayInDb_WithDatFromServer() {
        //Prepare the data
        DiaryRepoImplHelperTest.insert5EventsInEachDayFor1Day()
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.mockWebServerManager.getResponsesPath()
                        + "/get_activity_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(DiaryRepoImplHelperTest.userActivityDao.tableItems.last().eventEndTimeMills)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0, {
                    TimeUtils.todayMidnightCal().get(Calendar.DAY_OF_MONTH) == it.dayOfMonth
                })
                .assertValueAt(0, {
                    TimeUtils.todayMidnightCal().get(Calendar.MONTH) == it.monthOfYear
                })
                .assertValueAt(0, {
                    TimeUtils.todayMidnightCal().get(Calendar.YEAR) == it.year
                })
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_With15DaysInDb() {
        //Prepare the data
        DiaryRepoImplHelperTest.insertOneEventsInEachDayFor15Days()
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.mockWebServerManager.getResponsesPath()
                        + "/get_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl
                .loadDaysSummaryList(DiaryRepoImplHelperTest.userActivityDao.tableItems.first().eventEndTimeMills)
                .subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount(DiaryRepo.PAGE_SIZE)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_With7DaysInDb() {
        //Prepare the data
        DiaryRepoImplHelperTest.insert2EventsInEachDayFor7Days()
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.mockWebServerManager.getResponsesPath()
                        + "/get_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(DiaryRepoImplHelperTest.userActivityDao.tableItems.first().eventEndTimeMills)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount((DiaryRepo.PAGE_SIZE + 4) / 2)
                .assertComplete()
    }
}
