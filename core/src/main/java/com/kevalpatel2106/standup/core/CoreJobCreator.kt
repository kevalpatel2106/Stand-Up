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

import android.app.Application
import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.evernote.android.job.JobManager
import com.kevalpatel2106.standup.core.activityMonitor.ActivityMonitorJob
import com.kevalpatel2106.standup.core.dndManager.AutoDndMonitoringJob
import com.kevalpatel2106.standup.core.reminder.NotificationSchedulerJob
import com.kevalpatel2106.standup.core.sleepManager.SleepModeMonitoringJob
import com.kevalpatel2106.standup.core.sync.SyncJob


/**
 * Created by Keval on 17/01/18.
 * Evernote android-job creator.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 * @see [Evernote Job](https://github.com/evernote/android-job#usage)
 */
class CoreJobCreator : JobCreator {

    companion object {

        /**
         * Initialize the instance of the [CoreJobCreator]. Notify the [JobManager] to use it to
         * run the job based on the tag. The [JobCreator] maps a job tag to a specific job class.
         *
         * [CoreJobCreator] must have the single instance throughout the application. So the best
         * place to call this method is in the [Application] class.
         *
         * @see com.kevalpatel2106.common.application.BaseApplication
         * @see [Evernote Job](https://github.com/evernote/android-job#usage)
         */
        fun init(application: Application) {
            JobManager.create(application).addJobCreator(CoreJobCreator())
        }
    }

    /**
     * Get the job to run based on the tag provided while creating the job.
     *
     * Mapping:
     * - [ActivityMonitorJob.ACTIVITY_MONITOR_JOB_TAG] -> [ActivityMonitorJob]
     * - [SyncJob.SYNC_JOB_TAG] -> [SyncJob]
     * - [SyncJob.SYNC_NOW_JOB_TAG] -> [SyncJob]
     * - [NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG] -> [NotificationSchedulerJob]
     * - [AutoDndMonitoringJob.AUTO_DND_START_JOB_TAG] -> [AutoDndMonitoringJob]
     * - [AutoDndMonitoringJob.AUTO_DND_END_JOB_TAG] -> [AutoDndMonitoringJob]
     * - [SleepModeMonitoringJob.SLEEP_MODE_START_JOB_TAG] -> [SleepModeMonitoringJob]
     * - [SleepModeMonitoringJob.SLEEP_MODE_END_JOB_TAG] -> [SleepModeMonitoringJob]
     *
     * @throws IllegalStateException if the job for the [tag] not declared.
     * @see [Evernote Job](https://github.com/evernote/android-job#usage)
     */
    override fun create(tag: String): Job? {
        return when (tag) {
            ActivityMonitorJob.ACTIVITY_MONITOR_JOB_TAG -> ActivityMonitorJob()
            SyncJob.SYNC_JOB_TAG -> SyncJob()
            SyncJob.SYNC_NOW_JOB_TAG -> SyncJob()
            NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG -> NotificationSchedulerJob()
            AutoDndMonitoringJob.AUTO_DND_START_JOB_TAG -> AutoDndMonitoringJob()
            AutoDndMonitoringJob.AUTO_DND_END_JOB_TAG -> AutoDndMonitoringJob()
            SleepModeMonitoringJob.SLEEP_MODE_START_JOB_TAG -> SleepModeMonitoringJob()
            SleepModeMonitoringJob.SLEEP_MODE_END_JOB_TAG -> SleepModeMonitoringJob()
            else -> throw IllegalStateException("Did you forget to add job for $tag in CoreJobCreator?")
        }
    }
}