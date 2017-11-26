package com.kevalpatel2106.activityengine

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.annotation.VisibleForTesting
import com.google.android.gms.location.ActivityRecognitionClient
import com.kevalpatel2106.activityengine.DetectorConfig.DETECTION_BROADCAST_ACTION
import timber.log.Timber


/**
 * Created by Keval on 24/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@SuppressLint("StaticFieldLeak")
class ActivityDetector(private val context: Context) {

    private var isUpdatesRegistered = false

    @SuppressLint("StaticFieldLeak")
    @VisibleForTesting
    internal var activityRecognitionClient = ActivityRecognitionClient(context)

    /**
     * Registers for context recognition updates using [ActivityRecognitionClient.requestActivityUpdates].
     * Registers success and failure callbacks.
     */
    @SuppressLint("VisibleForTests")
    fun startDetection() {
        val task = activityRecognitionClient.requestActivityUpdates(DetectorConfig.DETECTION_INTERVAL,
                getActivityDetectionPendingIntent())

        task.addOnSuccessListener({
            Timber.i("Activity detector connected successfully.")
            isUpdatesRegistered = true
        })

        task.addOnFailureListener({
            Timber.e("Activity detector cannot be connected.")
            isUpdatesRegistered = false
        })
    }

    /**
     * Removes context recognition updates using [ActivityRecognitionClient.removeActivityUpdates].
     * Registers success and failure callbacks.
     */
    @SuppressLint("VisibleForTests")
    fun stopDetection() {
        val task = activityRecognitionClient.removeActivityUpdates(getActivityDetectionPendingIntent())
        task.addOnSuccessListener({
            Timber.i("Activity detector connection successfully terminated.")
            isUpdatesRegistered = false
        })

        task.addOnFailureListener({
            Timber.i("Error occurred while terminating context detector connection .")
            isUpdatesRegistered = true
        })
    }

    /**
     * Gets a PendingIntent to be sent for each context detection.
     */
    @VisibleForTesting
    internal fun getActivityDetectionPendingIntent(): PendingIntent {
        val intent = Intent(DETECTION_BROADCAST_ACTION)

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}