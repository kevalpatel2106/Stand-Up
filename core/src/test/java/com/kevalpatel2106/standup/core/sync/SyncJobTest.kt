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

package com.kevalpatel2106.standup.core.sync

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.standup.core.CoreTestUtils
import com.kevalpatel2106.standup.core.dailyReview.DailyReviewJob
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by Kevalpatel2106 on 01-Feb-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SyncJobTest {


    @Before
    fun setUpTest() {
        JobManager.create(CoreTestUtils.createMockContext())
        JobManager.instance().cancelAll()
    }

    @Test
    fun checkSyncNow() {
        SyncJob.syncNow()

        val set = JobManager.instance().getAllJobRequestsForTag(SyncJob.SYNC_NOW_JOB_TAG)
        Assert.assertEquals(1, set.size)

        val request = set.iterator().next()
        Assert.assertEquals(request.tag, SyncJob.SYNC_NOW_JOB_TAG)
        Assert.assertEquals(request.backoffPolicy, JobRequest.BackoffPolicy.EXPONENTIAL)
        Assert.assertFalse(request.isPeriodic)
        Assert.assertTrue(request.isUpdateCurrent)
        Assert.assertEquals(request.intervalMs, 0)
        Assert.assertEquals(request.startMs, 1)
        Assert.assertEquals(request.endMs, 1)
        Assert.assertFalse(request.requiresCharging())
        Assert.assertFalse(request.requiresBatteryNotLow())
        Assert.assertFalse(request.requiresDeviceIdle())
        Assert.assertFalse(request.requiresStorageNotLow())
        Assert.assertEquals(request.requiredNetworkType(), JobRequest.NetworkType.ANY)
        Assert.assertTrue(request.isExact)
    }

    @Test
    fun checkScheduleSyncNow() {
        val intervalMills = 1800_000L //30 mins
        SyncJob.scheduleSync(intervalMills)

        val set = JobManager.instance().getAllJobRequestsForTag(SyncJob.SYNC_JOB_TAG)
        Assert.assertEquals(1, set.size)

        val request = set.iterator().next()
        Assert.assertEquals(request.tag, SyncJob.SYNC_JOB_TAG)
        Assert.assertEquals(request.backoffPolicy, JobRequest.BackoffPolicy.EXPONENTIAL)
        Assert.assertTrue(request.isPeriodic)
        Assert.assertTrue(request.isUpdateCurrent)
        Assert.assertEquals(request.requiredNetworkType(), JobRequest.NetworkType.CONNECTED)
        Assert.assertFalse(request.requiresCharging())
        Assert.assertFalse(request.requiresBatteryNotLow())
        Assert.assertFalse(request.requiresDeviceIdle())
        Assert.assertFalse(request.requiresStorageNotLow())
        Assert.assertEquals(request.intervalMs, intervalMills)
        Assert.assertFalse(request.isExact)
    }

    @Test
    fun checkCancelScheduledSync() {
        val intervalMills = 1800_000L //30 mins
        SyncJob.scheduleSync(intervalMills)
        SyncJob.cancelScheduledSync()

        val set = JobManager.instance().getAllJobRequestsForTag(SyncJob.SYNC_JOB_TAG)
        Assert.assertTrue(set.isEmpty())
    }

    @Test
    fun checkCancelScheduledSync_WithSyncNowRunning() {
        val intervalMills = 1800_000L //30 mins
        SyncJob.scheduleSync(intervalMills)
        SyncJob.syncNow()
        SyncJob.cancelScheduledSync()

        var set = JobManager.instance().getAllJobRequestsForTag(SyncJob.SYNC_JOB_TAG)
        Assert.assertTrue(set.isEmpty())

        set = JobManager.instance().getAllJobRequestsForTag(SyncJob.SYNC_NOW_JOB_TAG)
        Assert.assertEquals(1, set.size)
    }
}
