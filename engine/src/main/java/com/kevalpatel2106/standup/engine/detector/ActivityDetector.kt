package com.kevalpatel2106.standup.engine.detector

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.annotation.VisibleForTesting
import com.google.android.gms.location.ActivityRecognitionClient
import com.kevalpatel2106.standup.engine.EngineConfig
import timber.log.Timber


/**
 * Created by Keval on 24/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@SuppressLint("StaticFieldLeak")
internal object ActivityDetector {

    @VisibleForTesting
    internal var activityRecognitionClient: ActivityRecognitionClient? = null

    /**
     * Registers for context recognition updates using [ActivityRecognitionClient.requestActivityUpdates].
     * Registers success and failure callbacks.
     */
    @SuppressLint("VisibleForTests")
    fun startDetection(context: Context,
                       onSuccess: () -> Unit,
                       onFailure: () -> Unit) {
        if (activityRecognitionClient == null) {

            activityRecognitionClient = ActivityRecognitionClient(context)
            val task = activityRecognitionClient!!.requestActivityUpdates(EngineConfig.DETECTION_INTERVAL,
                    getActivityDetectionPendingIntent(context))
            task.addOnSuccessListener({ onSuccess.invoke() })
            task.addOnFailureListener({
                //Make the client null
                activityRecognitionClient = null

                onFailure.invoke()
            })
        }
    }

    /**
     * Removes context recognition updates using [ActivityRecognitionClient.removeActivityUpdates].
     * Registers success and failure callbacks.
     */
    @SuppressLint("VisibleForTests")
    fun stopDetection(context: Context) {
        activityRecognitionClient?.let {
            val task = it.removeActivityUpdates(getActivityDetectionPendingIntent(context))
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
    internal fun getActivityDetectionPendingIntent(context: Context): PendingIntent {
        val intent = Intent(EngineConfig.DETECTION_BROADCAST_ACTION)

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}