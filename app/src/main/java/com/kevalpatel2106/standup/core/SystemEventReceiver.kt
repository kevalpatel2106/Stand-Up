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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.annotation.VisibleForTesting
import com.evernote.android.job.JobManager
import com.kevalpatel2106.base.UserSettingsManager
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.core.activityMonitor.ActivityMonitorJob
import com.kevalpatel2106.standup.core.dailyReview.DailyReviewHelper
import com.kevalpatel2106.standup.core.di.DaggerCoreComponent
import com.kevalpatel2106.standup.core.dndManager.AutoDndMonitoringJob
import com.kevalpatel2106.standup.core.reminder.NotificationSchedulerJob
import com.kevalpatel2106.standup.core.sync.SyncJob
import com.kevalpatel2106.utils.SharedPrefsProvider
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Keval on 30/12/17.
 * This is the [BroadcastReceiver] that will receive below system events:
 *
 * 1.[Intent.ACTION_MY_PACKAGE_REPLACED] - Whenever stand up app gets updated, it will cancel all
 * the jobs and than after reschedule all the jobs.
 *
 * 2.[Intent.ACTION_BOOT_COMPLETED] - Whenever boot completes, receiver will schedule all the jobs
 * based on user settings in [UserSettingsManager].
 *
 * [onReceive] method will first cancel all the jobs scheduled by [JobManager]. Evernote android job
 * registers all the jobs again on [Intent.ACTION_MY_PACKAGE_REPLACED] or [Intent.ACTION_BOOT_COMPLETED].
 * So first unregister all the jobs and re-schedule jobs based on the current settings.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SystemEventReceiver : BroadcastReceiver {

    @Suppress("unused")
    constructor() {
        //Inject dependencies.
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SystemEventReceiver)

    }

    @VisibleForTesting
    constructor(userSettingsManager: UserSettingsManager,
                sharedPrefsProvider: SharedPrefsProvider) {
        this.userSettingsManager = userSettingsManager
        this.sharedPrefsProvider = sharedPrefsProvider
    }

    /**
     * [UserSettingsManager] to provide user settings configs.
     */
    @Inject
    lateinit var userSettingsManager: UserSettingsManager

    /**
     * [SharedPrefsProvider] to access shared preferences.
     */
    @Inject
    lateinit var sharedPrefsProvider: SharedPrefsProvider

    /**
     * @see BroadcastReceiver.onReceive
     */
    override fun onReceive(context: Context, intent: Intent) {

        //Validate the intent actions
        if (intent.action != Intent.ACTION_BOOT_COMPLETED
                && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED)
            return

        // Cancel all the job
        // Evernote job automatically schedules all the jobs for you on boot complete.
        // So, we are going to cancel all the jobs and schedule our own jobs again.
        JobManager.instance().cancelAll()

        //Start monitoring activity.
        setUpActivityMonitoring()

        //Sync all the pending activities.
        setUpSync()

        //Schedule the next reminder
        setUpNotification(sharedPrefsProvider)

        //Schedule the next daily review.
        setUpDailyReview(context)

        setUpAutoDnd()
        Timber.i("Rescheduling of all jobs and alarms completed. :-)")
    }

    /**
     * Register alarm for daily review notifications.
     *
     * @see DailyReviewHelper.registerDailyReview
     */
    private fun setUpDailyReview(context: Context) {

        //Cancel the upcoming alarm, if any.
        DailyReviewHelper.cancelAlarm(context)

        //Register new alarm
        DailyReviewHelper.registerDailyReview(context, userSettingsManager)
    }

    /**
     * Register the [NotificationSchedulerJob] after [CoreConfig.STAND_UP_REMINDER_INTERVAL].
     *
     * @see NotificationSchedulerJob.scheduleNotification
     */
    private fun setUpNotification(sharedPrefsProvider: SharedPrefsProvider) {
        NotificationSchedulerJob.scheduleNotification(sharedPrefsProvider)
    }

    /**
     * Start monitoring user activity by registering [ActivityMonitorJob] after
     * [CoreConfig.MONITOR_SERVICE_PERIOD].
     *
     * @see ActivityMonitorJob.scheduleNextJob
     */
    private fun setUpActivityMonitoring() {
        ActivityMonitorJob.scheduleNextJob()
    }

    /**
     * Register next sync with the server after [UserSettingsManager.syncInterval] by registering
     * [SyncJob].
     *
     * @see SyncJob.scheduleSync
     */
    private fun setUpSync() {

        //Check if the background sync is enabled?
        if (userSettingsManager.enableBackgroundSync) {

            //Schedule periodic the sync service.
            SyncJob.scheduleSync(userSettingsManager.syncInterval)
        } else {
            Timber.i("Background sync is disabled by the user. Skipping it.")
        }
    }

    /**
     * Register the auto DND starting and ending jobs.
     *
     * @see AutoDndMonitoringJob.scheduleJobIfAutoDndEnabled
     * @see AutoDndMonitoringJob.cancelScheduledJob
     */
    private fun setUpAutoDnd() {
        AutoDndMonitoringJob.scheduleJobIfAutoDndEnabled(userSettingsManager)
    }
}
