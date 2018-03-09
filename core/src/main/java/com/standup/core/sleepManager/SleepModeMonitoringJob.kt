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

package com.standup.core.sleepManager

import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.db.userActivity.UserActivityHelper
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.core.Core
import com.standup.core.activityMonitor.ActivityMonitorJob
import com.standup.core.di.DaggerCoreComponent
import com.standup.core.reminder.NotificationSchedulerJob
import com.standup.core.repo.CoreRepo
import dagger.Lazy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Keval on 19/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
internal class SleepModeMonitoringJob : Job() {

    companion object {
        /**
         * Unique tag for the job which notifies when the sleep mode starts.
         */
        const val SLEEP_MODE_START_JOB_TAG = "sleep_mode_start_job_tag"

        /**
         * Unique tag for the job which notifies when the sleep mode ends.
         */
        const val SLEEP_MODE_END_JOB_TAG = "sleep_mode_end_job_tag"

        /**
         * Schedule the job to notify when the sleep mode starts and when the sleep mode ends.
         *
         * Here two jobs will be scheduled:
         * - Job with the tag [SLEEP_MODE_START_JOB_TAG] will run this job whenever the sleep mode
         * starts. This job is scheduled to run on [SleepModeMonitoringHelper.getSleepStartTiming]
         * unix milliseconds. At this time, job will cancelJob the [NotificationSchedulerJob] to prevent
         * stand up reminder notifications and [ActivityMonitorJob] will stop monitoring user's
         * activity. This will set [UserSettingsManager.isCurrentlyInSleepMode].
         * - Job with the tag [SLEEP_MODE_END_JOB_TAG] will run this job whenever the sleep mode
         * ends. This job is scheduled to run on [SleepModeMonitoringHelper.getSleepEndTiming]
         * unix milliseconds. At this time, job will reschedule the [NotificationSchedulerJob] to
         * display reminder notifications and [ActivityMonitorJob] to start monitoring user's activity.
         * This will reset [UserSettingsManager.isCurrentlyInSleepMode].
         *
         * This job is one-shot job not periodic. At the end of [SLEEP_MODE_END_JOB_TAG] job, it
         * will reschedule this job for the next day.
         *
         *
         * THIS METHOD IS FOR INTERNAL USE. USE [com.standup.core.Core.setUpSleepMode]
         * FOR SCHEDULING OR CANCELING THE JOB BASED ON THE USER SETTINGS.
         *
         * @return True if both the jobs are scheduled successfully else false.
         * @see SleepModeMonitoringHelper.getSleepStartTiming
         * @see SleepModeMonitoringHelper.getSleepEndTiming
         */
        @JvmStatic
        internal fun scheduleJob(userSettingsManager: UserSettingsManager): Boolean {

            return synchronized(ActivityMonitorJob::class) {

                //Arrange the DND start job
                val startSleepJobTime = SleepModeMonitoringHelper.getSleepStartTiming(userSettingsManager)
                val startJobId = JobRequest.Builder(SLEEP_MODE_START_JOB_TAG)
                        .setUpdateCurrent(true)
                        .setExact(startSleepJobTime - System.currentTimeMillis())
                        .build()
                        .schedule()

                Timber.i("`Sleep mode begin` monitoring job with id $startJobId scheduled at $startSleepJobTime milliseconds.")

                //Arrange the DND end job
                val endSleepJobTime = SleepModeMonitoringHelper.getSleepEndTiming(userSettingsManager)
                val endJobId = JobRequest.Builder(SLEEP_MODE_END_JOB_TAG)
                        .setUpdateCurrent(true)
                        .setExact(endSleepJobTime - System.currentTimeMillis())
                        .build()
                        .schedule()

                Timber.i("`Sleep mode end` monitoring job with id $endJobId scheduled after $endSleepJobTime milliseconds.")
                return@synchronized true
            }
        }


        /**
         * Cancel both [SLEEP_MODE_END_JOB_TAG] and [SLEEP_MODE_START_JOB_TAG] jobs.
         */
        @JvmStatic
        internal fun cancelScheduledJob() {
            JobManager.instance().cancelAllForTag(SLEEP_MODE_END_JOB_TAG)
            JobManager.instance().cancelAllForTag(SLEEP_MODE_START_JOB_TAG)
        }

        /**
         * Get new instance of [SleepModeMonitoringJob].
         */
        internal fun getInstance() = SleepModeMonitoringJob()

    }

    @Inject
    lateinit var userSettingsManager: UserSettingsManager

    @Inject
    lateinit var sharedPrefsProvider: SharedPrefsProvider

    @Inject
    lateinit var userSessionManager: UserSessionManager

    @Inject
    lateinit var core: Lazy<Core>

    @Inject
    lateinit var coreRepo: CoreRepo

    override fun onRunJob(params: Params): Result {
        //Inject dependencies.
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SleepModeMonitoringJob)

        //Check if the job should be running?
        if (!SleepModeMonitoringHelper.shouldRunningJob(userSessionManager)) {
            cancelScheduledJob()
            return Result.SUCCESS
        }


        when (params.tag) {
            SLEEP_MODE_START_JOB_TAG -> sleepModeStarted()
            SLEEP_MODE_END_JOB_TAG -> sleepModeEnded()
            else -> throw IllegalStateException("Invalid tag: ${params.tag}")
        }

        return Result.SUCCESS
    }

    private fun sleepModeStarted() {
        core.get().refresh()

        SleepModeStartNotification.notify(context)
    }

    private fun sleepModeEnded() {
        //Insert the fake starting activity
        coreRepo.insertNewUserActivity(
                newActivity = UserActivityHelper.createLocalUserActivity(UserActivityType.SITTING),
                doNotMergeWithPrevious = true
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({

                    //Turn off sleep mode
                    core.get().refresh()

                    //Schedule the next sleep monitoring job
                    scheduleJob(userSettingsManager)

                    //Dismiss the sleep mode notification
                    SleepModeStartNotification.cancel(context)
                }, {
                    //Error.
                    //NO OP
                    Timber.e(it.printStackTrace().toString())
                })
    }
}
