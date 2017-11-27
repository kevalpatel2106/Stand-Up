package com.kevalpatel2106.activityengine

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.annotation.VisibleForTesting
import com.google.android.gms.location.ActivityRecognitionClient
import timber.log.Timber


/**
 * Created by Keval on 24/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@SuppressLint("StaticFieldLeak")
object ActivityDetector {

    private lateinit var context: Context

    @VisibleForTesting
    internal var activityRecognitionClient: ActivityRecognitionClient? = null

    fun init(context: Context) {
        this.context = context
    }

    /**
     * Registers for context recognition updates using [ActivityRecognitionClient.requestActivityUpdates].
     * Registers success and failure callbacks.
     */
    @SuppressLint("VisibleForTests")
    fun startDetection() {
        if (activityRecognitionClient == null) {

            activityRecognitionClient = ActivityRecognitionClient(context)
            val task = activityRecognitionClient!!.requestActivityUpdates(DetectorConfig.DETECTION_INTERVAL,
                    getActivityDetectionPendingIntent())

            task.addOnSuccessListener({
                //Do nothing
                Timber.i("Activity detector connected successfully.")
            })

            task.addOnFailureListener({
                Timber.e("Activity detector cannot be connected.")

                //Make the client null
                activityRecognitionClient = null
            })
        }
    }

    /**
     * Removes context recognition updates using [ActivityRecognitionClient.removeActivityUpdates].
     * Registers success and failure callbacks.
     */
    @SuppressLint("VisibleForTests")
    fun stopDetection() {
        activityRecognitionClient?.let {
            val task = activityRecognitionClient!!.removeActivityUpdates(getActivityDetectionPendingIntent())
            task.addOnSuccessListener({
                Timber.i("Activity detector connection successfully terminated.")

                //Make the client null
                activityRecognitionClient = null
            })

            task.addOnFailureListener({
                Timber.i("Error occurred while terminating context detector connection .")
            })
        }
    }

    /**
     * Gets a PendingIntent to be sent for each context detection.
     */
    @VisibleForTesting
    internal fun getActivityDetectionPendingIntent(): PendingIntent {
        val intent = Intent(DetectorConfig.DETECTION_BROADCAST_ACTION)

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}