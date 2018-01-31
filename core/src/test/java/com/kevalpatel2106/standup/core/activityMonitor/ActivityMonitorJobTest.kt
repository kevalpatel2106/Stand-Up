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

package com.kevalpatel2106.standup.core.activityMonitor

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.kevalpatel2106.standup.core.CoreConfig
import com.kevalpatel2106.standup.core.CoreTestUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by Kevalpatel2106 on 31-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ActivityMonitorJobTest {

    @Before
    fun setUpTest() {
        JobManager.create(CoreTestUtils.createMockContext())
        JobManager.instance().cancelAll()
    }

    @Test
    fun checkScheduleJob() {
        Assert.assertTrue(ActivityMonitorJob.scheduleNextJob())

        val set = JobManager.instance().getAllJobRequestsForTag(ActivityMonitorJob.ACTIVITY_MONITOR_JOB_TAG)
        Assert.assertEquals(set.size, 1)

        val request = set.iterator().next()
        Assert.assertEquals(request.tag, ActivityMonitorJob.ACTIVITY_MONITOR_JOB_TAG)
        Assert.assertEquals(request.startMs, CoreConfig.MONITOR_SERVICE_PERIOD)
        Assert.assertEquals(request.endMs, CoreConfig.MONITOR_SERVICE_PERIOD)
        Assert.assertEquals(request.backoffPolicy, JobRequest.BackoffPolicy.EXPONENTIAL)
        Assert.assertFalse(request.isPeriodic)
        Assert.assertTrue(request.isUpdateCurrent)
        Assert.assertTrue(request.isExact)
    }

    @Test
    fun checkCancelScheduleJob() {
        Assert.assertTrue(ActivityMonitorJob.scheduleNextJob())

        ActivityMonitorJob.cancel()

        val set = JobManager.instance().getAllJobRequestsForTag(ActivityMonitorJob.ACTIVITY_MONITOR_JOB_TAG)
        Assert.assertTrue(set.isEmpty())
    }

}
