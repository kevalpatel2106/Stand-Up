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

package com.standup.core.reminder

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.core.CoreConfig
import com.standup.core.CorePrefsProvider
import com.standup.core.CoreTestUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by Kevalpatel2106 on 05-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ReminderScheduledTest {
    @Before
    fun setUpTest() {
        JobManager.create(CoreTestUtils.createMockContext())
        JobManager.instance().cancelAll()
    }

    @Test
    fun checkScheduleJobDefaultInterval() {
        NotificationSchedulerJob.scheduleNotification(SharedPrefsProvider(MockSharedPreference()))

        val set = JobManager.instance().getAllJobRequestsForTag(NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG)
        Assert.assertEquals(set.size, 1)

        val request = set.iterator().next()
        Assert.assertEquals(request.tag, NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG)
        Assert.assertEquals(request.intervalMs, CoreConfig.STAND_UP_REMINDER_INTERVAL)
        Assert.assertEquals(request.backoffPolicy, JobRequest.BackoffPolicy.EXPONENTIAL)
        Assert.assertTrue(request.isPeriodic)
        Assert.assertTrue(request.isUpdateCurrent)
        Assert.assertFalse(request.isExact)
    }

    @Test
    fun checkScheduleJobDefault5SecInterval() {
        NotificationSchedulerJob.scheduleNotification(SharedPrefsProvider(MockSharedPreference()), 900_000L)

        val set = JobManager.instance().getAllJobRequestsForTag(NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG)
        Assert.assertEquals(set.size, 1)

        val request = set.iterator().next()
        Assert.assertEquals(request.tag, NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG)
        Assert.assertEquals(request.intervalMs, 900_000L)
        Assert.assertEquals(request.backoffPolicy, JobRequest.BackoffPolicy.EXPONENTIAL)
        Assert.assertTrue(request.isPeriodic)
        Assert.assertTrue(request.isUpdateCurrent)
        Assert.assertFalse(request.isExact)
    }

    @Test
    fun checkScheduleJobNextNotificationTime() {
        val sharedPref = MockSharedPreference()
        NotificationSchedulerJob.scheduleNotification(SharedPrefsProvider(sharedPref), 900_000L)

        Assert.assertTrue((sharedPref.getLong(CorePrefsProvider.PREF_KEY_NEXT_NOTIFICATION_TIME, 0)
                - System.currentTimeMillis()) - 900_000L <= 30_000L)
    }

    @Test
    fun checkScheduleJobNextNotificationTimeAfterCancel() {
        val sharedPref = MockSharedPreference()
        NotificationSchedulerJob.scheduleNotification(SharedPrefsProvider(sharedPref), 900_000L)

        NotificationSchedulerJob.cancelJob(SharedPrefsProvider(sharedPref))

        Assert.assertEquals(sharedPref.getLong(CorePrefsProvider.PREF_KEY_NEXT_NOTIFICATION_TIME, 0), 0)
    }

    @Test
    fun checkCancelScheduleJob() {
        NotificationSchedulerJob.scheduleNotification(SharedPrefsProvider(MockSharedPreference()))

        NotificationSchedulerJob.cancelJob(SharedPrefsProvider(MockSharedPreference()))

        val set = JobManager.instance().getAllJobRequestsForTag(NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG)
        Assert.assertTrue(set.isEmpty())
    }
}
