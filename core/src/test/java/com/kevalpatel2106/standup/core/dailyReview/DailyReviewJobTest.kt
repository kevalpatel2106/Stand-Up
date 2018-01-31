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

package com.kevalpatel2106.standup.core.dailyReview

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.standup.core.CoreTestUtils
import com.kevalpatel2106.standup.core.activityMonitor.ActivityMonitorJob
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
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
class DailyReviewJobTest {

    @Before
    fun setUpTest() {
        JobManager.create(CoreTestUtils.createMockContext())
        JobManager.instance().cancelAll()
    }

    @Test
    fun checkScheduleJob_DailyReviewNotEnable() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val dailyAlarmTimeFrom12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) + 1800_000L

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong()))
                .thenReturn(dailyAlarmTimeFrom12Am)
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean()))
                .thenReturn(false)


        Assert.assertFalse(DailyReviewJob.scheduleJob(UserSettingsManager(sharedPrefsProvider)))
    }

    @Test
    fun checkScheduleJob_DailyReviewEnable() {
        scheduleJob()

        val set = JobManager.instance().getAllJobRequestsForTag(DailyReviewJob.DAILY_REVIEW_TAG)
        Assert.assertEquals(set.size, 1)

        val request = set.iterator().next()
        Assert.assertEquals(request.tag, DailyReviewJob.DAILY_REVIEW_TAG)
        Assert.assertEquals(request.backoffPolicy, JobRequest.BackoffPolicy.EXPONENTIAL)
        Assert.assertFalse(request.isPeriodic)
        Assert.assertTrue(request.isUpdateCurrent)
        Assert.assertTrue(request.isExact)
    }

    @Test
    fun checkCancelScheduleJob() {
        scheduleJob()
        ActivityMonitorJob.cancel()

        val set = JobManager.instance().getAllJobRequestsForTag(DailyReviewJob.DAILY_REVIEW_TAG)
        Assert.assertTrue(set.isEmpty())
    }

    private fun scheduleJob() {
        val sharedPrefsProvider = Mockito.mock(SharedPrefsProvider::class.java)
        val dailyAlarmTimeFrom12Am = TimeUtils.getMilliSecFrom12AM(System.currentTimeMillis()) + 1800_000L

        //Set the future day.
        Mockito.`when`(sharedPrefsProvider.getLongFromPreference(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong()))
                .thenReturn(dailyAlarmTimeFrom12Am)
        Mockito.`when`(sharedPrefsProvider.getBoolFromPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean()))
                .thenReturn(true)

        Assert.assertTrue(DailyReviewJob.scheduleJob(UserSettingsManager(sharedPrefsProvider)))
    }
}
