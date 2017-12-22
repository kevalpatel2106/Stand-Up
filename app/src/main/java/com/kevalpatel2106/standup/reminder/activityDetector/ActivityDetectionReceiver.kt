package com.kevalpatel2106.standup.reminder.activityDetector

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.annotation.VisibleForTesting
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.kevalpatel2106.standup.reminder.ReminderConfig
import com.kevalpatel2106.standup.reminder.scheduler.ReminderScheduler
import com.kevalpatel2106.standup.userActivity.UserActivity
import com.kevalpatel2106.standup.userActivity.UserActivityHelper
import com.kevalpatel2106.standup.userActivity.UserActivityType
import com.kevalpatel2106.standup.userActivity.repo.UserActivityRepo
import com.kevalpatel2106.standup.userActivity.repo.UserActivityRepoImpl
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
class ActivityDetectionReceiver : BroadcastReceiver() {

    private lateinit var context: Context

    @VisibleForTesting
    internal var mUserActivityRepo: UserActivityRepo = UserActivityRepoImpl()

    @SuppressLint("VisibleForTests")
    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
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
            onUserSitting(mUserActivityRepo)
        } else {
            //User is moving
            onUserMoving(mUserActivityRepo)
        }
    }

    private fun onUserMoving(repo: UserActivityRepo) {
        Timber.d("User is MOVING.")

        //Push the notification back to 1 hour
        ReminderScheduler.scheduleNextReminder()

        //Add the new value to database.
        repo.insertNewAndTerminatePreviousActivity(UserActivityHelper
                .createLocalUserActivity(UserActivityType.MOVING))
    }

    private fun onUserSitting(repo: UserActivityRepo) {
        Timber.d("User is SITTING.")

        //Add the new value to database.
        repo.insertNewAndTerminatePreviousActivity(UserActivityHelper
                .createLocalUserActivity(UserActivityType.SITTING))
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