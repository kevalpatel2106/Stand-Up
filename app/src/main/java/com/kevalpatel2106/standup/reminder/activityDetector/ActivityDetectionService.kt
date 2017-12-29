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

package com.kevalpatel2106.standup.reminder.activityDetector

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.support.annotation.VisibleForTesting
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.kevalpatel2106.standup.BuildConfig
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityHelper
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import com.kevalpatel2106.standup.reminder.ReminderConfig
import com.kevalpatel2106.standup.reminder.repo.ReminderRepo
import com.kevalpatel2106.standup.reminder.repo.ReminderRepoImpl
import com.kevalpatel2106.standup.reminder.scheduler.ReminderScheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*


/**
 * Created by Keval on 25/11/17.
 * This is receiver will receive the updates when the [UserActivity] is changed. [ReminderScheduler]
 * will broadcast intent with action [ReminderConfig.DETECTION_BROADCAST_ACTION] to invoke this
 * receiver.
 *
 * The [Intent] contains list of all the detected activities and their confidence level.
 * This data can be parsed using [ActivityRecognitionResult.extractResult].
 *
 * Once this list of probable user activity is available, it will detect weather the user is sitting
 * or moving/standing based on the [DetectedActivity]. If the user is sitting [onUserSitting] will
 * be called. Iff the user is moving [onUserMoving] will be called.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see DetectedActivity
 * @see ReminderScheduler
 */
class ActivityDetectionService : IntentService(ActivityDetectionService::class.java.name) {

    @VisibleForTesting
    internal var mReminderRepo: ReminderRepo = ReminderRepoImpl()

    @SuppressLint("VisibleForTests")
    override fun onHandleIntent(intent: Intent) {
        val result = ActivityRecognitionResult.extractResult(intent)

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        val detectedActivities = result.probableActivities as ArrayList
        Timber.d("Detected Activities: ".plus(detectedActivities.toString()))

        if (shouldIgnoreThisEvent(detectedActivities)) {
            //Not enough confidence
            //Let's ignore this event.
            return
        }

        if (isUserSitting(detectedActivities)) {
            //User is sitting
            onUserSitting(mReminderRepo)
        } else {
            //User is moving
            onUserMoving(mReminderRepo)
        }

        @Suppress("DEPRECATION")
        if (BuildConfig.DEBUG) {
            val i = Intent(ReminderConfig.DETECTION_BROADCAST_ACTION)
            i.putExtra("com.google.android.location.internal.EXTRA_ACTIVITY_RESULT",
                    i.getByteArrayExtra("com.google.android.location.internal.EXTRA_ACTIVITY_RESULT"))
            LocalBroadcastManager.getInstance(this@ActivityDetectionService).sendBroadcast(i)
        }
    }

    private fun onUserMoving(repo: ReminderRepo) {
        Timber.d("User is MOVING.")

        //Push the notification back to 1 hour
        ReminderScheduler.scheduleNextReminder()

        //Add the new value to database.
        repo.insertNewAndTerminatePreviousActivity(UserActivityHelper
                .createLocalUserActivity(UserActivityType.MOVING))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    private fun onUserSitting(repo: ReminderRepo) {
        Timber.d("User is SITTING.")

        //Add the new value to database.
        repo.insertNewAndTerminatePreviousActivity(UserActivityHelper
                .createLocalUserActivity(UserActivityType.SITTING))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    companion object {

        @JvmStatic
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

        @JvmStatic
        @VisibleForTesting
        internal fun shouldIgnoreThisEvent(detectedActivities: ArrayList<DetectedActivity>): Boolean {
            if (detectedActivities.size <= 0)
                throw IllegalStateException("Detected activity list must have at least one item.")

            sortDescendingByConfidence(detectedActivities)

            if (detectedActivities[0].confidence < ReminderConfig.CONFIDENCE_THRESHOLD) return true

            return when (detectedActivities[0].type) {
                DetectedActivity.STILL -> false
                DetectedActivity.ON_FOOT -> false
                DetectedActivity.WALKING -> false
                DetectedActivity.ON_BICYCLE -> false
                DetectedActivity.IN_VEHICLE -> false
                else -> true
            }
        }

        @VisibleForTesting
        @JvmStatic
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
    }
}