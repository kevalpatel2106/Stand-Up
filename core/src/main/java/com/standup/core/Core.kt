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
@file:Suppress("KDocUnresolvedReference")

package com.standup.core

import android.content.Context
import android.os.Handler
import com.evernote.android.job.JobConfig
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.core.activityMonitor.ActivityMonitorHelper
import com.standup.core.activityMonitor.ActivityMonitorJob
import com.standup.core.dailyReview.DailyReviewHelper
import com.standup.core.dailyReview.DailyReviewJob
import com.standup.core.dndManager.AutoDndMonitoringHelper
import com.standup.core.dndManager.AutoDndMonitoringJob
import com.standup.core.misc.CoreJobCreator
import com.standup.core.misc.EvernoteJobLogger
import com.standup.core.reminder.NotificationSchedulerHelper
import com.standup.core.reminder.NotificationSchedulerJob
import com.standup.core.reminder.ReminderNotification
import com.standup.core.sleepManager.SleepModeMonitoringHelper
import com.standup.core.sleepManager.SleepModeMonitoringJob
import com.standup.core.sync.SyncJob
import com.standup.core.sync.SyncJobHelper
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Keval on 19/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class Core @Inject constructor(private val userSessionManager: UserSessionManager,
                               private val userSettingsManager: UserSettingsManager,
                               private val prefsProvider: SharedPrefsProvider) {

    companion object {
        /**
         * Shut down the code by unregister all the jobs and stop the core.
         */
        fun meltdown() {
            JobManager.instance().cancelAll()
        }

        /**
         * Sync the database with the server manually. This will run the [SyncJob] forcefully.
         * This won't affect the period of the future periodic [SyncJob].
         *
         * @see SyncJob.syncNow
         */
        fun forceSync() = SyncJob.syncNow()

        /**
         * Check if the [SyncJob] is currently running?
         */
        fun isSyncingCurrently() = SyncJob.isSyncing

        /**
         * Fire the test [ReminderNotification]. This will cancelJob the [ReminderNotification] after
         * 10 seconds.
         */
        fun fireTestReminder(context: Context) {
            ReminderNotification().notify(context)

            //Cancel the notification after some time.
            Handler().postDelayed({ ReminderNotification().cancel(context) }, 10_000L /* 10 Seconds */)
        }

        fun runNotificationJob() {
            //Run the job
            JobRequest.Builder(NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG)
                    .setUpdateCurrent(true)
                    .startNow()
                    .build()
                    .schedule()
        }
    }

    /**
     * Start the core module. This will initialize [CoreJobCreator] and also schedules all the jobs
     * based on the [userSettingsManager] values.
     *
     * @see refresh
     * @see meltdown
     */
    fun turnOn(application: BaseApplication) {
        JobConfig.addLogger(EvernoteJobLogger())

        //Initialize the core creator
        CoreJobCreator.init(application)

        refresh()
    }

    /**
     * Schedule/Cancel the jobs based on the update preferences.
     */
    fun refresh() {
        if (BuildConfig.DEBUG) {
            Timber.i("Refreshing the core...")
            Timber.i("Is background sync enabled : ${userSettingsManager.isCurrentlyDndEnable}.")
            Timber.i("Currently DND mode is enabled: ${userSettingsManager.isCurrentlyDndEnable}.")
            Timber.i("Currently Sleep mode is enabled: ${userSettingsManager.isCurrentlyInSleepMode}.")
        }

        //Check iff the user is logged in?
        if (!userSessionManager.isUserLoggedIn) {
            Timber.w("User is not logged in. Skipping the refreshing of core.")
            return
        }

        JobManager.instance().cancelAll()

        setUpSleepMode(userSessionManager, userSettingsManager)
        setUpAutoDnd(userSessionManager, userSettingsManager)
        setUpActivityMonitoring(userSessionManager, userSettingsManager)
        setUpDailyReview(userSessionManager, userSettingsManager)
        setUpReminderNotification(userSessionManager, userSettingsManager, prefsProvider)
        setUpSync(userSessionManager, userSettingsManager)
    }

    /**
     * This method will schedule the [ActivityMonitorJob] after [CoreConfig.MONITOR_SERVICE_PERIOD]
     * if sleep mode is not activated (i.e. [UserSettingsManager.isCurrentlyInSleepMode] is true).
     * If the next [ActivityMonitorJob] is already scheduled, return.
     *
     * This method guarantees that no duplicate jobs are scheduled and no jobs scheduled in sleep mode.
     * Use this method instead of calling [ActivityMonitorJob.scheduleNextJob] or
     * [ActivityMonitorJob.cancel] to control the job.
     *
     * @see ActivityMonitorJob.scheduleNextJob
     * @see ActivityMonitorJob.cancel
     * @see ActivityMonitorHelper.isAnyJobScheduled
     */
    internal fun setUpActivityMonitoring(userSessionManager: UserSessionManager,
                                         userSettingsManager: UserSettingsManager) {

        //Check if the sleep mode is running?
        //Don't start monitoring.
        if (!ActivityMonitorHelper.shouldMonitoringActivity(userSessionManager, userSettingsManager)) {
            Timber.i("User activity should not be monitored. Canceling the future activity monitoring jobs.")
            ActivityMonitorJob.cancel()
            return
        }

        //Check if the next job is scheduled or not?
        if (ActivityMonitorHelper.isAnyJobScheduled()) {
            //Job is already scheduled.
            return
        }

        //Go ahead. Schedule.
        ActivityMonitorJob.scheduleNextJob()
    }

    fun setUpReminderNotification() {
        setUpReminderNotification(userSessionManager, userSettingsManager, prefsProvider)
    }

    /**
     * Register the [NotificationSchedulerJob] after [CoreConfig.STAND_UP_REMINDER_INTERVAL] if the
     * DND mode and sleep mode is not running and the [NotificationSchedulerJob] is not scheduled
     * already.
     *
     * This method guarantees that no duplicate jobs are scheduled and no jobs scheduled in DND mode
     * or in the sleep mode. Use this method instead of calling
     * [NotificationSchedulerJob.scheduleNotification] or [NotificationSchedulerJob.cancelJob] to
     * control the job.
     *
     * @see NotificationSchedulerJob.scheduleNotification
     * @see NotificationSchedulerJob.cancelJob
     * @see NotificationSchedulerHelper.isReminderScheduled
     */
    internal fun setUpReminderNotification(userSessionManager: UserSessionManager,
                                           userSettingsManager: UserSettingsManager,
                                           sharedPrefsProvider: SharedPrefsProvider) {

        //DND mode is turned on or the sleep mode is turn on?.
        //Do not schedule the next job and cancelJob the current job.
        if (!NotificationSchedulerHelper.shouldDisplayNotification(userSessionManager, userSettingsManager)) {
            Timber.i("Not right time to schedule notifications. Canceling the job.")
            NotificationSchedulerJob.cancelJob(sharedPrefsProvider)
            return
        }

        //Check if the notification job is already scheduled?
        if (NotificationSchedulerHelper.isReminderScheduled()) {
            //Job is scheduled.
            //Do not schedule it again.
            return
        }

        //Schedule the periodic job.
        NotificationSchedulerJob.scheduleNotification(sharedPrefsProvider)
    }


    /**
     * Register alarm for daily review notifications.
     *
     * @see DailyReviewJob.scheduleJob
     */
    private fun setUpDailyReview(userSessionManager: UserSessionManager,
                                 userSettingsManager: UserSettingsManager) {
        //Check if the daily review is enabled?
        if (!DailyReviewHelper.shouldRunningThisJob(userSessionManager, userSettingsManager)) {
            Timber.i("Daily reviews are disabled. Canceling the job.")
            DailyReviewJob.cancelScheduledJob()
            return
        }

        //Register new alarm
        DailyReviewJob.scheduleJob(userSettingsManager)
    }

    /**
     * Register next sync with the server after [UserSettingsManager.syncInterval] by registering
     * [SyncJob].
     *
     * @see SyncJob.scheduleSync
     */
    private fun setUpSync(userSessionManager: UserSessionManager,
                          userSettingsManager: UserSettingsManager) {
        //Check if the background sync is enabled?
        if (!SyncJobHelper.shouldRunJob(userSessionManager, userSettingsManager)) {
            Timber.i("Background sync is disabled by the user. Skipping it.")
            SyncJob.cancelScheduledSync()
        } else {

            //Schedule periodic the sync service.
            SyncJob.scheduleSync(userSettingsManager.syncInterval)
        }
    }

    /**
     * Register the auto DND starting and ending jobs.
     *
     * @see AutoDndMonitoringJob.scheduleJobIfAutoDndEnabled
     * @see AutoDndMonitoringJob.cancelScheduledJob
     */
    private fun setUpAutoDnd(userSessionManager: UserSessionManager,
                             userSettingsManager: UserSettingsManager) {
        //Check if the auto dnd is enabled?
        if (!AutoDndMonitoringHelper.shouldRunningThisJob(userSettingsManager, userSessionManager)) {
            AutoDndMonitoringJob.cancelScheduledJob()
            return
        }

        // If the auto dnd mode should be turned in currently...
        // Set the flag
        if (AutoDndMonitoringHelper.isCurrentlyInAutoDndMode(userSettingsManager)) {
            userSettingsManager.isCurrentlyDndEnable = true
        }

        AutoDndMonitoringJob.scheduleJobIfAutoDndEnabled(userSettingsManager)
    }

    /**
     * Register the sleep starting and ending jobs.
     *
     * @see AutoDndMonitoringJob.scheduleJobIfAutoDndEnabled
     * @see AutoDndMonitoringJob.cancelScheduledJob
     */
    private fun setUpSleepMode(userSessionManager: UserSessionManager,
                               userSettingsManager: UserSettingsManager) {
        //Check if the sleep mode is enabled?
        if (!SleepModeMonitoringHelper.shouldRunningJob(userSessionManager)) {
            SleepModeMonitoringJob.cancelScheduledJob()
            return
        }

        //Check if while scheduling this job, is sleep mode should turn on?
        userSettingsManager.isCurrentlyInSleepMode = SleepModeMonitoringHelper
                .isCurrentlyInSleepMode(userSettingsManager)

        SleepModeMonitoringJob.scheduleJob(userSettingsManager)
    }
}
