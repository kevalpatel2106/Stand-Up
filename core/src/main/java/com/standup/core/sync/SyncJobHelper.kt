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

package com.standup.core.sync

import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.utils.annotations.Helper
import com.kevalpatel2106.utils.rxbus.Event
import com.kevalpatel2106.utils.rxbus.RxBus
import com.standup.core.CoreConfig
import com.standup.core.CorePrefsProvider
import com.standup.core.sync.SyncJob.Companion.isSyncing

/**
 * Created by Keval on 05/01/18.
 * Helper class for [SyncJob].
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 * @see SyncJob
 */
@Helper(SyncJob::class)
internal object SyncJobHelper {

    /**
     * Check if the [SyncJob] should sync the data with the server?
     *
     * @param userSessionManager [UserSessionManager] for getting the current session information.
     * @param userSettingsManager [UserSettingsManager] for getting the settings information.
     * @return True if sync should happen. False if sync should not perform. (Bad timing for syncing.)
     */
    @JvmStatic
    internal fun shouldRunJob(userSessionManager: UserSessionManager,
                              userSettingsManager: UserSettingsManager): Boolean {
        return userSessionManager.isUserLoggedIn && userSettingsManager.enableBackgroundSync
    }


    /**
     * Broadcast that sync is started using [RxBus] event with tag [CoreConfig.TAG_RX_SYNC_STARTED].
     * This will also set the value of [isSyncing].
     *
     * @see RxBus
     */
    @VisibleForTesting
    internal fun notifySyncStarted() {
        SyncJob.isSyncing = true
        RxBus.post(Event(CoreConfig.TAG_RX_SYNC_STARTED))
    }

    /**
     * Broadcast that sync is completed using [RxBus] event with tag [CoreConfig.TAG_RX_SYNC_ENDED].
     * This will also reset the value of [isSyncing] and save the last sync time in [CorePrefsProvider].
     *
     * @see RxBus
     * @see CorePrefsProvider
     */
    @VisibleForTesting
    internal fun notifySyncTerminated(corePrefsProvider: CorePrefsProvider) {
        //Save the last syncing time
        corePrefsProvider.saveLastSyncTime(System.currentTimeMillis()
                - 1_000L /* Remove one second to prevent displaying 0 seconds in sync settings. */)

        //Syncing stopped.
        isSyncing = false
        RxBus.post(Event(CoreConfig.TAG_RX_SYNC_ENDED))
    }
}
