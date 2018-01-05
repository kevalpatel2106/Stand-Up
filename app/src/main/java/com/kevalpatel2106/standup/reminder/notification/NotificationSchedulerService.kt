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

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.CallSuper
import android.support.annotation.VisibleForTesting
import com.firebase.jobdispatcher.*
import com.kevalpatel2106.standup.reminder.ReminderConfig
import com.kevalpatel2106.standup.reminder.sync.SyncService
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import timber.log.Timber

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class NotificationSchedulerService : JobService() {

    companion object {

        @VisibleForTesting
        internal const val REMINDER_NOTIFICATION_JOB_TAG = "reminder_notification_job"

        @SuppressLint("VisibleForTests")
        @JvmStatic
        internal fun scheduleNotification(context: Context,
                                          scheduleAfter: Int = ReminderConfig.STAND_UP_DURATION) {
            //Schedule the job
            FirebaseJobDispatcher(GooglePlayDriver(context))
                    .mustSchedule(prepareJob(context, scheduleAfter))
        }

        @JvmStatic
        @VisibleForTesting
        internal fun prepareJob(context: Context,
                                scheduleAfter: Int = ReminderConfig.STAND_UP_DURATION): Job {

            Timber.e("Notification scheduled.")
            //Build the job
            val executionWindow = Trigger.executionWindow(
                    scheduleAfter - ReminderConfig.NOTIFICATION_SERVICE_PERIOD_TOLERANCE,
                    scheduleAfter + ReminderConfig.NOTIFICATION_SERVICE_PERIOD_TOLERANCE)

            //Save the time of upcoming notification.
            SharedPrefsProvider.savePreferences(ReminderConfig.PREF_KEY_NEXT_NOTIFICATION_TIME,
                    System.currentTimeMillis() + scheduleAfter)

            return FirebaseJobDispatcher(GooglePlayDriver(context))
                    .newJobBuilder()
                    .setService(NotificationSchedulerService::class.java)       // the JobService that will be called
                    .setTag(REMINDER_NOTIFICATION_JOB_TAG)         // uniquely identifies the job
                    .setRecurring(false)
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                    .setTrigger(executionWindow)
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    .build()
        }

        @JvmStatic
        internal fun cancel(context: Context) {
            FirebaseJobDispatcher(GooglePlayDriver(context)).cancel(REMINDER_NOTIFICATION_JOB_TAG)
            SharedPrefsProvider.removePreferences(ReminderConfig.PREF_KEY_NEXT_NOTIFICATION_TIME)
        }

        @VisibleForTesting
        internal fun shouldDisplayNotification(): Boolean {
            return UserSessionManager.isUserLoggedIn
        }
    }

    @CallSuper
    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    @SuppressLint("VisibleForTests")
    @CallSuper
    override fun onStartJob(p0: JobParameters?): Boolean {
        Timber.d("Notification reminder job started.")

        if (shouldDisplayNotification()) {
            //Fire reminder notification
            ReminderNotification.notify(this@NotificationSchedulerService)

            //Schedule the next notification
            scheduleNotification(this@NotificationSchedulerService)

            //Sync the database
            SyncService.syncNow(this@NotificationSchedulerService)
        }
        return false /* Stop the service */
    }
}