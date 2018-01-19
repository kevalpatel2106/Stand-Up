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

package com.kevalpatel2106.standup.core.sync

import android.annotation.SuppressLint
import android.support.annotation.VisibleForTesting
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.kevalpatel2106.common.SharedPreferenceKeys
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.core.AsyncJob
import com.kevalpatel2106.standup.core.CoreConfig
import com.kevalpatel2106.standup.core.di.DaggerCoreComponent
import com.kevalpatel2106.standup.core.repo.CoreRepo
import com.kevalpatel2106.standup.core.sync.SyncJob.Companion.cancelScheduledSync
import com.kevalpatel2106.standup.core.sync.SyncJob.Companion.isSyncingCurrently
import com.kevalpatel2106.standup.core.sync.SyncJob.Companion.syncNow
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.rxbus.Event
import com.kevalpatel2106.utils.rxbus.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Keval on 31/12/17.
 * This service is responsible for syncing the [com.kevalpatel2106.standup.db.userActivity.UserActivity]
 * with the server.
 *
 * Periodic Syncing:
 * - This [AsyncJob] is scheduled to run periodically (until the next boot) at the interval of
 * [com.kevalpatel2106.standup.misc.UserSettingsManager.syncInterval] milliseconds. User can change this
 * sync interval form the [com.kevalpatel2106.standup.settings.syncing.SyncSettingsFragment].
 *
 * - This [AsyncJob] will only run if internet connection is available.
 *
 * - Periodic syncing should only work if [com.kevalpatel2106.standup.misc.UserSettingsManager.enableBackgroundSync]
 * is true.
 *
 * - If you want to cancel all scheduled sync jobs, call [cancelScheduledSync].
 *
 * Manual Syncing:
 * Application sync instantaneously using [syncNow]. This will ignore all the scheduled [SyncJob]
 * jobs and runs instantaneously.
 *
 * Getting the current state:
 * - Whenever the [SyncJob] start syncing the activities with the server,
 * [CoreConfig.TAG_RX_SYNC_STARTED] event will get broadcast  on [RxBus]. Interested component can
 * register this [Event] tag on the [RxBus] and get callback when syncing starts.
 *
 * - Whenever the syncing of the activities is completed, [CoreConfig.TAG_RX_SYNC_ENDED] event will
 * get broadcast on [RxBus]. Interested component can register this [Event] tag on the [RxBus] and
 * get callback when syncing completes. This event will broadcast event if the synicng with the server
 * fails.
 *
 * - Any component can query [isSyncingCurrently] to check if the [SyncJob] is syncing the activities
 * currently or not?
 *
 * Getting the last sync time:
 * - Time of the last sync job is stored in the [SharedPrefsProvider] with key
 * [com.kevalpatel2106.standup.constants.SharedPreferenceKeys.PREF_KEY_LAST_SYNC_TIME]. This time is
 * in unix milliseconds.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SyncJob : AsyncJob() {

    /**
     * [CompositeDisposable] for collecting all [io.reactivex.disposables.Disposable]. It will get
     * cleared when the service stops.
     *
     * @see destroyJob
     */
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    companion object {

        /**
         * A unique tag for periodic sync job.
         *
         * @see scheduleSync
         */
        @VisibleForTesting
        internal const val SYNC_JOB_TAG = "sync_job"

        /**
         * Unique tag for manual sync job.
         *
         * @see syncNow
         */
        @VisibleForTesting
        internal const val SYNC_NOW_JOB_TAG = "sync_now_job"

        /**
         * Forcefully start syncing all the pending [com.kevalpatel2106.standup.db.userActivity.UserActivity]
         * with the server. This will ignore all the scheduled [SyncJob] jobs and runs instantaneously.
         *
         * If the [SyncJob] is already running while this method get invoke, call will be discarded.
         */
        @SuppressLint("VisibleForTests")
        @JvmStatic
        internal fun syncNow() {
            synchronized(SyncJob::class) {

                //Schedule the job
                val id = JobRequest.Builder(SYNC_JOB_TAG)
                        .setUpdateCurrent(true)
                        .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                        .startNow()
                        .build()
                        .schedule()

                Timber.i("Forcing the manual syncing with job id: $id")
            }
        }

        @SuppressLint("VisibleForTests")
        @JvmStatic
        internal fun scheduleSync(intervalMills: Long) {
            synchronized(SyncJob::class) {
                //Schedule the job
                val id = JobRequest.Builder(SYNC_JOB_TAG)
                        .setUpdateCurrent(true)
                        .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                        .setPeriodic(intervalMills, CoreConfig.SYNC_SERVICE_PERIOD_TOLERANCE)
                        .build()
                        .schedule()

                Timber.i("Next sync job with id $id is scheduled after :$intervalMills milliseconds.")
            }
        }

        @JvmStatic
        internal fun cancelScheduledSync() {
            JobManager.instance().cancelAllForTag(SYNC_JOB_TAG)
            Timber.i("All the sync jobs are canceled.")
        }

        /**
         * Flag to let others know if the [SyncJob] is running or not? The value of this boolean
         * is modified internally from [SyncJob] itself.
         *
         * @see isSyncingCurrently
         */
        private var isSyncing = false

        internal fun isSyncingCurrently() = isSyncing
    }

    /**
     * [SharedPrefsProvider] for accessing the preferences.
     */
    @Inject
    lateinit var sharedPrefsProvider: SharedPrefsProvider

    /**
     * [UserSessionManager] for getting the user session details.
     */
    @Inject
    lateinit var userSessionManager: UserSessionManager

    @Inject
    lateinit var coreRepo: CoreRepo

    override fun onRunJobAsync(params: Params) {

        //Inject dependencies
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SyncJob)

        if (SyncServiceHelper.shouldSync(userSessionManager)) {

            //Add the new value to database.
            coreRepo.sendPendingActivitiesToServer()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        compositeDisposable.add(it)

                        //Let others know sync started.
                        notifySyncStarted()
                    }
                    .doAfterTerminate {
                        //Let others know sync stopped.
                        notifySyncTerminated(sharedPrefsProvider)

                        destroyJob()
                    }
                    .subscribe({
                        //NO OP
                    }, {
                        //Error.
                        //NO OP
                        Timber.e(it.message)
                    })
        } else {

            //Do nothing.
            //You shouldn't be syncing.
            destroyJob()
        }
    }

    private fun notifySyncTerminated(sharedPrefsProvider: SharedPrefsProvider) {
        //Save the last syncing time
        sharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_LAST_SYNC_TIME,
                System.currentTimeMillis() - 1000L /* Remove one second to prevent displaying 0 seconds in sync settings. */)

        //Syncing stopped.
        isSyncing = false
        RxBus.post(Event(CoreConfig.TAG_RX_SYNC_ENDED))
        Timber.i("Syncing completed...")
    }

    private fun notifySyncStarted() {
        isSyncing = true
        RxBus.post(Event(CoreConfig.TAG_RX_SYNC_STARTED))
        Timber.i("Syncing started...")
    }

    private fun destroyJob() {
        stopJob(Result.SUCCESS)
        compositeDisposable.dispose()
    }
}
