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
import com.kevalpatel2106.standup.reminder.ReminderConfig
import com.kevalpatel2106.standup.reminder.di.DaggerReminderComponent
import com.kevalpatel2106.standup.reminder.sync.SyncService
import com.kevalpatel2106.standup.misc.UserSettingsManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class NotificationSchedulerService : JobService() {

    companion object {

        @SuppressLint("VisibleForTests")
        @JvmStatic
        internal fun scheduleNotification(context: Context,
                                          scheduleAfter: Int = ReminderConfig.STAND_UP_DURATION) {
            //Schedule the job
            FirebaseJobDispatcher(GooglePlayDriver(context))
                    .mustSchedule(NotificationSchedulerHelper.prepareJob(context, scheduleAfter,
                            SharedPrefsProvider(context)))
        }


        @JvmStatic
        internal fun cancel(context: Context) {
            FirebaseJobDispatcher(GooglePlayDriver(context))
                    .cancel(NotificationSchedulerHelper.REMINDER_NOTIFICATION_JOB_TAG)
            SharedPrefsProvider(context).removePreferences(SharedPreferenceKeys.PREF_KEY_NEXT_NOTIFICATION_TIME)
        }
    }

    @Inject lateinit var userSessionManager: UserSessionManager
    @Inject lateinit var userSettingsManager: UserSettingsManager

    override fun onCreate() {
        super.onCreate()

        DaggerReminderComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@NotificationSchedulerService)
    }

    @CallSuper
    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    @SuppressLint("VisibleForTests")
    @CallSuper
    override fun onStartJob(p0: JobParameters?): Boolean {
        Timber.d("Notification reminder job started.")

        if (NotificationSchedulerHelper.shouldDisplayNotification(userSessionManager)) {
            //Fire reminder notification
            ReminderNotification.notify(this@NotificationSchedulerService)

            //Schedule the next notification
            scheduleNotification(this@NotificationSchedulerService)

            //Sync the database
            if (userSettingsManager.enableBackgroundSync)
                SyncService.syncNow(this@NotificationSchedulerService)
        }
        return false /* Stop the service */
    }
}