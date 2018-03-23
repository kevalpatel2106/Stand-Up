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

package com.standup.app.diary.list

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.common.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.common.userActivity.repo.UserActivityRepoImpl
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.standup.app.billing.repo.MockBillingRepoImpl
import com.standup.app.diary.repo.DiaryRepo
import com.standup.app.diary.repo.DiaryRepoImpl
import com.standup.app.diary.repo.DiaryRepoImplHelperTest
import junit.framework.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.io.File
import java.util.*


/**
 * Created by Kevalpatel2106 on 03-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(Enclosed::class)
class DiaryViewModelTest {


    @RunWith(JUnit4::class)
    class LoadDailySummaryListForProUserTest {
        @Rule
        @JvmField
        val rule = InstantTaskExecutorRule()

        @Rule
        @JvmField
        val rule1 = RxSchedulersOverrideRule()

        private lateinit var mockApplication: Application
        private val userActivityDao = UserActivityDaoMockImpl(ArrayList())
        private val mockServerManager = MockServerManager()

        private lateinit var diaryRepo: DiaryRepo
        private lateinit var billingRepo: MockBillingRepoImpl

        @Before
        fun setUp() {
            mockServerManager.startMockWebServer()

            mockServerManager.enqueueResponse(File(DiaryRepoImplHelperTest.mockWebServerManager.getResponsesPath()
                    + "/get_activity_empty_response.json"))

            val mockUserActivityRepo = UserActivityRepoImpl(
                    userActivityDao = userActivityDao,
                    retrofit = NetworkApi("test-user-id", "test-token")
                            .getRetrofitClient(mockServerManager.getBaseUrl())
            )

            mockApplication = Mockito.mock(Application::class.java)

            diaryRepo = DiaryRepoImpl(
                    userActivityRepo = mockUserActivityRepo,
                    userActivityDao = userActivityDao
            )
            billingRepo = MockBillingRepoImpl()

            //Premium member
            billingRepo.buyPremiumError = false
            billingRepo.isPremiumPurchased = true

        }

        @Test
        fun emptyDb() {
            val dairyViewModel = DiaryViewModel(mockApplication, diaryRepo, billingRepo)

            Assert.assertTrue(dairyViewModel.noMoreData.value!!)
            Assert.assertFalse(dairyViewModel.blockUi.value!!)
            Assert.assertTrue(dairyViewModel.activities.value!!.isEmpty())
        }

        @Test
        fun allActivitiesInSameMonth() {
            //Generate fake data
            DiaryViewModelTestUtils.insert2EventsInEachDayFor7DaysForSameMonth(userActivityDao)

            val dairyViewModel = DiaryViewModel(mockApplication, diaryRepo, billingRepo)

            Assert.assertTrue(dairyViewModel.noMoreData.value!!)
            Assert.assertFalse(dairyViewModel.blockUi.value!!)
            Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
            Assert.assertEquals(dairyViewModel.activities.value!!.size, 7 /* Summary */ + 1 /* Days header */)
        }

        @Test
        fun allActivitiesBetweenTwoMonth() {
            //Generate fake data
            DiaryViewModelTestUtils.insert2EventsInEachDayFor7DaysBetweenTwoMonth(userActivityDao)

            val dairyViewModel = DiaryViewModel(mockApplication, diaryRepo, billingRepo)

            Assert.assertTrue(dairyViewModel.noMoreData.value!!)
            Assert.assertFalse(dairyViewModel.blockUi.value!!)
            Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
            Assert.assertEquals(dairyViewModel.activities.value!!.size, 7 /* Summary */ + 2 /* Days header */)
        }

        @Test
        fun allActivitiesDifferentTwoMonth() {
            //Generate fake data
            DiaryViewModelTestUtils.insert2EventsInDifferentDaysOfDifferentMonths(userActivityDao = userActivityDao)

            val dairyViewModel = DiaryViewModel(mockApplication, diaryRepo, billingRepo)

            Assert.assertTrue(dairyViewModel.noMoreData.value!!)
            Assert.assertFalse(dairyViewModel.blockUi.value!!)
            Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
            Assert.assertEquals(dairyViewModel.activities.value!!.size, 7 /* Summary */ + 7 /* Days header */)
        }

        @Test
        fun withSecondPage() {
            //Generate fake data
            DiaryViewModelTestUtils.insert2EventsInDifferentDaysOfDifferentMonths(16, userActivityDao)

            //Load the first page
            val dairyViewModel = DiaryViewModel(mockApplication, diaryRepo, billingRepo)

            Assert.assertFalse(dairyViewModel.noMoreData.value!!)
            Assert.assertFalse(dairyViewModel.blockUi.value!!)
            Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
            Assert.assertEquals(dairyViewModel.activities.value!!.size, 10 /* Summary */ + 10 /* Days header */)

            //Load the next page
            val oldestTime = dairyViewModel.activities.value!!.last().dayActivity.first().eventStartTimeMills

            dairyViewModel.loadDailySummaryPage(oldestTime)
            Assert.assertTrue(dairyViewModel.noMoreData.value!!)
            Assert.assertFalse(dairyViewModel.blockUi.value!!)
            Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
            Assert.assertEquals(dairyViewModel.activities.value!!.size, 16 /* Summary */ + 16 /* Days header */)
        }
    }

    @RunWith(JUnit4::class)
    class LoadDailySummaryListForNonProUserTest {

        @Rule
        @JvmField
        val rule = InstantTaskExecutorRule()

        @Rule
        @JvmField
        val rule1 = RxSchedulersOverrideRule()

        private lateinit var mockApplication: Application
        private val userActivityDao = UserActivityDaoMockImpl(ArrayList())
        private val mockServerManager = MockServerManager()

        private lateinit var diaryRepo: DiaryRepo
        private lateinit var billingRepo: MockBillingRepoImpl

        @Before
        fun setUp() {
            mockServerManager.startMockWebServer()

            mockServerManager.enqueueResponse(File(DiaryRepoImplHelperTest.mockWebServerManager.getResponsesPath()
                    + "/get_activity_empty_response.json"))

            val mockUserActivityRepo = UserActivityRepoImpl(
                    userActivityDao = userActivityDao,
                    retrofit = NetworkApi("test-user-id", "test-token")
                            .getRetrofitClient(mockServerManager.getBaseUrl())
            )

            mockApplication = Mockito.mock(Application::class.java)

            diaryRepo = DiaryRepoImpl(
                    userActivityRepo = mockUserActivityRepo,
                    userActivityDao = userActivityDao
            )
            billingRepo = MockBillingRepoImpl()

            //Non premium user
            billingRepo.buyPremiumError = false
            billingRepo.isPremiumPurchased = false
        }

        @Test
        fun checkLoadNextPageWithEmptyDb() {
            val dairyViewModel = DiaryViewModel(mockApplication, diaryRepo, billingRepo)

            Assert.assertTrue(dairyViewModel.noMoreData.value!!)
            Assert.assertFalse(dairyViewModel.blockUi.value!!)
            Assert.assertFalse(dairyViewModel.moreItemsBlocked.value!!)
            Assert.assertTrue(dairyViewModel.activities.value!!.isEmpty())
            Assert.assertTrue(dairyViewModel.moreItemsBlocked.value!!)
        }
//
//        @Test
//        fun checkLoadNextPageWithAllActivitiesInSameMonth() {
//            //Generate fake data
//            DiaryViewModelTestUtils.insert2EventsInEachDayFor7DaysForSameMonth(userActivityDao)
//
//            val dairyViewModel = DiaryViewModel(mockApplication, diaryRepo, billingRepo)
//
//            Assert.assertTrue(dairyViewModel.noMoreData.value!!)
//            Assert.assertFalse(dairyViewModel.blockUi.value!!)
//            Assert.assertFalse(dairyViewModel.moreItemsBlocked.value!!)
//            Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
//            Assert.assertEquals(dairyViewModel.activities.value!!.size, 7 /* Summary */ + 1 /* Days header */)
//        }
//
//        @Test
//        fun checkLoadNextPageWithAllActivitiesBetweenTwoMonth() {
//            //Generate fake data
//            DiaryViewModelTestUtils.insert2EventsInEachDayFor7DaysBetweenTwoMonth(userActivityDao)
//
//            val dairyViewModel = DiaryViewModel(mockApplication, diaryRepo, billingRepo)
//
//            Assert.assertTrue(dairyViewModel.noMoreData.value!!)
//            Assert.assertFalse(dairyViewModel.blockUi.value!!)
//            Assert.assertFalse(dairyViewModel.moreItemsBlocked.value!!)
//            Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
//            Assert.assertEquals(dairyViewModel.activities.value!!.size, 7 /* Summary */ + 2 /* Days header */)
//        }
//
//        @Test
//        fun checkLoadNextPageWithAllActivitiesDifferentTwoMonth() {
//            //Generate fake data
//            DiaryViewModelTestUtils.insert2EventsInDifferentDaysOfDifferentMonths(userActivityDao = userActivityDao)
//
//            val dairyViewModel = DiaryViewModel(mockApplication, diaryRepo, billingRepo)
//
//            Assert.assertTrue(dairyViewModel.noMoreData.value!!)
//            Assert.assertFalse(dairyViewModel.blockUi.value!!)
//            Assert.assertFalse(dairyViewModel.moreItemsBlocked.value!!)
//            Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
//            Assert.assertEquals(dairyViewModel.activities.value!!.size, 7 /* Summary */ + 7 /* Days header */)
//        }
//
//        @Test
//        fun checkLoadNextPage_WithSecondPage() {
//            //Generate fake data
//            DiaryViewModelTestUtils.insert2EventsInDifferentDaysOfDifferentMonths(16, userActivityDao)
//
//            //Load the first page
//            val dairyViewModel = DiaryViewModel(mockApplication, diaryRepo, billingRepo)
//
//            Assert.assertFalse(dairyViewModel.noMoreData.value!!)
//            Assert.assertFalse(dairyViewModel.blockUi.value!!)
//            Assert.assertFalse(dairyViewModel.moreItemsBlocked.value!!)
//            Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
//            Assert.assertEquals(dairyViewModel.activities.value!!.size, 10 /* Summary */ + 10 /* Days header */)
//
//            //Load the next page
//            val oldestTime = dairyViewModel.activities.value!!.last().dayActivity.first().eventStartTimeMills
//
//            dairyViewModel.loadDailySummaryPage(oldestTime)
//            Assert.assertTrue(dairyViewModel.noMoreData.value!!)
//            Assert.assertFalse(dairyViewModel.blockUi.value!!)
//            Assert.assertTrue(dairyViewModel.moreItemsBlocked.value!!)
//            Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
//        }
    }
}
