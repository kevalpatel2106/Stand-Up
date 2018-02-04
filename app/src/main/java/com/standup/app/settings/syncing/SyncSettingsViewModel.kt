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

package com.standup.app.settings.syncing

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.rxbus.RxBus
import com.standup.app.settings.di.DaggerSettingsComponent
import com.standup.core.Core
import com.standup.core.CoreConfig
import com.standup.core.CorePrefsProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Keval on 13/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SyncSettingsViewModel : BaseViewModel {

    @Inject
    lateinit var sharedPrefsProvider: SharedPrefsProvider

    @Inject
    lateinit var core: Core

    @Inject
    lateinit var corePrefsProvider: CorePrefsProvider

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
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SyncSettingsViewModel)
        init()
    }

    private fun init() {
        //Set initial syncing value.
        isSyncing.value = Core.isSyncingCurrently()

        //Register for the sync events
        addDisposable(RxBus.register(CoreConfig.TAG_RX_SYNC_STARTED).subscribe { isSyncing.value = true })
        addDisposable(RxBus.register(CoreConfig.TAG_RX_SYNC_ENDED).subscribe {
            isSyncing.value = false

            lastSyncTime.value = TimeUtils.calculateHumanReadableDurationFromNow(corePrefsProvider.lastSyncTime)
        })

        lastSyncTime.value = corePrefsProvider.lastSyncTime.let {
            if (it <= 0) "" else TimeUtils.calculateHumanReadableDurationFromNow(it)
        }
    }

    /**
     * Start syncing the database with the server right now if the network is available.
     */
    fun manualSync() {
        if (!Core.isSyncingCurrently()) Core.forceSync()
        isSyncing.value = true
    }


    fun onBackgroundSyncPolicyChange() {
        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { core.refresh() }
                .subscribe()
    }
}
