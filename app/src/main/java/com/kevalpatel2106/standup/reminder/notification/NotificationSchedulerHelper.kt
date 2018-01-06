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

package com.kevalpatel2106.standup.reminder.notification

import android.content.Context
import android.support.annotation.VisibleForTesting
import com.firebase.jobdispatcher.*
import com.kevalpatel2106.base.annotations.Helper
import com.kevalpatel2106.standup.reminder.ReminderConfig
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager

/**
 * Created by Keval on 05/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Helper(NotificationSchedulerService::class)
internal object NotificationSchedulerHelper {

    @VisibleForTesting
    internal const val REMINDER_NOTIFICATION_JOB_TAG = "reminder_notification_job"

    @JvmStatic
    @VisibleForTesting
    internal fun prepareJob(context: Context,
                            scheduleAfter: Int = ReminderConfig.STAND_UP_DURATION): Job {

        //Save the time of upcoming notification.
        SharedPrefsProvider.savePreferences(ReminderConfig.PREF_KEY_NEXT_NOTIFICATION_TIME,
                System.currentTimeMillis() + scheduleAfter)

        return FirebaseJobDispatcher(GooglePlayDriver(context))
                .newJobBuilder()
                .setService(NotificationSchedulerService::class.java)       // the JobService that will be called
                .setTag(REMINDER_NOTIFICATION_JOB_TAG)         // uniquely identifies the job
                .setRecurring(false)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(getExecutionWindow(scheduleAfter))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .build()
    }

    @JvmStatic
    @VisibleForTesting
    internal fun getExecutionWindow(scheduleAfterSec: Int): JobTrigger.ExecutionWindowTrigger {
        return Trigger.executionWindow(
                scheduleAfterSec - ReminderConfig.NOTIFICATION_SERVICE_PERIOD_TOLERANCE,
                scheduleAfterSec + ReminderConfig.NOTIFICATION_SERVICE_PERIOD_TOLERANCE)
    }

    @VisibleForTesting
    internal fun shouldDisplayNotification(): Boolean {
        return UserSessionManager.isUserLoggedIn
    }
}