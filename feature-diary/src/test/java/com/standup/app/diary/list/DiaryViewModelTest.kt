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

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.network.NetworkModule
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.standup.app.diary.repo.DiaryRepo
import com.standup.app.diary.repo.DiaryRepoImpl
import junit.framework.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*


/**
 * Created by Kevalpatel2106 on 03-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DiaryViewModelTest {
    private val DIFF_BETWEEN_END_AND_START = 60000L   //60 Sec

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rule1 = RxSchedulersOverrideRule()

    private val userActivityDao = UserActivityDaoMockImpl(ArrayList())
    private val mockServerManager = MockServerManager()
    private lateinit var diaryRepo: DiaryRepo

    @Before
    fun setUp() {
        mockServerManager.startMockWebServer()
        diaryRepo = DiaryRepoImpl(
                NetworkModule().getRetrofitClient(mockServerManager.getBaseUrl()),
                userActivityDao
        )
    }

    @Test
    fun checkLoadNextPageWithEmptyDb() {

        val dairyViewModel = DiaryViewModel(diaryRepo)

        Assert.assertTrue(dairyViewModel.noMoreData.value!!)
        Assert.assertFalse(dairyViewModel.blockUi.value!!)
        Assert.assertTrue(dairyViewModel.activities.value!!.isEmpty())
    }

    @Test
    fun checkLoadNextPageWithAllActivitiesInSameMonth() {
        //Generate fake data
        insert2EventsInEachDayFor7DaysForSameMonth()

        val dairyViewModel = DiaryViewModel(diaryRepo)

        Assert.assertTrue(dairyViewModel.noMoreData.value!!)
        Assert.assertFalse(dairyViewModel.blockUi.value!!)
        Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
        Assert.assertEquals(dairyViewModel.activities.value!!.size, 7 /* Summary */ + 1 /* Days header */)
    }

    @Test
    fun checkLoadNextPageWithAllActivitiesBetweenTwoMonth() {
        //Generate fake data
        insert2EventsInEachDayFor7DaysBetweenTwoMonth()

        val dairyViewModel = DiaryViewModel(diaryRepo)

        Assert.assertTrue(dairyViewModel.noMoreData.value!!)
        Assert.assertFalse(dairyViewModel.blockUi.value!!)
        Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
        Assert.assertEquals(dairyViewModel.activities.value!!.size, 7 /* Summary */ + 2 /* Days header */)
    }

    @Test
    fun checkLoadNextPageWithAllActivitiesDifferentTwoMonth() {
        //Generate fake data
        insert2EventsInDifferentDaysOfDifferentMonths()

        val dairyViewModel = DiaryViewModel(diaryRepo)

        Assert.assertTrue(dairyViewModel.noMoreData.value!!)
        Assert.assertFalse(dairyViewModel.blockUi.value!!)
        Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
        Assert.assertEquals(dairyViewModel.activities.value!!.size, 7 /* Summary */ + 7 /* Days header */)
    }

    @Test
    fun checkLoadNextPageWithSecondPage() {
        //Generate fake data
        insert2EventsInDifferentDaysOfDifferentMonths(16)

        //Load the first page
        val dairyViewModel = DiaryViewModel(diaryRepo)

        Assert.assertFalse(dairyViewModel.noMoreData.value!!)
        Assert.assertFalse(dairyViewModel.blockUi.value!!)
        Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
        Assert.assertEquals(dairyViewModel.activities.value!!.size, 10 /* Summary */ + 10 /* Days header */)

        //Load the next page
        val oldestTime = dairyViewModel.activities.value!!.last().dayActivity.first().eventStartTimeMills

        dairyViewModel.loadNext(oldestTime)
        Assert.assertTrue(dairyViewModel.noMoreData.value!!)
        Assert.assertFalse(dairyViewModel.blockUi.value!!)
        Assert.assertFalse(dairyViewModel.activities.value!!.isEmpty())
        Assert.assertEquals(dairyViewModel.activities.value!!.size, 16 /* Summary */ + 16 /* Days header */)
    }

    private fun insert2EventsInEachDayFor7DaysForSameMonth() {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = Calendar.getInstance()
        //Convert date to 30 dec 2017
        //We pick this date because as we go past dates in the loop we won't change the month.
        cal.set(Calendar.DAY_OF_MONTH, 30)
        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        for (i in DiaryRepo.PAGE_SIZE + 4 downTo 1) {

            if (i.rem(2) == 0) cal.add(Calendar.DAY_OF_MONTH, -1)

            val startTime = cal.timeInMillis + if (i.rem(2) == 0) DIFF_BETWEEN_END_AND_START else 0
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

    private fun insert2EventsInEachDayFor7DaysBetweenTwoMonth() {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = Calendar.getInstance()
        //Convert date to 4 dec 2017
        //We pick this date because as we go past 7 dates the last 3 events will be in previous month.
        cal.set(Calendar.DAY_OF_MONTH, 4)
        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        for (i in DiaryRepo.PAGE_SIZE + 4 downTo 1) {

            if (i.rem(2) == 0) cal.add(Calendar.DAY_OF_MONTH, -1)

            val startTime = cal.timeInMillis + if (i.rem(2) == 0) DIFF_BETWEEN_END_AND_START else 0
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

    private fun insert2EventsInDifferentDaysOfDifferentMonths(totalDays: Int = 7) {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = Calendar.getInstance()
        //Convert date to 30 dec 2017. No specific reason
        cal.set(Calendar.DAY_OF_MONTH, 30)
        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.YEAR, 2017)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        for (i in totalDays * 2 downTo 1) {

            if (i.rem(2) == 0) {
                cal.add(Calendar.MONTH, -1)
                cal.add(Calendar.DAY_OF_MONTH, -1)
            }

            val startTime = cal.timeInMillis + if (i.rem(2) == 0) DIFF_BETWEEN_END_AND_START else 0
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
