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

package com.standup.core

import com.standup.core.activityMonitor.ActivityMonitorJob
import com.standup.core.dailyReview.DailyReviewJob
import com.standup.core.dndManager.AutoDndMonitoringJob
import com.standup.core.misc.CoreJobCreator
import com.standup.core.reminder.NotificationSchedulerJob
import com.standup.core.sleepManager.SleepModeMonitoringJob
import com.standup.core.sync.SyncJob
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 01-Feb-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class CoreJobCreatorTest {

    @Test
    fun checkCreateJob_InvalidTag() {
        try {
            CoreJobCreator().create("invalid_tag")
            Assert.fail()
        } catch (e: IllegalStateException) {
            //Test passed
        }
    }


    @Test
    fun checkCreateJob_ActivityMonitorTag() {
        try {
            assertTrue(CoreJobCreator().create(ActivityMonitorJob.ACTIVITY_MONITOR_JOB_TAG) is ActivityMonitorJob)
        } catch (e: IllegalStateException) {
            Assert.fail()
        }
    }

    @Test
    fun checkCreateJob_ReminderNotificationTag() {
        try {
            assertTrue(CoreJobCreator().create(NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG)
                    is NotificationSchedulerJob)
        } catch (e: IllegalStateException) {
            Assert.fail()
        }
    }

    @Test
    fun checkCreateJob_AutoDndStartTag() {
        try {
            assertTrue(CoreJobCreator().create(AutoDndMonitoringJob.AUTO_DND_START_JOB_TAG)
                    is AutoDndMonitoringJob)
        } catch (e: IllegalStateException) {
            Assert.fail()
        }
    }

    @Test
    fun checkCreateJob_AutoDndEndTag() {
        try {
            assertTrue(CoreJobCreator().create(AutoDndMonitoringJob.AUTO_DND_END_JOB_TAG)
                    is AutoDndMonitoringJob)
        } catch (e: IllegalStateException) {
            Assert.fail()
        }
    }

    @Test
    fun checkCreateJob_SleepStartTag() {
        try {
            assertTrue(CoreJobCreator().create(SleepModeMonitoringJob.SLEEP_MODE_START_JOB_TAG)
                    is SleepModeMonitoringJob)
        } catch (e: IllegalStateException) {
            Assert.fail()
        }
    }

    @Test
    fun checkCreateJob_SleepEndTag() {
        try {
            assertTrue(CoreJobCreator().create(SleepModeMonitoringJob.SLEEP_MODE_END_JOB_TAG)
                    is SleepModeMonitoringJob)
        } catch (e: IllegalStateException) {
            Assert.fail()
        }
    }

    @Test
    fun checkCreateJob_SyncTag() {
        try {
            assertTrue(CoreJobCreator().create(SyncJob.SYNC_JOB_TAG) is SyncJob)
        } catch (e: IllegalStateException) {
            Assert.fail()
        }
    }

    @Test
    fun checkCreateJob_SyncNowTag() {
        try {
            assertTrue(CoreJobCreator().create(SyncJob.SYNC_NOW_JOB_TAG) is SyncJob)
        } catch (e: IllegalStateException) {
            Assert.fail()
        }
    }

    @Test
    fun checkCreateJob_DailyReviewTag() {
        try {
            assertTrue(CoreJobCreator().create(DailyReviewJob.DAILY_REVIEW_TAG)
                    is DailyReviewJob)
        } catch (e: IllegalStateException) {
            Assert.fail()
        }
    }
}
