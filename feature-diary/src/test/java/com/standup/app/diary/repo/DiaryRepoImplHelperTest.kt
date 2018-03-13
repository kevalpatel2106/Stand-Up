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
import com.kevalpatel2106.common.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.TimeUtils
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import java.nio.file.Paths
import java.util.*

/**
 * Created by Keval on 02/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(Enclosed::class)
internal object DiaryRepoImplHelperTest {

    private val path = Paths.get("").toAbsolutePath().toString().let {
        return@let if (it.endsWith("feature-diary")) it else it.plus("/feature-diary")
    }
    val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/standup/app/diary/repo", path)

    lateinit var dairyRepoImpl: DiaryRepoImpl
    lateinit var userActivityDao: UserActivityDaoMockImpl
    val mockWebServerManager = MockServerManager()

    val DIFF_BETWEEN_END_AND_START = 60000L   //60 Sec

    fun setUpForAllTests() {
        //Mock network set
        mockWebServerManager.startMockWebServer()

        //Mock database table
        userActivityDao = UserActivityDaoMockImpl(ArrayList())

        dairyRepoImpl = DiaryRepoImpl(
                NetworkApi().getRetrofitClient(mockWebServerManager.getBaseUrl()),
                userActivityDao
        )
    }

    fun tearUpForAllTests() {
        mockWebServerManager.close()
    }

    fun insert2EventsInEachDayFor7Days() {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = TimeUtils.todayMidnightCal()

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

    fun insert5EventsInEachDayFor1Day() {
        //Set fake db items so that we have all the 5 activities in one single day
        val cal = TimeUtils.todayMidnightCal()

        for (i in 15 downTo 1) {
            val startTime = cal.timeInMillis + (i * DIFF_BETWEEN_END_AND_START)
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

    fun insertOneEventsInEachDayFor15Days() {
        //Set fake db items so that we have at least one user activity with past 10 days.
        val cal = TimeUtils.todayMidnightCal()

        for (i in DiaryRepo.PAGE_SIZE + 5 downTo 1) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
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
