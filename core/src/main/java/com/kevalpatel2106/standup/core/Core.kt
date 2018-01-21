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
 */package com.kevalpatel2106.standup.core

import android.content.Context
import android.os.Handler
import com.evernote.android.job.JobManager
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.standup.core.activityMonitor.ActivityMonitorHelper
import com.kevalpatel2106.standup.core.activityMonitor.ActivityMonitorJob
import com.kevalpatel2106.standup.core.dailyReview.DailyReviewJob
import com.kevalpatel2106.standup.core.dndManager.AutoDndMonitoringJob
import com.kevalpatel2106.standup.core.reminder.NotificationSchedulerHelper
import com.kevalpatel2106.standup.core.reminder.NotificationSchedulerJob
import com.kevalpatel2106.standup.core.reminder.ReminderNotification
import com.kevalpatel2106.standup.core.sleepManager.SleepModeMonitoringJob
import com.kevalpatel2106.standup.core.sync.SyncJob
import com.kevalpatel2106.utils.SharedPrefsProvider
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

        fun forceSync() = SyncJob.syncNow()

        fun isSyncingCurrently() = SyncJob.isSyncing

        fun fireTestReminder(context: Context) {
            ReminderNotification().notify(context)

            //Cancel the notification after some time.
            Handler().postDelayed({ ReminderNotification().cancel(context) }, 10000L /* 10 Seconds */)
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
        //Initialize the core creator
        CoreJobCreator.init(application)

        refresh()
    }

    /**
     * Schedule/Cancel the jobs based on the update preferences.
     */
    fun refresh() {
        Timber.i("Refreshing the core...")

        //Check iff the user is logged in?
        if (!userSessionManager.isUserLoggedIn) {
            Timber.w("User is not logged in. Skipping the refreshing of core.")
            return
        }

        //Schedule all the jobs based on their preferences
        setUpActivityMonitoring(userSettingsManager)

        setUpDailyReview(userSettingsManager)

        setUpReminderNotification(userSettingsManager, prefsProvider)

        setUpSync(userSettingsManager)

        setUpAutoDnd(userSettingsManager)

        setUpSleepMode(userSettingsManager)
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
    internal fun setUpActivityMonitoring(userSettingsManager: UserSettingsManager) {

        //Check if the sleep mode is running?
        //Don't start monitoring.
        if (userSettingsManager.isCurrentlyInSleepMode) {
            Timber.i("Currently Sleep mode is enabled: ${userSettingsManager.isCurrentlyInSleepMode}.")
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


    /**
     * Register the [NotificationSchedulerJob] after [CoreConfig.STAND_UP_REMINDER_INTERVAL] if the
     * DND mode and sleep mode is not running and the [NotificationSchedulerJob] is not scheduled
     * already.
     *
     * This method guarantees that no duplicate jobs are scheduled and no jobs scheduled in DND mode
     * or in the sleep mode. Use this method instead of calling
     * [NotificationSchedulerJob.scheduleNotification] or [NotificationSchedulerJob.cancel] to
     * control the job.
     *
     * @see NotificationSchedulerJob.scheduleNotification
     * @see NotificationSchedulerJob.cancel
     * @see NotificationSchedulerHelper.isReminderScheduled
     */
    internal fun setUpReminderNotification(userSettingsManager: UserSettingsManager,
                                           sharedPrefsProvider: SharedPrefsProvider) {

        //DND mode is turned on or the sleep mode is turn on?.
        //Do not schedule the next job and cancel the current job.
        if (userSettingsManager.isCurrentlyDndEnable || userSettingsManager.isCurrentlyInSleepMode) {
            Timber.i("Currently DND mode is enabled: ${userSettingsManager.isCurrentlyDndEnable}.")
            Timber.i("Currently Sleep mode is enabled: ${userSettingsManager.isCurrentlyInSleepMode}.")
            NotificationSchedulerJob.cancel(sharedPrefsProvider)
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
    private fun setUpDailyReview(userSettingsManager: UserSettingsManager) {
        //Check if the daily review is enabled?
        if (!userSettingsManager.isDailyReviewEnable) {
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
    private fun setUpSync(userSettingsManager: UserSettingsManager) {
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
    private fun setUpAutoDnd(userSettingsManager: UserSettingsManager) {
        //Check if the auto dnd is enabled?
        if (!userSettingsManager.isAutoDndEnable) {
            AutoDndMonitoringJob.cancelScheduledJob()
            return
        }

        AutoDndMonitoringJob.scheduleJobIfAutoDndEnabled(userSettingsManager)
    }

    /**
     * Register the sleep starting and ending jobs.
     *
     * @see AutoDndMonitoringJob.scheduleJobIfAutoDndEnabled
     * @see AutoDndMonitoringJob.cancelScheduledJob
     */
    private fun setUpSleepMode(userSettingsManager: UserSettingsManager) {
        SleepModeMonitoringJob.scheduleJob(userSettingsManager)
    }
}