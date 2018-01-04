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

package com.kevalpatel2106.standup.reminder.sync

import android.content.Context
import android.support.annotation.VisibleForTesting
import com.firebase.jobdispatcher.*
import com.kevalpatel2106.standup.reminder.repo.ReminderRepo
import com.kevalpatel2106.standup.reminder.repo.ReminderRepoImpl
import com.kevalpatel2106.utils.UserSessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by Keval on 31/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SyncService : JobService() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @VisibleForTesting
    internal var reminderRepo: ReminderRepo = ReminderRepoImpl()

    companion object {

        private const val SYNC_JOB_TAG = "sync_job"

        @JvmStatic
        internal fun syncNow(context: Context) {

            val monitoringJob = FirebaseJobDispatcher(GooglePlayDriver(context))
                    .newJobBuilder()
                    .setService(SyncService::class.java)       // the JobService that will be called
                    .setTag(SYNC_JOB_TAG)         // uniquely identifies the job
                    .setRecurring(false)
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                    .setTrigger(Trigger.NOW)
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    .addConstraint(Constraint.ON_ANY_NETWORK)
                    .build()

            //Schedule the job
            FirebaseJobDispatcher(GooglePlayDriver(context)).mustSchedule(monitoringJob)
        }

        @JvmStatic
        internal fun cancel(context: Context) {
            FirebaseJobDispatcher(GooglePlayDriver(context)).cancel(SYNC_JOB_TAG)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onStartJob(job: JobParameters): Boolean {
        Timber.d("Syncing job started.")

        if (shouldSync()) {
            //Add the new value to database.
            reminderRepo.sendPendingActivitiesToServer()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        compositeDisposable.add(it)
                    }
                    .doAfterTerminate {
                        jobFinished(job, false)
                    }
                    .subscribe({
                        //NO OP
                    }, {
                        //Error.
                        //NO OP
                        Timber.e(it.message)
                    })
            return true
        } else {

            //Do nothing.
            //You shouldn't be syncing.
            return false
        }
    }

    override fun onStopJob(job: JobParameters?): Boolean {
        return true    //Job done. Wait for the jobParams to finish
    }

    internal fun shouldSync(): Boolean {
        return UserSessionManager.isUserLoggedIn
    }
}