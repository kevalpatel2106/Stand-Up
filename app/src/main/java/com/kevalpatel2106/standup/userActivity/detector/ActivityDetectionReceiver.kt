package com.kevalpatel2106.standup.userActivity.detector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.kevalpatel2106.standup.BuildConfig
import com.kevalpatel2106.standup.StandUpDb
import com.kevalpatel2106.standup.userActivity.UserActivity
import com.kevalpatel2106.standup.userActivity.UserActivityType
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import java.util.*


/**
 * Created by Keval on 25/11/17.
 * This is receiver will receive the updates when the [UserActivity] is changed. [ActivityDetector]
 * will broadcast intent with action [DetectorConfig.DETECTION_BROADCAST_ACTION] to invoke this
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
 * @see ActivityDetector
 */
class ActivityDetectionReceiver : BroadcastReceiver() {

    private lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        val result = ActivityRecognitionResult.extractResult(intent)

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        val detectedActivities = result.probableActivities as ArrayList
        Timber.d("Detected Activities: ".plus(detectedActivities.toString()))

        //Sort the activity list by confidante level
        Collections.sort(detectedActivities) { p0, p1 -> p1.confidence - p0.confidence }

        if (BuildConfig.DEBUG) {
            ActivityUpdateNotification.notify(context, detectedActivities)
        }

        //Activity detected.
        when (detectedActivities[0].type) {
            DetectedActivity.STILL -> onUserSitting()
            DetectedActivity.IN_VEHICLE -> onUserSitting()
            DetectedActivity.ON_BICYCLE or DetectedActivity.ON_FOOT or DetectedActivity.WALKING -> onUserMoving()
            DetectedActivity.UNKNOWN or DetectedActivity.TILTING -> {
                /* Do nothing */
                Timber.e("Unknown activity detected.")
            }
        }
    }

    private fun onUserMoving() {
        Timber.d("User is MOVING.")

        //Schedule the next job after 1 hour
        ActivityDetector.scheduleNextNotification()

        launch {
            StandUpDb.getDb().userActivityDao()
                    .insertNewAndTerminatePreviousAcivity(UserActivity
                            .createLocalUserActivity(UserActivityType.MOVING))
        }
    }

    private fun onUserSitting() {
        Timber.d("User is SITTING.")

        //Add the new value to database.
        launch {
            StandUpDb.getDb().userActivityDao()
                    .insertNewAndTerminatePreviousAcivity(UserActivity
                            .createLocalUserActivity(UserActivityType.SITTING))
        }
    }
}