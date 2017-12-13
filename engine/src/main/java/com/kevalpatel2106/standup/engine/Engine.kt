package com.kevalpatel2106.standup.engine

import android.content.Context
import com.firebase.jobdispatcher.*
import com.kevalpatel2106.standup.engine.detector.ActivityDetector
import timber.log.Timber

/**
 * Created by Keval on 13/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object Engine {
    private var dispatcher: FirebaseJobDispatcher? = null

    internal fun scheduleNextNotification() {
        scheduleNotification(EngineConfig.STAND_UP_DURATION)
    }

    internal fun scheduleNotification(afterTimeSecs: Int) {
        dispatcher?.let {
            val myJob = it.newJobBuilder()
                    .setService(SchedulerService::class.java)       // the JobService that will be called
                    .setTag(EngineConfig.SCHEDULER_JOB_TAG)         // uniquely identifies the job
                    .setRecurring(false)
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
//                    .setTrigger(Trigger.executionWindow(afterTimeSecs, afterTimeSecs + 60))
                    .setTrigger(Trigger.NOW)
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .build()
            it.mustSchedule(myJob)
        }

    }

    internal fun removeAll() {
        dispatcher?.cancelAll()
    }

    fun startEngine(context: Context) {
        if (dispatcher == null) {
            dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
            scheduleNextNotification()
        }

        ActivityDetector.startDetection(context, {
            //Do nothing
            Timber.i("Activity detector connected successfully.")
        }, {
            Timber.e("Activity detector cannot be connected.")
        })
    }

    fun shutDown(context: Context) {
        removeAll()
        dispatcher = null

        ActivityDetector.stopDetection(context)
    }
}