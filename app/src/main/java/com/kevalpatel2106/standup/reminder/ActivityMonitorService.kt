/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.reminder

import android.content.Context
import com.firebase.jobdispatcher.*
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.common.api.GoogleApiClient


/**
 * Created by Keval on 30/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class ActivityMonitorService : JobService() {

    private var googleApiClient: GoogleApiClient? = null

    companion object {

        fun scheduleMonitoringJob(context: Context) {

            //Build the job
            val executionWindow = Trigger.executionWindow(
                    ReminderConfig.MONITOR_SERVICE_PERIOD - ReminderConfig.MONITOR_SERVICE_PERIOD_TOLERANCE,
                    ReminderConfig.MONITOR_SERVICE_PERIOD + ReminderConfig.MONITOR_SERVICE_PERIOD_TOLERANCE)

            val monitoringJob = FirebaseJobDispatcher(GooglePlayDriver(context))
                    .newJobBuilder()
                    .setService(ActivityMonitorService::class.java)       // the JobService that will be called
                    .setTag(ReminderConfig.ACTIVITY_MONITOR_JOB_TAG)         // uniquely identifies the job
                    .setRecurring(true)
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                    .setTrigger(executionWindow)
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    .build()

            //Schedule the job
            FirebaseJobDispatcher(GooglePlayDriver(context)).mustSchedule(monitoringJob)
        }

        fun cancel(context: Context) {
            FirebaseJobDispatcher(GooglePlayDriver(context))
                    .cancel(ReminderConfig.ACTIVITY_MONITOR_JOB_TAG)
        }
    }

    override fun onStopJob(job: JobParameters?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartJob(job: JobParameters?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getCurrentUserActivity(context: Context): Boolean {
        googleApiClient?.let {

            Awareness.getSnapshotClient(context).detectedActivity
                    .addOnSuccessListener({
                        val probableActivity = it
                    })
        }
        return true
    }
}