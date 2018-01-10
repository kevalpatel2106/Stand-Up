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

import android.annotation.SuppressLint
import android.content.Context
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.kevalpatel2106.standup.BaseApplication
import com.kevalpatel2106.standup.reminder.di.DaggerReminderComponent
import com.kevalpatel2106.standup.reminder.repo.ReminderRepo
import com.kevalpatel2106.utils.UserSessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Keval on 31/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SyncService : JobService() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    companion object {
        @SuppressLint("VisibleForTests")
        @JvmStatic
        internal fun syncNow(context: Context) {
            //Schedule the job
            FirebaseJobDispatcher(GooglePlayDriver(context))
                    .mustSchedule(SyncServiceHelper.prepareJob(context))
        }

        @JvmStatic
        internal fun cancel(context: Context) {
            FirebaseJobDispatcher(GooglePlayDriver(context)).cancel(SyncServiceHelper.SYNC_JOB_TAG)
        }
    }

    @Inject lateinit var userSessionManager: UserSessionManager

    @Inject lateinit var reminderRepo: ReminderRepo

    override fun onCreate() {
        super.onCreate()

        DaggerReminderComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SyncService)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    @SuppressLint("VisibleForTests")
    override fun onStartJob(job: JobParameters): Boolean {
        Timber.d("Syncing job started.")

        if (SyncServiceHelper.shouldSync(userSessionManager)) {
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
}