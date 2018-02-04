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

package com.standup.core.reminder

import com.evernote.android.job.JobManager
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.utils.annotations.Helper
import com.standup.core.sleepManager.SleepModeMonitoringHelper

/**
 * Created by Keval on 05/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Helper(NotificationSchedulerJob::class)
internal object NotificationSchedulerHelper {

    /**
     * Is it okay to run/schedule the [NotificationSchedulerJob]?
     *
     * [NotificationSchedulerJob] should only be running if DND is turned off (i.e.
     * [UserSettingsManager.isCurrentlyDndEnable] is false), Sleep is turned off (i.e.
     * [UserSettingsManager.isCurrentlyInSleepMode] is false) and user is logged in (i.e.
     * [UserSessionManager.isUserLoggedIn] is true.).
     *
     * @return True if application is good to schedule/run the [NotificationSchedulerJob] or else false.
     */
    internal fun shouldDisplayNotification(userSessionManager: UserSessionManager,
                                           userSettingsManager: UserSettingsManager): Boolean {
        return userSessionManager.isUserLoggedIn
                && !userSettingsManager.isCurrentlyInSleepMode
                && !SleepModeMonitoringHelper.isCurrentlyInSleepMode(userSettingsManager)
                && !userSettingsManager.isCurrentlyDndEnable
    }

    /**
     * Check if any [NotificationSchedulerJob] is scheduled?
     *
     * @return True if [NotificationSchedulerJob] is scheduled else false.
     * @see JobManager.getAllJobRequestsForTag
     */
    internal fun isReminderScheduled(): Boolean = JobManager.instance()
            .getAllJobRequestsForTag(NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG)
            .isNotEmpty()
}
