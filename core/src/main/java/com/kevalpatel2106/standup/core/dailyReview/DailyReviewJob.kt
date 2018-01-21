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

package com.kevalpatel2106.standup.core.dailyReview

import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.standup.core.activityMonitor.ActivityMonitorJob
import com.kevalpatel2106.standup.core.di.DaggerCoreComponent
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Keval on 17/01/18.
 * A receiver to get callback whenever the daily review alarm goes off. This receiver will generate
 * the daily review notification and fire it.
 *
 * Due to the background limitations in Android O, we can not let this function run more than 10 secs.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DailyReviewJob : Job() {

    companion object {

        /**
         * Unique tag for the [DailyReviewJob].
         */
        internal const val DAILY_REVIEW_TAG = "daily_review_tag"

        /**
         * Schedule the next [DailyReviewJob]. This job is one shot job.  This method will set tne new job
         * only if [UserSettingsManager.isDailyReviewEnable] is true. Next daily job will be schedule
         * on [DailyReviewHelper.getNextAlarmTime].
         *
         * @return True if the job is scheduled or else false.
         * @see DailyReviewHelper.getNextAlarmTime
         */
        internal fun scheduleJob(userSettingsManager: UserSettingsManager): Boolean {
            return synchronized(ActivityMonitorJob::class) {

                if (!userSettingsManager.isDailyReviewEnable) {
                    cancelScheduledJob()
                    return@synchronized false
                }

                //Arrange the DND start job
                val nextDailyReviewTime = DailyReviewHelper.getNextAlarmTime(userSettingsManager)
                val startJobId = JobRequest.Builder(DailyReviewJob.DAILY_REVIEW_TAG)
                        .setUpdateCurrent(true)
                        .setExact(nextDailyReviewTime - System.currentTimeMillis())
                        .build()
                        .schedule()

                Timber.i("`Daily Review` job with id $startJobId scheduled at $nextDailyReviewTime milliseconds.")
                return@synchronized true
            }
        }

        /**
         * Cancel [DAILY_REVIEW_TAG] job.
         */
        @JvmStatic
        internal fun cancelScheduledJob() {
            JobManager.instance().cancelAllForTag(DAILY_REVIEW_TAG)
        }
    }

    @Inject
    internal lateinit var userSettingsManager: UserSettingsManager

    override fun onRunJob(params: Params): Result {
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this)

        if (!userSettingsManager.isDailyReviewEnable) {
            return Result.SUCCESS
        }

        //TODO Display daily review.
        DailyReviewNotification.notify(context)

        return Result.SUCCESS
    }
}