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
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.misc.UserSessionManager
import com.kevalpatel2106.standup.misc.UserSettingsManager
import com.kevalpatel2106.standup.reminder.ReminderConfig
import com.kevalpatel2106.standup.reminder.di.DaggerReminderComponent
import com.kevalpatel2106.utils.SharedPrefsProvider
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 * Service to fire reminder notification. This job will run after given duration and display the
 * reminder notification if user is logged in. This service will automatically schedule the next
 * job after [ReminderConfig.STAND_UP_DURATION].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class NotificationSchedulerService : JobService() {

    companion object {

        /**
         * Schedule the [NotificationSchedulerService] job after [scheduleAfter] milliseconds.
         * This will override all the previous scheduled [NotificationSchedulerService] job. This
         * job will be one shot not recurring.
         */
        @SuppressLint("VisibleForTests")
        @JvmStatic
        internal fun scheduleNotification(context: Context,
                                          scheduleAfter: Int = ReminderConfig.STAND_UP_DURATION) {
            //Schedule the job
            FirebaseJobDispatcher(GooglePlayDriver(context))
                    .mustSchedule(NotificationSchedulerHelper.prepareJob(context, scheduleAfter,
                            SharedPrefsProvider(context)))
        }

        /**
         * Cancel all the [NotificationSchedulerService] jobs if any job is scheduled with
         * [NotificationSchedulerHelper.REMINDER_NOTIFICATION_JOB_TAG].
         */
        @JvmStatic
        internal fun cancel(context: Context) {
            FirebaseJobDispatcher(GooglePlayDriver(context))
                    .cancel(NotificationSchedulerHelper.REMINDER_NOTIFICATION_JOB_TAG)
            SharedPrefsProvider(context).removePreferences(SharedPreferenceKeys.PREF_KEY_NEXT_NOTIFICATION_TIME)
        }
    }

    /**
     * [UserSessionManager] for getting the user session details.
     */
    @Inject lateinit var userSessionManager: UserSessionManager

    /**
     * [UserSettingsManager] for getting the user settings details.
     */
    @Inject lateinit var userSettingsManager: UserSettingsManager

    override fun onCreate() {
        super.onCreate()

        //Dagger injection
        DaggerReminderComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@NotificationSchedulerService)
    }

    @CallSuper
    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    /**
     * When the job starts, programme will first check if the user is logged in or not?
     * </p>
     * If the user is logged in, it will display the notification and schedule the next job
     * after [ReminderConfig.STAND_UP_DURATION].
     */
    @SuppressLint("VisibleForTests")
    @CallSuper
    override fun onStartJob(p0: JobParameters?): Boolean {

        if (NotificationSchedulerHelper.shouldDisplayNotification(userSessionManager)) {
            //Fire reminder notification
            ReminderNotification().notify(this@NotificationSchedulerService)

            //Schedule the next notification
            scheduleNotification(this@NotificationSchedulerService)
        }
        return false /* Stop the service */
    }
}
