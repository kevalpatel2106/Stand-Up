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

package com.kevalpatel2106.standup.core

import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.utils.SharedPrefsProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 01-Feb-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class CorePrefsProviderTest {
    private lateinit var sharedPrefsProvider: SharedPrefsProvider
    private lateinit var corePrefsProvider: CorePrefsProvider

    @Before
    fun setUp() {
        sharedPrefsProvider = SharedPrefsProvider(MockSharedPreference())
        corePrefsProvider = CorePrefsProvider(sharedPrefsProvider)
    }

    @Test
    fun checkSaveLastSyncTime() {
        val lastSyncTime = System.currentTimeMillis()
        corePrefsProvider.saveLastSyncTime(lastSyncTime)

        Assert.assertEquals(lastSyncTime,
                sharedPrefsProvider.getLongFromPreference(CorePrefsProvider.PREF_KEY_LAST_SYNC_TIME))
    }

    @Test
    fun checkSaveNextNotificationTime() {
        val nextNotificationTime = System.currentTimeMillis()
        corePrefsProvider.saveNextNotificationTime(nextNotificationTime)

        Assert.assertEquals(nextNotificationTime,
                sharedPrefsProvider.getLongFromPreference(CorePrefsProvider.PREF_KEY_NEXT_NOTIFICATION_TIME))
    }

    @Test
    fun checkGetLastSyncTime() {
        val lastSyncTime = System.currentTimeMillis()
        corePrefsProvider.saveLastSyncTime(lastSyncTime)

        Assert.assertEquals(lastSyncTime, corePrefsProvider.lastSyncTime)
    }

    @Test
    fun checkGetNextNotificationTime() {
        val nextNotificationTime = System.currentTimeMillis()
        corePrefsProvider.saveNextNotificationTime(nextNotificationTime)

        Assert.assertEquals(nextNotificationTime, corePrefsProvider.nextNotificationTime)
    }
}
