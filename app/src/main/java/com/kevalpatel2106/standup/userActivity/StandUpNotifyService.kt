package com.kevalpatel2106.standup.userActivity

import android.support.annotation.CallSuper
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.kevalpatel2106.standup.notification.TestStandUpNowNotification
import com.kevalpatel2106.standup.userActivity.detector.ActivityDetector

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class StandUpNotifyService : JobService() {

    @CallSuper
    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    @CallSuper
    override fun onStartJob(p0: JobParameters?): Boolean {
        //Schedule next event
        ActivityDetector.scheduleNextNotification()

        TestStandUpNowNotification.notify(this@StandUpNotifyService)
        return true /*Wait for the background execution to complete*/
    }
}