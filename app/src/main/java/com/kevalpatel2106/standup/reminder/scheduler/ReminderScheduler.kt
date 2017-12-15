package com.kevalpatel2106.standup.reminder.scheduler

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.firebase.jobdispatcher.*
import com.google.android.gms.location.ActivityRecognitionClient
import com.kevalpatel2106.standup.reminder.ReminderConfig
import timber.log.Timber

/**
 * Created by Keval on 13/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object ReminderScheduler {
    /**
     * [FirebaseJobDispatcher] instance to schedule the [JobService] that will display notification
     * to stretch legs.
     */
    private var dispatcher: FirebaseJobDispatcher? = null

    /**
     * [ActivityRecognitionClient] to get the notification updates.
     */
    @SuppressLint("StaticFieldLeak")
    private var activityRecognitionClient: ActivityRecognitionClient? = null

    /**
     * Schedule the stand up notification [JobService] after [ReminderConfig.DETECTION_INTERVAL].
     *
     * @see [ReminderNotifyService]
     */
    internal fun scheduleNextReminder() {
        scheduleReminder(ReminderConfig.STAND_UP_DURATION)
    }

    /**
     * Schedule the stand up notification [JobService] after [afterTimeSecs].
     *
     * @see [ReminderNotifyService]
     */
    private fun scheduleReminder(afterTimeSecs: Int) {
        dispatcher?.let {
            val myJob = it.newJobBuilder()
                    .setService(ReminderNotifyService::class.java)       // the JobService that will be called
                    .setTag(ReminderConfig.SCHEDULER_JOB_TAG)         // uniquely identifies the job
                    .setRecurring(false)
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                    .setTrigger(Trigger.executionWindow(afterTimeSecs, afterTimeSecs + 60))
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .build()
            it.mustSchedule(myJob)
        }
    }

    /**
     * Start the activity detection and schedule the service to display notification at specific interval.
     */
    fun startSchedulerIfNotRunning(context: Context) {

        //Check if the dispatcher already running.
        if (dispatcher == null) {
            //Schedule the notification.
            dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
            scheduleNextReminder()
        }

        //Start activity detection if it is not.
        if (ReminderConfig.ENABLE_ACTIVITY_DETECTION && activityRecognitionClient == null) {

            activityRecognitionClient = ActivityRecognitionClient(context)
            val task = activityRecognitionClient!!.requestActivityUpdates(ReminderConfig.DETECTION_INTERVAL,
                    getActivityDetectionPendingIntent(context))
            task.addOnSuccessListener({
                Timber.i("Activity detector connected successfully.")
            })
            task.addOnFailureListener({
                //Make the client null
                activityRecognitionClient = null

                Timber.e("Activity detector cannot be connected.")
            })
        }
    }

    /**
     * Stop the activity detection and remove all the scheduled job.
     */
    fun shutDown(context: Context) {
        //Stop activity detection
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

        //Remove all future jobs
        dispatcher?.cancelAll()
        dispatcher = null
    }

    /**
     * Gets a PendingIntent to be sent for each context detection.
     */
    private fun getActivityDetectionPendingIntent(context: Context): PendingIntent {
        val intent = Intent(ReminderConfig.DETECTION_BROADCAST_ACTION)

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}