package com.kevalpatel2106.activityengine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import java.util.*


/**
 * Created by Keval on 25/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
abstract class ActivityDetectionReceiver : BroadcastReceiver() {

    protected lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        val result = ActivityRecognitionResult.extractResult(intent)

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        val detectedActivities = result.probableActivities as ArrayList

        //Sort the activity list by confidante level
        Collections.sort(detectedActivities) { p0, p1 -> p0.confidence - p1.confidence }

        //Activity detected.
        when (detectedActivities[0].type) {
            DetectedActivity.STILL -> onUserSitting()
            DetectedActivity.IN_VEHICLE -> onUserSitting()
            DetectedActivity.ON_BICYCLE or DetectedActivity.ON_FOOT or DetectedActivity.WALKING -> onUserMoving()
            DetectedActivity.UNKNOWN or DetectedActivity.TILTING -> {
                /* Do nothing */
            }
        }
    }

    abstract fun onUserMoving()

    abstract fun onUserSitting()
}