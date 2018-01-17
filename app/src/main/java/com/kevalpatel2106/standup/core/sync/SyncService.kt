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
import android.content.Context
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.core.CoreConfig
import com.kevalpatel2106.standup.core.di.DaggerCoreComponent
import com.kevalpatel2106.standup.core.repo.CoreRepo
import com.kevalpatel2106.standup.core.sync.SyncService.Companion.cancelScheduledSync
import com.kevalpatel2106.standup.core.sync.SyncService.Companion.isSyncingCurrently
import com.kevalpatel2106.standup.core.sync.SyncService.Companion.syncNow
import com.kevalpatel2106.standup.misc.UserSessionManager
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
 * - This [JobService] is scheduled to run periodically (untill the next boot) at the interval of
 * [com.kevalpatel2106.standup.misc.UserSettingsManager.syncInterval] milliseconds. User can change this
 * sync interval form the [com.kevalpatel2106.standup.settings.syncing.SyncSettingsFragment].
 *
 * - This [JobService] will only run if internet connection is available.
 *
 * - Periodic syncing should only work if [com.kevalpatel2106.standup.misc.UserSettingsManager.enableBackgroundSync]
 * is true.
 *
 * - If you want to cancel all scheduled sync jobs, call [cancelScheduledSync].
 *
 * Manual Syncing:
 * Application sync instantaneously using [syncNow]. This will ignore all the scheduled [SyncService]
 * jobs and runs instantaneously.
 *
 * Getting the current state:
 * - Whenever the [SyncService] start syncing the activities with the server,
 * [CoreConfig.TAG_RX_SYNC_STARTED] event will get broadcast  on [RxBus]. Interested component can
 * register this [Event] tag on the [RxBus] and get callback when syncing starts.
 *
 * - Whenever the syncing of the activities is completed, [CoreConfig.TAG_RX_SYNC_ENDED] event will
 * get broadcast on [RxBus]. Interested component can register this [Event] tag on the [RxBus] and
 * get callback when syncing completes. This event will broadcast event if the synicng with the server
 * fails.
 *
 * - Any component can query [isSyncingCurrently] to check if the [SyncService] is syncing the activities
 * currently or not?
 *
 * Getting the last sync time:
 * - Time of the last sync job is stored in the [SharedPrefsProvider] with key
 * [com.kevalpatel2106.standup.constants.SharedPreferenceKeys.PREF_KEY_LAST_SYNC_TIME]. This time is
 * in unix milliseconds.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SyncService : JobService() {

    /**
     * [CompositeDisposable] for collecting all [io.reactivex.disposables.Disposable]. It will get
     * cleared when the service stops.
     *
     * @see onDestroy
     */
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    companion object {

        /**
         * Forcefully start syncing all the pending [com.kevalpatel2106.standup.db.userActivity.UserActivity]
         * with the server. This will ignore all the scheduled [SyncService] jobs and runs instantaneously.
         *
         * If the [SyncService] is already running while this method get invoke, call will be discarded.
         *
         * @see SyncServiceHelper.prepareJob
         */
        @SuppressLint("VisibleForTests")
        @JvmStatic
        internal fun syncNow(context: Context) {
            //Schedule the job
            FirebaseJobDispatcher(GooglePlayDriver(context))
                    .mustSchedule(SyncServiceHelper.prepareJob(context))

            Timber.i("Forcing the manual syncing...")
        }

        @SuppressLint("VisibleForTests")
        @JvmStatic
        internal fun scheduleSync(context: Context, interval: Int) {
            //Schedule the job
            FirebaseJobDispatcher(GooglePlayDriver(context))
                    .mustSchedule(SyncServiceHelper.prepareJob(context, interval))
            Timber.i("Next sync job is scheduled after :".plus(interval).plus(" milliseconds."))
        }

        @JvmStatic
        internal fun cancelScheduledSync(context: Context) {
            FirebaseJobDispatcher(GooglePlayDriver(context)).cancel(SyncServiceHelper.SYNC_JOB_TAG)
            Timber.i("All the sync jobs are canceled.")
        }

        /**
         * Flag to let others know if the [SyncService] is running or not? The value of this boolean
         * is modified internally from [SyncService] itself.
         *
         * @see isSyncingCurrently
         */
        private var isSyncing = false

        internal fun isSyncingCurrently() = isSyncing
    }

    @Inject lateinit var userSessionManager: UserSessionManager

    @Inject lateinit var coreRepo: CoreRepo

    override fun onCreate() {
        super.onCreate()

        //Inject dependencies
        DaggerCoreComponent.builder()
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
            coreRepo.sendPendingActivitiesToServer()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        compositeDisposable.add(it)

                        //Let others know sync started.
                        isSyncing = true
                        RxBus.post(Event(CoreConfig.TAG_RX_SYNC_STARTED))
                        Timber.i("Syncing started...")
                    }
                    .doAfterTerminate {
                        jobFinished(job, false)

                        //Save the last syncing time
                        SharedPrefsProvider(this@SyncService).savePreferences(
                                SharedPreferenceKeys.PREF_KEY_LAST_SYNC_TIME,
                                System.currentTimeMillis() - 1000L /* Remove one second to prevent displaying 0 seconds in sync settings. */)

                        //Syncing stopped.
                        isSyncing = false
                        RxBus.post(Event(CoreConfig.TAG_RX_SYNC_ENDED))
                        Timber.i("Syncing completed...")
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