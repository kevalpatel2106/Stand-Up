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

import android.app.Application
import com.kevalpatel2106.common.prefs.SharedPreferenceKeys
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import com.standup.app.dashboard.R
import com.standup.core.CorePrefsProvider
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Kevalpatel2106 on 04-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DashboardRepoImplTest {
    private val DIFF_BETWEEN_END_AND_START: Int = 30000
    private val TEXT_DND_RUNNING = "test_dnd_running"
    private val TEXT_SLEEP_RUNNING = "test_sleep_mode_running"
    private val TEXT_NEXT_REMINDER = "test_sleep_mode_running %1\$s."

    private val mockSharedPrefsProvider = SharedPrefsProvider(MockSharedPreference())
    private lateinit var mockUserActivityDao: UserActivityDaoMockImpl
    private lateinit var mockUserSettingsManager: UserSettingsManager
    private lateinit var mockApplication: Application
    private lateinit var mockCorePrefsProvider: CorePrefsProvider
    private val mockServerManager = MockServerManager()

    private lateinit var dashboardRepo: DashboardRepo

    @Before
    fun setUp() {
        mockApplication = Mockito.mock(Application::class.java)
        Mockito.`when`(mockApplication.getString(R.string.dnd_mode_is_enabled))
                .thenReturn(TEXT_DND_RUNNING)
        Mockito.`when`(mockApplication.getString(R.string.sleep_mode_is_enabled))
                .thenReturn(TEXT_SLEEP_RUNNING)
        Mockito.`when`(mockApplication.getString(R.string.next_notification_time))
                .thenReturn(TEXT_NEXT_REMINDER)

        mockUserActivityDao = UserActivityDaoMockImpl(ArrayList())
        mockCorePrefsProvider = CorePrefsProvider(mockSharedPrefsProvider)
        mockUserSettingsManager = UserSettingsManager(mockSharedPrefsProvider)
        mockServerManager.startMockWebServer()

        dashboardRepo = DashboardRepoImpl(
                application = mockApplication,
                userSettingsManager = mockUserSettingsManager,
                userActivityDao = mockUserActivityDao,
                corePrefsProvider = mockCorePrefsProvider
        )
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    fun checkGetNextReminderStatus_InAutoDndMode() {
        val timeFrom12Am = System.currentTimeMillis() - TimeUtils.getMidnightCal(System.currentTimeMillis(), false).timeInMillis

        //Dnd mode start
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE, true)
        mockUserSettingsManager.setAutoDndTime(
                startTimeMillsFrom12Am = timeFrom12Am - 10_000,
                endTimeMillsFrom12Am = timeFrom12Am + 10_000
        )
        //Sleep mode off
        mockUserSettingsManager.setSleepTime(
                startTimeMillsFrom12Am = timeFrom12Am - 10_000,
                endTimeMillsFrom12Am = timeFrom12Am - 5_000
        )

        Assert.assertEquals(TEXT_DND_RUNNING, dashboardRepo.getNextReminderStatus())
    }

    @Test
    fun checkGetNextReminderStatus_InForceDndMode() {
        val timeFrom12Am = System.currentTimeMillis() - TimeUtils.getMidnightCal(System.currentTimeMillis(), false).timeInMillis

        //Dnd mode start
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_FORCE_DND_ENABLE, true)
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE, false)
        mockUserSettingsManager.setAutoDndTime(
                startTimeMillsFrom12Am = timeFrom12Am - 10_000,
                endTimeMillsFrom12Am = timeFrom12Am - 5_000
        )
        //Sleep mode off
        mockUserSettingsManager.setSleepTime(
                startTimeMillsFrom12Am = timeFrom12Am - 10_000,
                endTimeMillsFrom12Am = timeFrom12Am - 5_000
        )

        Assert.assertEquals(dashboardRepo.getNextReminderStatus(), TEXT_DND_RUNNING)
    }

    @Test
    fun checkGetNextReminderStatus_InSleepMode() {
        val timeFrom12Am = System.currentTimeMillis() - TimeUtils.getMidnightCal(System.currentTimeMillis(), false).timeInMillis

        //Dnd mode start
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_FORCE_DND_ENABLE, false)
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE, false)
        mockUserSettingsManager.setAutoDndTime(
                startTimeMillsFrom12Am = timeFrom12Am - 10_000,
                endTimeMillsFrom12Am = timeFrom12Am - 5_000
        )
        //Sleep mode off
        mockUserSettingsManager.setSleepTime(
                startTimeMillsFrom12Am = timeFrom12Am - 10_000,
                endTimeMillsFrom12Am = timeFrom12Am + 10_000
        )

        Assert.assertEquals(dashboardRepo.getNextReminderStatus(), TEXT_SLEEP_RUNNING)
    }

    @Test
    fun checkGetNextReminderStatus_WithNextNotificationTime() {
        val nextNotificationTime = System.currentTimeMillis()

        //Dnd mode start
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_FORCE_DND_ENABLE, false)
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE, false)
        mockSharedPrefsProvider.savePreferences(CorePrefsProvider.PREF_KEY_NEXT_NOTIFICATION_TIME, nextNotificationTime)
        mockUserSettingsManager.setAutoDndTime(
                startTimeMillsFrom12Am = TimeUtils.currentMillsFromMidnight() - 10_000,
                endTimeMillsFrom12Am = TimeUtils.currentMillsFromMidnight() - 5_000
        )
        //Sleep mode off
        mockUserSettingsManager.setSleepTime(
                startTimeMillsFrom12Am = TimeUtils.currentMillsFromMidnight() - 10_000,
                endTimeMillsFrom12Am = TimeUtils.currentMillsFromMidnight() - 10_000
        )

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        Assert.assertEquals(dashboardRepo.getNextReminderStatus(),
                String.format(TEXT_NEXT_REMINDER, sdf.format(nextNotificationTime)))
    }

    @Test
    fun checkGetNextReminderStatus_WithNoNextNotificationTime() {
        //Dnd mode start
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_FORCE_DND_ENABLE, false)
        mockSharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_IS_AUTO_DND_ENABLE, false)
        mockUserSettingsManager.setAutoDndTime(
                startTimeMillsFrom12Am = TimeUtils.currentMillsFromMidnight() - 10_000,
                endTimeMillsFrom12Am = TimeUtils.currentMillsFromMidnight() - 5_000
        )
        //Sleep mode off
        mockUserSettingsManager.setSleepTime(
                startTimeMillsFrom12Am = TimeUtils.currentMillsFromMidnight() - 10_000,
                endTimeMillsFrom12Am = TimeUtils.currentMillsFromMidnight() - 10_000
        )

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        Assert.assertEquals(dashboardRepo.getNextReminderStatus(),
                String.format(TEXT_NEXT_REMINDER, sdf.format(0)))
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
                    it.dayActivity.size == mockUserActivityDao.tableItems.size
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
        val cal = TimeUtils.todayMidnightCal()

        for (i in 10 downTo 1) {
            cal.add(Calendar.HOUR_OF_DAY, 1)

            val startTime = cal.timeInMillis
            val endTime = startTime + DIFF_BETWEEN_END_AND_START

            mockUserActivityDao.insert(UserActivity(eventStartTimeMills = startTime,
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
