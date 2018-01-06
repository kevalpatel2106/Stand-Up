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

package com.kevalpatel2106.standup.reminder.activityMonitor

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.VisibleForTesting
import com.firebase.jobdispatcher.*
import com.google.android.gms.location.DetectedActivity
import com.kevalpatel2106.base.annotations.Helper
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityHelper
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import com.kevalpatel2106.standup.reminder.ReminderConfig
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.UserSessionManager
import java.util.*

/**
 * Created by Keval on 30/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Helper(ActivityMonitorService::class)
internal object ActivityMonitorHelper {
    internal const val ACTIVITY_MONITOR_JOB_TAG = "activity_monitor_job"

    @SuppressLint("VisibleForTests")
    @JvmStatic
    internal fun prepareJob(context: Context): Job {
        return FirebaseJobDispatcher(GooglePlayDriver(context))
                .newJobBuilder()
                .setService(ActivityMonitorService::class.java)       // the JobService that will be called
                .setTag(ACTIVITY_MONITOR_JOB_TAG)         // uniquely identifies the jobParams
                .setRecurring(true)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(getExecutionWindow())
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .build()
    }


    @JvmStatic
    @VisibleForTesting
    internal fun getExecutionWindow(): JobTrigger.ExecutionWindowTrigger {
        return Trigger.executionWindow(
                ReminderConfig.MONITOR_SERVICE_PERIOD - ReminderConfig.MONITOR_SERVICE_PERIOD_TOLERANCE,
                ReminderConfig.MONITOR_SERVICE_PERIOD + ReminderConfig.MONITOR_SERVICE_PERIOD_TOLERANCE)
    }

    internal fun isUserSitting(detectedActivities: ArrayList<DetectedActivity>): Boolean {
        if (detectedActivities.size <= 0)
            throw IllegalStateException("Detected activity list must have at least one item.")

        sortDescendingByConfidence(detectedActivities)

        //Activity detected.
        return when (detectedActivities[0].type) {
            DetectedActivity.STILL -> true
            DetectedActivity.IN_VEHICLE -> true
            DetectedActivity.ON_BICYCLE or DetectedActivity.ON_FOOT or DetectedActivity.WALKING -> false
            DetectedActivity.UNKNOWN or DetectedActivity.TILTING -> {
                throw IllegalStateException("Tilting or unknown activity should not come this far.")
            }
            else -> false
        }
    }

    internal fun shouldIgnoreThisEvent(detectedActivities: ArrayList<DetectedActivity>): Boolean {
        if (detectedActivities.size <= 0)
            throw IllegalStateException("Detected activity list must have at least one item.")

        sortDescendingByConfidence(detectedActivities)

        if (detectedActivities[0].confidence < ReminderConfig.CONFIDENCE_THRESHOLD) return true

        return when (detectedActivities[0].type) {
            DetectedActivity.STILL -> false
            DetectedActivity.ON_FOOT -> false
            DetectedActivity.WALKING -> false
            DetectedActivity.RUNNING -> false
            DetectedActivity.ON_BICYCLE -> false
            DetectedActivity.IN_VEHICLE -> false
            else -> true
        }
    }

    internal fun sortDescendingByConfidence(detectedActivities: ArrayList<DetectedActivity>): ArrayList<DetectedActivity> {
        //Sort the array by confidence level
        //Descending
        Collections.sort(detectedActivities) { p0, p1 ->
            val diff = p1.confidence - p0.confidence

            if (diff == 0) {
                if (p1.type == DetectedActivity.STILL || p1.type == DetectedActivity.IN_VEHICLE) {
                    return@sort 1
                } else {
                    return@sort -1
                }
            } else {
                return@sort diff
            }
        }

        return detectedActivities
    }

    internal fun convertToUserActivity(detectedActivities: ArrayList<DetectedActivity>): UserActivity? {
        if (shouldIgnoreThisEvent(detectedActivities)) {
            //Not enough confidence
            //Let's ignore this event.
            return null
        }

        return if (isUserSitting(detectedActivities)) {
            //User is sitting
            UserActivityHelper.createLocalUserActivity(UserActivityType.SITTING)
        } else {
            //User is moving
            UserActivityHelper.createLocalUserActivity(UserActivityType.MOVING)
        }
    }


    internal fun shouldScheduleNotification(userActivity: UserActivity): Boolean {
        if (userActivity.userActivityType == UserActivityType.MOVING) {

            // Reschedule the notification if the user is currently moving
            return true
        } else if (SharedPrefsProvider.getLongFromPreference(ReminderConfig.PREF_KEY_NEXT_NOTIFICATION_TIME)
                < (System.currentTimeMillis() + TimeUtils.convertToMilli(ReminderConfig.STAND_UP_DURATION.toLong()))) {

            // There is no notification since an hour. That indicates that may be notification job
            // is not scheduled for a long time or it was canceled.
            // Schedule the new job for the future. Later on based on the user activity, we can push
            // the job back.
            return true
        }
        return false
    }

    internal fun shouldMonitoringActivity(): Boolean {
        return UserSessionManager.isUserLoggedIn
    }
}