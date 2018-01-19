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
import com.kevalpatel2106.base.UserSettingsManager
import com.kevalpatel2106.standup.application.BaseApplication
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
class AutoDndMonitoringJob : Job() {

    companion object {
        const val AUTO_DND_START_JOB_TAG = "auto_dnd_start_job_tag"
        const val AUTO_DND_END_JOB_TAG = "auto_dnd_end_job_tag"

        @JvmStatic
        fun scheduleJobIfAutoDndEnabled(userSettingsManager: UserSettingsManager): Boolean {
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

        @JvmStatic
        fun cancelScheduledJob() {
            JobManager.instance().cancelAllForTag(AUTO_DND_START_JOB_TAG)
            JobManager.instance().cancelAllForTag(AUTO_DND_END_JOB_TAG)
        }
    }

    @Inject
    lateinit var userSettingsManager: UserSettingsManager

    @Inject
    lateinit var sharedPrefsProvider: SharedPrefsProvider

    override fun onRunJob(params: Params): Result {
        //Inject dependencies.
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.appComponent)
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
        userSettingsManager.isCurrentlyInSleepMode = true

        //Remove all the notification jobs
        NotificationSchedulerJob.cancel(context)

        //TODO may be display a small low priority notification?

    }

    private fun autoDndEnded() {
        //Turn off the DND mode
        userSettingsManager.isCurrentlyDndEnable = false

        //Start scheduling the notifications again
        NotificationSchedulerJob.scheduleNotification(sharedPrefsProvider)

        //Schedule the next dnd monitoring job
        scheduleJobIfAutoDndEnabled(userSettingsManager)
    }
}