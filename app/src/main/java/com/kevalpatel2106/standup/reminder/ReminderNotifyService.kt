/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.kevalpatel2106.standup.reminder

import android.support.annotation.CallSuper
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

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
        //TODO Schedule next reminder

        ReminderNotification.notify(this@ReminderNotifyService)
        return true /* Wait for the background execution to complete */
    }
}