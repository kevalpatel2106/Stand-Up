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

package com.standup.app.settings.list

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v4.app.Fragment
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.utils.alert
import com.standup.app.settings.R
import com.standup.app.settings.dailyReview.DailyReviewSettingsDetailActivity
import com.standup.app.settings.dailyReview.DailyReviewSettingsFragment
import com.standup.app.settings.di.DaggerSettingsComponent
import com.standup.app.settings.dnd.DndSettingsDetailActivity
import com.standup.app.settings.dnd.DndSettingsFragment
import com.standup.app.settings.notifications.NotificationSettingsDetailActivity
import com.standup.app.settings.notifications.NotificationSettingsFragment
import com.standup.app.settings.syncing.SyncSettingsDetailActivity
import com.standup.app.settings.syncing.SyncSettingsFragment

/**
 * Created by Keval on 11/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class SettingsViewModel : BaseViewModel {

    internal var detailFragment = MutableLiveData<Fragment>()

    internal var logoutInProgress = MutableLiveData<Boolean>()

    internal var settingsItems = MutableLiveData<ArrayList<SettingsItem>>()

    internal var isTwoPane = false

    @Suppress("unused")
    constructor() {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
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

            //Add daily review
            it.add(SettingsItem(SettingsId.DAILY_REVIEW, "Daily Review", R.drawable.ic_daily_review_notification))

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
                    detailFragment.value = NotificationSettingsFragment.getNewInstance()
                } else {
                    NotificationSettingsDetailActivity.launch(context)
                }
            }
            SettingsId.DAILY_REVIEW -> {
                if (isTwoPane) {
                    detailFragment.value = DailyReviewSettingsFragment.getNewInstance()
                } else {
                    DailyReviewSettingsDetailActivity.launch(context)
                }
            }
            SettingsId.LOGOUT -> {
                context.alert(
                        messageResource = R.string.sign_out_warning_message,
                        titleResource = R.string.sign_out_warning_title,
                        func = {
                            positiveButton(R.string.sign_out_warning_positive_btn_title, {
                                logoutInProgress.value = true
//                                logout.logout()

                                //TODO make logout work
                            })
                            negativeButton(android.R.string.cancel, { /* NO OP */ })
                        }
                )
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
