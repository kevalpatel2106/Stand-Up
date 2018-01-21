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

package com.kevalpatel2106.standup.core.dndManager

import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.standup.core.Core
import com.kevalpatel2106.standup.core.activityMonitor.ActivityMonitorJob
import com.kevalpatel2106.standup.core.di.DaggerCoreComponent
import com.kevalpatel2106.standup.core.reminder.NotificationSchedulerJob
import com.kevalpatel2106.utils.SharedPrefsProvider
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Keval on 19/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
internal class AutoDndMonitoringJob : Job() {

    companion object {
        /**
         * Unique tag for the job which notifies when the Auto DND mode starts.
         */
        const val AUTO_DND_START_JOB_TAG = "auto_dnd_start_job_tag"

        /**
         * Unique tag for the job which notifies when the Auto DND mode ends.
         */
        const val AUTO_DND_END_JOB_TAG = "auto_dnd_end_job_tag"

        /**
         * Schedule the job to notify when the auto dnd mode starts and when the auto dnd mode ends.
         *
         * Here two jobs will be scheduled:
         * - Job with the tag [AUTO_DND_START_JOB_TAG] will run this job whenever the auto DND mode
         * starts. This job is scheduled to run on [AutoDndMonitoringHelper.getAutoDndStartTiming]
         * unix milliseconds. At this time, job will cancel the [NotificationSchedulerJob] to prevent
         * stand up reminder notifications. This will set [UserSettingsManager.isCurrentlyDndEnable].
         * - Job with the tag [AUTO_DND_END_JOB_TAG] will run this job whenever the auto DND mode
         * ends. This job is scheduled to run on [AutoDndMonitoringHelper.getAutoDndEndTiming]
         * unix milliseconds. At this time, job will reschedule the [NotificationSchedulerJob] to
         * display reminder notifications. This will reset [UserSettingsManager.isCurrentlyDndEnable].
         *
         * This job is one-shot job not periodic. At the end of [AUTO_DND_END_JOB_TAG] job, it
         * will reschedule this job for the next day.
         *
         * @return True if both the jobs are scheduled successfully else false.
         * @see AutoDndMonitoringHelper.getAutoDndStartTiming
         * @see AutoDndMonitoringHelper.getAutoDndEndTiming
         */
        @JvmStatic
        internal fun scheduleJobIfAutoDndEnabled(userSettingsManager: UserSettingsManager): Boolean {
            return synchronized(ActivityMonitorJob::class) {
                if (!userSettingsManager.isAutoDndEnable) {

                    //Cancel the any dnd scheduled jobs.
                    cancelScheduledJob()
                    return@synchronized false
                }

                //Arrange the DND start job
                val startDndJobTime = AutoDndMonitoringHelper.getAutoDndStartTiming(userSettingsManager)
                val startJobId = JobRequest.Builder(AUTO_DND_START_JOB_TAG)
                        .setUpdateCurrent(true)
                        .setExact(startDndJobTime - System.currentTimeMillis())
                        .build()
                        .schedule()

                Timber.i("`Auto DND begin` monitoring job with id $startJobId scheduled after $startDndJobTime milliseconds.")

                //Arrange the DND end job
                val endDndJobTime = AutoDndMonitoringHelper.getAutoDndEndTiming(userSettingsManager)
                val endJobId = JobRequest.Builder(AUTO_DND_END_JOB_TAG)
                        .setUpdateCurrent(true)
                        .setExact(endDndJobTime - System.currentTimeMillis())
                        .build()
                        .schedule()

                Timber.i("`Auto DND end` monitoring job with id $endJobId scheduled after $endDndJobTime milliseconds.")
                return@synchronized true
            }
        }

        /**
         * Cancel both [AUTO_DND_END_JOB_TAG] and [AUTO_DND_START_JOB_TAG] jobs.
         */
        @JvmStatic
        internal fun cancelScheduledJob() {
            JobManager.instance().cancelAllForTag(AUTO_DND_START_JOB_TAG)
            JobManager.instance().cancelAllForTag(AUTO_DND_END_JOB_TAG)
        }
    }

    @Inject
    lateinit var userSettingsManager: UserSettingsManager

    @Inject
    lateinit var sharedPrefsProvider: SharedPrefsProvider

    @Inject
    lateinit var userSessionManager: UserSessionManager

    override fun onRunJob(params: Params): Result {
        //Inject dependencies.
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@AutoDndMonitoringJob)

        when (params.tag) {
            AUTO_DND_START_JOB_TAG -> autoDndStarted()
            AUTO_DND_END_JOB_TAG -> autoDndEnded()
            else -> throw IllegalStateException("Invalid tag: ${params.tag}")
        }

        return Result.SUCCESS
    }

    private fun autoDndStarted() {
        //Turn on the DND mode
        userSettingsManager.isCurrentlyDndEnable = true

        Core(userSessionManager = userSessionManager,
                userSettingsManager = userSettingsManager,
                prefsProvider = sharedPrefsProvider)
                .refresh()

        //TODO may be display a small low priority notification?

    }

    private fun autoDndEnded() {
        //Turn off the DND mode
        userSettingsManager.isCurrentlyDndEnable = false

        Core(userSessionManager = userSessionManager,
                userSettingsManager = userSettingsManager,
                prefsProvider = sharedPrefsProvider)
                .refresh()

        //Schedule the next dnd monitoring job
        scheduleJobIfAutoDndEnabled(userSettingsManager)
    }
}