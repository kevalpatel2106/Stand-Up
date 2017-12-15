package com.kevalpatel2106.standup.reminder.scheduler

import android.support.annotation.CallSuper
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.kevalpatel2106.standup.notification.ReminderNotification

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class ReminderNotifyService : JobService() {

    @CallSuper
    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    @CallSuper
    override fun onStartJob(p0: JobParameters?): Boolean {
        //Schedule next event
        ReminderScheduler.scheduleNextReminder()

        ReminderNotification.notify(this@ReminderNotifyService)
        return true /* Wait for the background execution to complete */
    }
}