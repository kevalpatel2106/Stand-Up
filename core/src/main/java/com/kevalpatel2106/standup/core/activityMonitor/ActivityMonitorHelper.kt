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

package com.kevalpatel2106.standup.core.activityMonitor

import android.support.annotation.VisibleForTesting
import com.evernote.android.job.JobManager
import com.google.android.gms.location.DetectedActivity
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityHelper
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.standup.core.CoreConfig
import com.kevalpatel2106.utils.annotations.Helper
import java.util.*

/**
 * Created by Keval on 30/12/17.
 * Helper class for [ActivityMonitorJob].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Helper(ActivityMonitorJob::class)
internal object ActivityMonitorHelper {

    /**
     * Check if the user is sitting or not? If the activity with the most confidence in
     * [detectedActivities] list is [DetectedActivity.STILL] or [DetectedActivity.IN_VEHICLE],
     * it will return true else it will return false.
     *
     * NOTE: [detectedActivities] must be sorted by the descending order of [DetectedActivity.getConfidence].
     *
     * @return True if the user is sitting or else false.
     * @throws IllegalStateException If the most confidante [DetectedActivity] in [detectedActivities]
     * is [DetectedActivity.TILTING] or [DetectedActivity.UNKNOWN].
     */
    @VisibleForTesting
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

    /**
     * This method will check if the given [detectedActivities] list can be ignore or not?
     *
     * If the list satisfy any of below points, than that can be ignored.
     * - The activity with the highest confidence level in [detectedActivities] must have the
     * confidence level more than [CoreConfig.CONFIDENCE_THRESHOLD].
     * - If the detected activity is other than [DetectedActivity.STILL], [DetectedActivity.ON_FOOT],
     * [DetectedActivity.WALKING], [DetectedActivity.RUNNING], [DetectedActivity.ON_BICYCLE] or
     * [DetectedActivity.IN_VEHICLE].
     *
     * @throws IllegalStateException If the [detectedActivities] is empty.
     */
    internal fun shouldIgnoreThisEvent(detectedActivities: ArrayList<DetectedActivity>): Boolean {
        if (detectedActivities.size <= 0)
            throw IllegalStateException("Detected activity list must have at least one item.")

        sortDescendingByConfidence(detectedActivities)

        if (detectedActivities[0].confidence < CoreConfig.CONFIDENCE_THRESHOLD) return true

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

    /**
     * Sort the given [detectedActivities] list in the descending order of the [DetectedActivity.getConfidence].
     */
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

    /**
     * Convert the [detectedActivities] list into the [UserActivity] or return null if
     * [detectedActivities] can be ignore.
     *
     * @see shouldIgnoreThisEvent
     */
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

    /**
     * Check if currently [ActivityMonitorJob] should be monitoring user activity base on the
     * [userSessionManager]?
     *
     * @return True if it is okay to monitor the user activity or else false.
     */
    internal fun shouldMonitoringActivity(userSessionManager: UserSessionManager,
                                          userSettingsManager: UserSettingsManager): Boolean {
        return userSessionManager.isUserLoggedIn && userSettingsManager.isCurrentlyInSleepMode
    }

    /**
     * @return True if the [ActivityMonitorJob] is scheduled else false.
     */
    internal fun isAnyJobScheduled(): Boolean = JobManager.instance()
            .getAllJobRequestsForTag(ActivityMonitorJob.ACTIVITY_MONITOR_JOB_TAG).isNotEmpty()
}
