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

package com.kevalpatel2106.standup.settings.syncing

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.core.CoreConfig
import com.kevalpatel2106.standup.core.sync.SyncService
import com.kevalpatel2106.standup.settings.di.DaggerSettingsComponent
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.rxbus.RxBus
import javax.inject.Inject

/**
 * Created by Keval on 13/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SyncSettingsViewModel : BaseViewModel {

    @Inject lateinit var sharedPrefsProvider: SharedPrefsProvider

    internal val isSyncing = MutableLiveData<Boolean>()
    internal val lastSyncTime = MutableLiveData<String>()

    @VisibleForTesting
    constructor(sharedPrefsProvider: SharedPrefsProvider) {
        this.sharedPrefsProvider = sharedPrefsProvider
        init()
    }

    @Suppress("unused")
    constructor() {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.appComponent)
                .build()
                .inject(this@SyncSettingsViewModel)
        init()
    }

    private fun init() {
        //Set initial syncing value.
        isSyncing.value = SyncService.isSyncingCurrently()

        //Register for the sync events
        addDisposable(RxBus.register(CoreConfig.TAG_RX_SYNC_STARTED).subscribe { isSyncing.value = true })
        addDisposable(RxBus.register(CoreConfig.TAG_RX_SYNC_ENDED).subscribe {
            isSyncing.value = false

            lastSyncTime.value = TimeUtils.calculateHumanReadableDurationFromNow(sharedPrefsProvider
                    .getLongFromPreference(SharedPreferenceKeys.PREF_KEY_LAST_SYNC_TIME))
        })

        lastSyncTime.value = sharedPrefsProvider.getLongFromPreference(SharedPreferenceKeys.PREF_KEY_LAST_SYNC_TIME).let {
            if (it <= 0) "" else TimeUtils.calculateHumanReadableDurationFromNow(it)
        }
    }

    /**
     * Start syncing the database with the server right now if the network is available.
     */
    fun manualSync(context: Context) {
        if (!SyncService.isSyncingCurrently()) SyncService.syncNow(context)
        isSyncing.value = true
    }


    fun onBackgroundSyncPolicyChange(context: Context, isEnabled: Boolean, interval: Int) {

        //Canceled current job
        SyncService.cancelScheduledSync(context)

        //reschedule the job if background sync is enabled.
        if (isEnabled) {
            SyncService.scheduleSync(context, interval)
        }
    }
}