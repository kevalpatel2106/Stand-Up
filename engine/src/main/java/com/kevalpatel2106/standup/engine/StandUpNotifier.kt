package com.kevalpatel2106.standup.engine

import android.support.annotation.CallSuper
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

/**
 * Created by Keval on 13/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
abstract class StandUpNotifier : JobService() {

    @CallSuper
    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    @CallSuper
    override fun onStartJob(p0: JobParameters?): Boolean {
        //Schedule next event
        Engine.scheduleNextNotification()

        notifyUserToStandUp()
        return false
    }

    abstract fun notifyUserToStandUp()
}