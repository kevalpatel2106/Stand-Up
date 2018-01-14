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

import android.content.Context
import android.support.annotation.VisibleForTesting
import com.firebase.jobdispatcher.*
import com.kevalpatel2106.base.annotations.Helper
import com.kevalpatel2106.standup.misc.UserSessionManager

/**
 * Created by Keval on 05/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Helper(SyncService::class)
object SyncServiceHelper {

    @VisibleForTesting
    internal const val SYNC_JOB_TAG = "sync_job"

    @VisibleForTesting
    @JvmStatic
    internal fun prepareJob(context: Context): Job {
        return FirebaseJobDispatcher(GooglePlayDriver(context))
                .newJobBuilder()
                .setService(SyncService::class.java)       // the JobService that will be called
                .setTag(SYNC_JOB_TAG)         // uniquely identifies the job
                .setRecurring(false)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.NOW)
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .addConstraint(Constraint.ON_ANY_NETWORK)
                .build()
    }

    @VisibleForTesting
    @JvmStatic
    internal fun shouldSync(userSessionManager: UserSessionManager): Boolean {
        return userSessionManager.isUserLoggedIn
    }

}