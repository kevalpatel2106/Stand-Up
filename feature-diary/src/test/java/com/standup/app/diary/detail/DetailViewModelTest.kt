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

package com.standup.app.diary.detail

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.common.misc.lottie.LottieJson
import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.kevalpatel2106.common.userActivity.repo.UserActivityRepoImpl
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.TimeUtils
import com.standup.app.billing.repo.MockBillingRepoImpl
import com.standup.app.diary.repo.DiaryRepo
import com.standup.app.diary.repo.DiaryRepoImpl
import com.standup.app.diary.repo.DiaryRepoImplHelperTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.io.File
import java.util.*

/**
 * Created by Kevalpatel2106 on 23-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DetailViewModelTest {

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

    private lateinit var model: DetailViewModel

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

        model = DetailViewModel(diaryRepo, billingRepo, mockApplication)

        //Premium member
        billingRepo.isError = false
        billingRepo.isPremiumPurchased = true

    }

    @Test
    @Throws(Exception::class)
    fun checkInit() {
        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.errorMessage.value)
        Assert.assertTrue(model.isPremiumUser.value!!)
        Assert.assertNull(model.summary.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkIsPremium_ProUser() {
        //Premium member
        billingRepo.isError = false
        billingRepo.isPremiumPurchased = true

        model.checkIsPremiumUser()

        Assert.assertTrue(model.isPremiumUser.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkIsPremium_NotProUser() {
        //Premium member
        billingRepo.isError = false
        billingRepo.isPremiumPurchased = false

        model.checkIsPremiumUser()

        Assert.assertFalse(model.isPremiumUser.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkIsPremium_Error() {
        //Premium member
        billingRepo.isError = true
        billingRepo.isPremiumPurchased = false

        model.checkIsPremiumUser()

        Assert.assertFalse(model.isPremiumUser.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkFetchData_WithEmptyData() {
        val calendar = Calendar.getInstance()

        model.fetchData(
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
                month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR)
        )

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.summary.value)

        Assert.assertNotNull(model.errorMessage.value)
        Assert.assertNotNull(model.errorMessage.value!!.errorImage, LottieJson.CLOUD_FLOATING)
        Assert.assertNotNull(model.errorMessage.value!!.getMessage(null))
    }

    @Test
    @Throws(Exception::class)
    fun checkFetchData_WithData() {
        insert2ActivitiesForToday()

        val calendar = Calendar.getInstance()

        model.fetchData(
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
                month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR)
        )

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.errorMessage.value)

        Assert.assertNotNull(model.summary.value)
        Assert.assertEquals(2, model.summary.value!!.dayActivity.size)
        Assert.assertEquals(calendar.get(Calendar.DAY_OF_MONTH), model.summary.value!!.dayOfMonth)
        Assert.assertEquals(calendar.get(Calendar.MONTH), model.summary.value!!.monthOfYear)
        Assert.assertEquals(calendar.get(Calendar.YEAR), model.summary.value!!.year)
    }

    private fun insert2ActivitiesForToday() {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = TimeUtils.todayMidnightCal()

        for (i in 2 downTo 1) {

            val startTime = cal.timeInMillis + if (i.rem(2) == 0) DiaryRepoImplHelperTest.DIFF_BETWEEN_END_AND_START else 0
            val endTime = startTime + DiaryRepoImplHelperTest.DIFF_BETWEEN_END_AND_START

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
