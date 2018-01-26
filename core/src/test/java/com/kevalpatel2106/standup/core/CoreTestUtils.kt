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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.ServiceInfo
import android.test.mock.MockContext
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.robolectric.RuntimeEnvironment
import java.util.*

/**
 * Created by Keval on 26/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

internal object CoreTestUtils {

    /**
     * Mock [Context] for creating the Job manager in evernote. Run this with robo-electric. This
     * is taken from evernote android job repo.
     * ([Here](https://github.com/evernote/android-job/blob/master/library/src/test/java/com/evernote/android/job/BaseJobManagerTest.java#L121)
     *
     * @see 'https://github.com/evernote/android-job/issues/220'
     */
    fun createMockContext(): Context {
        // otherwise the JobScheduler isn't supported we check if the service is enable
        // Robolectric doesn't parse services from the manifest, see https://github.com/robolectric/robolectric/issues/416
        val packageManager = Mockito.mock(PackageManager::class.java)
        Mockito.`when`<List<ResolveInfo>>(packageManager
                .queryBroadcastReceivers(ArgumentMatchers.any(Intent::class.java), ArgumentMatchers.anyInt()))
                .thenReturn(Collections.singletonList(ResolveInfo()))

        val resolveInfo = ResolveInfo()
        resolveInfo.serviceInfo = ServiceInfo()
        resolveInfo.serviceInfo.permission = "android.permission.BIND_JOB_SERVICE"
        Mockito.`when`<List<ResolveInfo>>(packageManager
                .queryIntentServices(ArgumentMatchers.any(Intent::class.java), ArgumentMatchers.anyInt()))
                .thenReturn(Collections.singletonList(resolveInfo))

        val context = Mockito.spy(RuntimeEnvironment.application)
        Mockito.`when`(context.packageManager).thenReturn(packageManager)
        Mockito.`when`(context.applicationContext).thenReturn(context)

        val mockContext = Mockito.mock(MockContext::class.java)
        Mockito.`when`(mockContext.applicationContext).thenReturn(context)
        return mockContext
    }
}
