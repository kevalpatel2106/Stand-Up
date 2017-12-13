package com.kevalpatel2106.standup.engine

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

/**
 * Created by Keval on 13/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SchedulerService : JobService() {
    override fun onStopJob(p0: JobParameters?): Boolean {
        return true

    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        //Fire up the notification
        TestNotification.notify(this)

        //Schedule next event
        Engine.scheduleNextNotification()
        return false
    }

}