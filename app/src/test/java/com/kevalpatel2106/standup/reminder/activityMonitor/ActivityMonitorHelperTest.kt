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

package com.kevalpatel2106.standup.reminder.activityMonitor

import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.RetryStrategy
import com.kevalpatel2106.standup.reminder.ReminderConfig
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 05-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ActivityMonitorHelperTest {

    @Test
    @Throws(IOException::class)
    fun checkPrepareJob() {
        val builder = ActivityMonitorHelper.prepareJob(RuntimeEnvironment.application)

        Assert.assertEquals(builder.constraints.size, 0)
        Assert.assertTrue(builder.isRecurring)
        Assert.assertEquals(builder.tag, ActivityMonitorHelper.ACTIVITY_MONITOR_JOB_TAG)
        Assert.assertEquals(builder.retryStrategy, RetryStrategy.DEFAULT_LINEAR)
        Assert.assertEquals(builder.lifetime, Lifetime.UNTIL_NEXT_BOOT)
        Assert.assertEquals(builder.service, ActivityMonitorService::class.java.canonicalName)
    }

    @Test
    @Throws(IOException::class)
    fun checkGetExecutionWindow() {
        val executionWindow = ActivityMonitorHelper.getExecutionWindow()

        Assert.assertEquals(ReminderConfig.MONITOR_SERVICE_PERIOD
                - ReminderConfig.MONITOR_SERVICE_PERIOD_TOLERANCE,
                executionWindow.windowStart)

        Assert.assertEquals(ReminderConfig.MONITOR_SERVICE_PERIOD
                + ReminderConfig.MONITOR_SERVICE_PERIOD_TOLERANCE,
                executionWindow.windowEnd)
    }
}
