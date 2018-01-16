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

package com.kevalpatel2106.standup.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.google.android.gms.common.GoogleApiAvailability
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.misc.UserSettingsManager
import com.kevalpatel2106.standup.reminder.activityMonitor.ActivityMonitorService
import com.kevalpatel2106.standup.reminder.di.DaggerReminderComponent
import com.kevalpatel2106.standup.reminder.notification.NotificationSchedulerService
import com.kevalpatel2106.standup.reminder.sync.SyncService
import javax.inject.Inject

/**
 * Created by Keval on 30/12/17.
 * This is the [BroadcastReceiver] that will receive below system events:
 *
 * 1.[Intent.ACTION_MY_PACKAGE_REPLACED] - Whenever stand up app gets updated, it will cancel all
 * the jobs and than after reschedule all the jobs.
 *
 * 2.[Intent.ACTION_PACKAGE_CHANGED] - Whenever the google play services gets updated, receiver
 * will cancel all the jobs and than after reschedule all the jobs. See
 * [issue](https://github.com/firebase/firebase-jobdispatcher-android/issues/6).
 *
 * 3.[Intent.ACTION_BOOT_COMPLETED] - Whenever boot completes, receiver will schedule all the jobs
 * based on user settings in [UserSettingsManager].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SystemEventReceiver : BroadcastReceiver() {

    @Inject lateinit var userSettingsManager: UserSettingsManager

    override fun onReceive(context: Context, intent: Intent) {
        DaggerReminderComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SystemEventReceiver)

        // Cancel all the job
        FirebaseJobDispatcher(GooglePlayDriver(context)).cancelAll()

        // Check if the Google Play Services or my current app updated?
        // If other package updated, we don't care.
        // Issue: https://github.com/firebase/firebase-jobdispatcher-android/issues/6
        if (intent.action == Intent.ACTION_PACKAGE_CHANGED) {
            if (intent.extras[Intent.EXTRA_PACKAGE_NAME] != GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE)  /* The updated app is not google play services */
                return
        }

        //Sync all the pending activities.
        if (userSettingsManager.enableBackgroundSync) {
            SyncService.scheduleSync(context, userSettingsManager.syncInterval.toInt())
        }

        //Start monitoring activity.
        ActivityMonitorService.scheduleMonitoringJob(context)

        //Schedule the next reminder
        NotificationSchedulerService.scheduleNotification(context)
    }
}
