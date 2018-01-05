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

package com.kevalpatel2106.standup.reminder.sync

import android.content.SharedPreferences
import com.firebase.jobdispatcher.Constraint
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.RetryStrategy
import com.firebase.jobdispatcher.Trigger
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Created by Kevalpatel2106 on 05-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SyncServiceTest {

    @Test
    fun checkSyncNow() {
        val builder = SyncService.prepareJob(RuntimeEnvironment.application)

        Assert.assertTrue(builder.constraints.contains(Constraint.ON_ANY_NETWORK))
        Assert.assertEquals(builder.constraints.size, 1)
        Assert.assertFalse(builder.isRecurring)
        Assert.assertEquals(builder.tag, SyncService.SYNC_JOB_TAG)
        Assert.assertEquals(builder.retryStrategy, RetryStrategy.DEFAULT_LINEAR)
        Assert.assertEquals(builder.lifetime, Lifetime.UNTIL_NEXT_BOOT)
        Assert.assertEquals(builder.trigger, Trigger.NOW)
    }

    fun checkShouldNotSyncSync() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(sharedPref.getBoolean(anyString(), anyBoolean())).thenReturn(false)

        Assert.assertFalse(SyncService.shouldSync())
    }

    fun checkShouldSyncSync() {
        val sharedPref = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(sharedPref.getBoolean(anyString(), anyBoolean())).thenReturn(true)

        Assert.assertTrue(SyncService.shouldSync())
    }
}