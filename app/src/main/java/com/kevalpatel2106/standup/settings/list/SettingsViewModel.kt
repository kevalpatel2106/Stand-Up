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

package com.kevalpatel2106.standup.settings.list

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v4.app.Fragment
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.authentication.logout.Logout
import com.kevalpatel2106.standup.settings.di.DaggerSettingsComponent
import com.kevalpatel2106.standup.settings.dndSettings.DndSettingsDetailActivity
import com.kevalpatel2106.standup.settings.dndSettings.DndSettingsFragment
import com.kevalpatel2106.standup.settings.notificationsSettings.NotifficationSettingsFragment
import com.kevalpatel2106.standup.settings.notificationsSettings.NotificationSettingsDetailActivity
import com.kevalpatel2106.standup.settings.syncSettings.SyncSettingsDetailActivity
import com.kevalpatel2106.standup.settings.syncSettings.SyncSettingsFragment
import javax.inject.Inject

/**
 * Created by Keval on 11/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SettingsViewModel : BaseViewModel {

    @Inject lateinit var logout: Logout

    internal var detailFragment = MutableLiveData<Fragment>()

    internal var logoutInProgress = MutableLiveData<Boolean>()

    internal var settingsItems = MutableLiveData<ArrayList<SettingsItem>>()

    internal var isTwoPane = false

    @VisibleForTesting
    constructor(logout: Logout) {
        this.logout = logout

        init()
    }

    @Suppress("unused")
    constructor() {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.appComponent)
                .build()
                .inject(this@SettingsViewModel)

        init()
    }

    fun init() {
        settingsItems.value = ArrayList()
        logoutInProgress.value = false

        prepareSettingsList()
    }

    private fun prepareSettingsList() {
        settingsItems.value?.let {
            //Add sync
            it.add(SettingsItem(SettingsId.SYNC, "Sync", R.drawable.ic_sync))

            //Add DND
            it.add(SettingsItem(SettingsId.DND, "DND", R.drawable.ic_dnd_on))

            //Add Notifications
            it.add(SettingsItem(SettingsId.NOTIFICATION, "Notifications", R.drawable.ic_notifications_bell))

            //Add Sleep hour
            it.add(SettingsItem(SettingsId.LOGOUT, "Logout", R.drawable.ic_logout))

            //Publish the update.
            settingsItems.value = it
        }
    }

    fun onSettingsClicked(context: Context, settingsItem: SettingsItem) {
        when (settingsItem.id) {
            SettingsId.SYNC -> {
                if (isTwoPane) {
                    detailFragment.value = SyncSettingsFragment.getNewInstance()
                } else {
                    SyncSettingsDetailActivity.launch(context)
                }
            }
            SettingsId.DND -> {
                if (isTwoPane) {
                    detailFragment.value = DndSettingsFragment.getNewInstance()
                } else {
                    DndSettingsDetailActivity.launch(context)
                }
            }
            SettingsId.NOTIFICATION -> {
                if (isTwoPane) {
                    detailFragment.value = NotifficationSettingsFragment.getNewInstance()
                } else {
                    NotificationSettingsDetailActivity.launch(context)
                }
            }
            SettingsId.LOGOUT -> {
                logoutInProgress.value = true
                logout.logout()
                return
            }
        }

        //Set selection
        if (isTwoPane) {
            settingsItems.value?.forEach { it.isSelected = false }
            settingsItem.isSelected = true

            //Publish the update.
            settingsItems.value = settingsItems.value
        }
    }
}
