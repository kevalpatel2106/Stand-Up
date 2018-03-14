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

import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.kevalpatel2106.utils.TimeUtils
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Assert
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
    fun checkLoadDaysSummaryList_WhenEmptyDb_NoDataFromServer() {
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.RESPONSE_DIR_PATH
                        + "/get_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(
                TimeUtils.getMidnightCal(unixMills = System.currentTimeMillis(), isUtc = false).timeInMillis)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        //Verify the request
        val request1 = DiaryRepoImplHelperTest.mockWebServerManager.mockWebServer.takeRequest()
        Assert.assertEquals("/getActivity", request1.path)
        Assert.assertNotNull(request1.getHeader("Authorization"))

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount(0)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_WhenEmptyDb_DataFromServerForOlderDay() {
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.RESPONSE_DIR_PATH
                        + "/get_activity_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(System.currentTimeMillis())
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        //Verify the request
        val request1 = DiaryRepoImplHelperTest.mockWebServerManager.mockWebServer.takeRequest()
        Assert.assertEquals("/getActivity", request1.path)
        Assert.assertNotNull(request1.getHeader("Authorization"))

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0, { it.dayActivity.size == 2 })
                .assertValueAt(0, { it.dayActivity[0].remoteId == 5110359589388288L /* From response */ })
                .assertValueAt(0, { it.dayActivity[1].remoteId == 5653294995210240L /* From response */ })
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_WhenEmptyDb_DataFromServerFor() {
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.RESPONSE_DIR_PATH
                        + "/get_activity_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(
                TimeUtils.getMidnightCal(1, 3, 2016).timeInMillis)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        //Verify the request
        val request1 = DiaryRepoImplHelperTest.mockWebServerManager.mockWebServer.takeRequest()
        Assert.assertEquals("/getActivity", request1.path)
        Assert.assertNotNull(request1.getHeader("Authorization"))

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount(0)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_WhenEmptyDb_ErrorServerFor() {
        DiaryRepoImplHelperTest.mockWebServerManager
                .enqueueResponse(File(DiaryRepoImplHelperTest.RESPONSE_DIR_PATH
                        + "/authentication_field_missing.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(
                TimeUtils.getMidnightCal(1, 3, 2016).timeInMillis)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        //Verify the request
        val request1 = DiaryRepoImplHelperTest.mockWebServerManager.mockWebServer.takeRequest()
        Assert.assertEquals("/getActivity", request1.path)
        Assert.assertNotNull(request1.getHeader("Authorization"))

        //Verify the response
        testSubscriber.assertNoErrors()
                .assertValueCount(0)
                .assertComplete()
    }

    @Test
    fun checkLoadDaysSummaryList_With1DayInDb() {
        DiaryRepoImplHelperTest.insert5EventsInEachDayFor1Day()

        DiaryRepoImplHelperTest.mockWebServerManager.enqueueResponse(File(DiaryRepoImplHelperTest.RESPONSE_DIR_PATH
                + "/get_activity_empty_response.json"))

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
                + "/get_activity_empty_response.json"))

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
                + "/get_activity_empty_response.json"))

        val testSubscriber = TestSubscriber<DailyActivitySummary>()
        DiaryRepoImplHelperTest.dairyRepoImpl.loadDaysSummaryList(DiaryRepoImplHelperTest.userActivityDao.tableItems.first().eventEndTimeMills)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoErrors()
                .assertValueCount((DiaryRepo.PAGE_SIZE + 4) / 2)
                .assertComplete()
    }
}
