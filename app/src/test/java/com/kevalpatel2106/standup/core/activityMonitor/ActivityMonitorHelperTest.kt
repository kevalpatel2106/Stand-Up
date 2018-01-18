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

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
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
        val builder = ActivityMonitorHelper.prepareJob()

        Assert.assertFalse(builder.isPersisted)
        Assert.assertTrue(builder.isRequireBatteryNotLow)
        Assert.assertTrue(builder.isRequireCharging)
        Assert.assertTrue(builder.isRequireDeviceIdle)
        Assert.assertTrue(builder.isRequireStorageNotLow)
        Assert.assertTrue(builder.isPeriodic)
        Assert.assertEquals(builder.id, ActivityMonitorHelper.ACTIVITY_MONITOR_JOB_TAG)
        Assert.assertEquals(builder.service.className, ActivityMonitorService::class.simpleName)
    }
}
