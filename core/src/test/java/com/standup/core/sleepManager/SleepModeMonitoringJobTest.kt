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

package com.standup.core.sleepManager

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.kevalpatel2106.common.prefs.SharedPreferenceKeys
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import com.standup.core.CoreTestUtils
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.startsWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by Kevalpatel2106 on 31-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SleepModeMonitoringJobTest {

    @Before
    fun setUpTest() {
        JobManager.create(CoreTestUtils.createMockContext())
        JobManager.instance().cancelAll()
    }

    @Test
    fun checkScheduleJob() {
        scheduleJob()

        //Check start job
        val startJobSet = JobManager.instance().getAllJobRequestsForTag(SleepModeMonitoringJob.SLEEP_MODE_START_JOB_TAG)
        Assert.assertEquals(startJobSet.size, 1)

        var request = startJobSet.iterator().next()
        Assert.assertEquals(request.tag, SleepModeMonitoringJob.SLEEP_MODE_START_JOB_TAG)
        Assert.assertEquals(request.backoffPolicy, JobRequest.BackoffPolicy.EXPONENTIAL)
        Assert.assertFalse(request.isPeriodic)
        Assert.assertTrue(request.isUpdateCurrent)
        Assert.assertTrue(request.isExact)

        //Check end job
        val endJobSet = JobManager.instance().getAllJobRequestsForTag(SleepModeMonitoringJob.SLEEP_MODE_END_JOB_TAG)
        Assert.assertEquals(startJobSet.size, 1)

        request = endJobSet.iterator().next()
        Assert.assertEquals(request.tag, SleepModeMonitoringJob.SLEEP_MODE_END_JOB_TAG)
        Assert.assertEquals(request.backoffPolicy, JobRequest.BackoffPolicy.EXPONENTIAL)
        Assert.assertFalse(request.isPeriodic)
        Assert.assertTrue(request.isUpdateCurrent)
        Assert.assertTrue(request.isExact)
    }

    @Test
    fun checkCancelScheduleJob() {
        scheduleJob()

        SleepModeMonitoringJob.cancelScheduledJob()

        var set = JobManager.instance().getAllJobRequestsForTag(SleepModeMonitoringJob.SLEEP_MODE_START_JOB_TAG)
        Assert.assertTrue(set.isEmpty())

        set = JobManager.instance().getAllJobRequestsForTag(SleepModeMonitoringJob.SLEEP_MODE_END_JOB_TAG)
        Assert.assertTrue(set.isEmpty())
    }

    private fun scheduleJob() {
        val sleepStartTime12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) + 900_000L
        val sleepEndTime12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) + 1800_000L
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)

        //Set dnd enable
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(startsWith(SharedPreferenceKeys.PREF_KEY_AUTO_DND_START_TIME_FROM_12AM), anyLong()))
                .thenReturn(sleepStartTime12Am)
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(startsWith(SharedPreferenceKeys.PREF_KEY_AUTO_DND_END_TIME_FROM_12AM), anyLong()))
                .thenReturn(sleepEndTime12Am)

        assertTrue(SleepModeMonitoringJob.scheduleJob(UserSettingsManager(sharedPrefsProvider)))
    }
}
