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

package com.kevalpatel2106.standup.core.sleepManager

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
class SleepModeMonitoringJob : Job() {

    companion object {
        const val SLEEP_MODE_START_JOB_TAG = "sleep_mode_start_job_tag"
        const val SLEEP_MODE_END_JOB_TAG = "sleep_mode_end_job_tag"

        @JvmStatic
        fun scheduleJob(userSettingsManager: UserSettingsManager): Boolean {

            return synchronized(ActivityMonitorJob::class) {

                //Arrange the DND start job
                val startSleepJobTime = SleepModeMonitoringHelper.getSleepStartTiming(userSettingsManager)
                val startJobId = JobRequest.Builder(SLEEP_MODE_START_JOB_TAG)
                        .setUpdateCurrent(true)
                        .setExact(startSleepJobTime - System.currentTimeMillis())
                        .build()
                        .schedule()

                Timber.i("`Sleep mode begin` monitoring job with id $startJobId scheduled after $startSleepJobTime milliseconds.")

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

        @JvmStatic
        fun cancelScheduledJob() {
            JobManager.instance().cancelAllForTag(SLEEP_MODE_START_JOB_TAG)
            JobManager.instance().cancelAllForTag(SLEEP_MODE_END_JOB_TAG)
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
                .inject(this@SleepModeMonitoringJob)

        when (params.tag) {
            SLEEP_MODE_START_JOB_TAG -> sleepModeStarted()
            SLEEP_MODE_END_JOB_TAG -> sleepModeEnded()
            else -> throw IllegalStateException("Invalid tag: ${params.tag}")
        }

        return Result.SUCCESS
    }

    private fun sleepModeStarted() {
        if (userSettingsManager.isAutoDndEnable) {

            //Turn on sleep mode
            userSettingsManager.isCurrentlyInSleepMode = true

            //Stop monitoring activity
            ActivityMonitorJob.cancel(context)

            //Remove all the notification jobs
            NotificationSchedulerJob.cancel(context)

            //TODO may be display a small low priority notification?
        }
    }

    private fun sleepModeEnded() {
        //Turn off sleep mode
        userSettingsManager.isCurrentlyInSleepMode = false

        //Start scheduling the notifications again
        NotificationSchedulerJob.scheduleNotification(sharedPrefsProvider)

        //Start monitoring activity
        ActivityMonitorJob.scheduleNextJob()

        //Schedule the next sleep monitoring job
        scheduleJob(userSettingsManager)
    }
}