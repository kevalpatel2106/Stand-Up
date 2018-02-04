/*
 *  Copyright 2018 Keval Patel.
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

package com.standup.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.annotation.VisibleForTesting
import com.evernote.android.job.JobManager
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.utils.annotations.OnlyForTesting
import com.standup.core.di.DaggerCoreComponent
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Keval on 30/12/17.
 * This is the [BroadcastReceiver] that will receive below system events:
 *
 * 1.[Intent.ACTION_MY_PACKAGE_REPLACED] - Whenever stand up app gets updated, it will cancelJob all
 * the jobs and than after reschedule all the jobs.
 *
 * 2.[Intent.ACTION_BOOT_COMPLETED] - Whenever boot completes, receiver will schedule all the jobs
 * based on user settings in [UserSettingsManager].
 *
 * [onReceive] method will first cancelJob all the jobs scheduled by [JobManager]. Evernote android job
 * registers all the jobs again on [Intent.ACTION_MY_PACKAGE_REPLACED] or [Intent.ACTION_BOOT_COMPLETED].
 * So first unregister all the jobs and re-schedule jobs based on the current settings.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SystemEventReceiver : BroadcastReceiver {

    @Suppress("unused")
    constructor() {
        //Inject dependencies.
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SystemEventReceiver)

    }

    @VisibleForTesting
    @OnlyForTesting
    constructor(core: Core) {
        this.core = core
    }

    @Inject
    lateinit var core: Core

    /**
     * @see BroadcastReceiver.onReceive
     */
    override fun onReceive(context: Context, intent: Intent) {

        //Validate the intent actions
        if (intent.action != Intent.ACTION_BOOT_COMPLETED
                && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED)
            return

        // Cancel all the job
        // Evernote job automatically schedules all the jobs for you on boot complete.
        // So, we are going to cancelJob all the jobs and schedule our own jobs again.
        Core.meltdown()

        core.refresh()
        Timber.i("Rescheduling of all jobs and alarms completed. :-)")
    }
}
