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

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v4.app.Fragment
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.standup.app.settings.R
import com.standup.app.settings.dailyReview.DailyReviewSettingsDetailActivity
import com.standup.app.settings.dailyReview.DailyReviewSettingsFragment
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
@SuppressLint("VisibleForTests")
internal class SettingsViewModel : BaseViewModel() {

    internal var detailFragment = MutableLiveData<Fragment>()

    internal var showLogoutConformation = MutableLiveData<Boolean>()
    internal var openSyncSettings = MutableLiveData<Boolean>()
    internal var openDndSettings = MutableLiveData<Boolean>()
    internal var openNotificationSettings = MutableLiveData<Boolean>()
    internal var openDailyReview = MutableLiveData<Boolean>()

    internal var settingsItems = MutableLiveData<ArrayList<MaterialAboutCard>>()

    init {
        settingsItems.value = ArrayList()
        showLogoutConformation.value = false
    }

    @VisibleForTesting
    internal fun prepareSettingsList(context: Context) {
        settingsItems.value?.let {
            it.clear()

            it.add(getSettingsCard(context))
            it.add(getLogoutCard())
            it.add(getHelpCard())

            //Publish the update.
            settingsItems.value = it
        }
    }

    private fun getSettingsCard(context: Context): MaterialAboutCard {
        val syncSettingsItem = prepareCard(
                icon = R.drawable.ic_sync,
                text = "Sync",
                clickListener = MaterialAboutItemOnClickAction {
                    openSyncSettings.value = true
                }
        )

        val notificationsSettingsItem = prepareCard(
                icon = R.drawable.ic_notifications_bell,
                text = "Notifications",
                clickListener = MaterialAboutItemOnClickAction {
                    openNotificationSettings.value = true
                }
        )

        val dndSettingsItem = prepareCard(
                icon = R.drawable.ic_dnd_on,
                text = "DND",
                clickListener = MaterialAboutItemOnClickAction {
                    openDndSettings.value = true
                }
        )

        val dailyReviewItem = prepareCard(
                icon = R.drawable.ic_daily_review_notification,
                text = "Daily Review",
                clickListener = MaterialAboutItemOnClickAction {
                    openDailyReview.value = true
                }
        )

        return MaterialAboutCard.Builder()
                .title(context.getString(R.string.title_activity_settings))
                .addItem(syncSettingsItem)
                .addItem(dndSettingsItem)
                .addItem(dailyReviewItem)
                .addItem(notificationsSettingsItem)
                .build()
    }

    private fun getLogoutCard(): MaterialAboutCard {
        val logoutItem = prepareCard(
                icon = R.drawable.ic_logout,
                text = "Logout",
                clickListener = MaterialAboutItemOnClickAction {
                    showLogoutConformation.value = true
                }
        )

        return MaterialAboutCard.Builder()
                .addItem(logoutItem)
                .build()
    }

    private fun getHelpCard(): MaterialAboutCard {
        val logoutItem = prepareCard(
                icon = R.drawable.ic_logout,
                text = "Help",
                clickListener = MaterialAboutItemOnClickAction {
                    //TODO Handle help card click
                }
        )

        return MaterialAboutCard.Builder()
                .addItem(logoutItem)
                .build()
    }

    fun openSyncSettings(context: Context, isTwoPane: Boolean) {
        if (isTwoPane) {
            detailFragment.value = SyncSettingsFragment.getNewInstance()
        } else {
            SyncSettingsDetailActivity.launch(context)
        }
    }

    fun openDNDSettings(context: Context, isTwoPane: Boolean) {
        if (isTwoPane) {
            detailFragment.value = DndSettingsFragment.getNewInstance()
        } else {
            DndSettingsDetailActivity.launch(context)
        }
    }

    fun openNotificationSettings(context: Context, isTwoPane: Boolean) {
        if (isTwoPane) {
            detailFragment.value = NotificationSettingsFragment.getNewInstance()
        } else {
            NotificationSettingsDetailActivity.launch(context)
        }
    }

    fun openDailyReviewSettings(context: Context, isTwoPane: Boolean) {
        if (isTwoPane) {
            detailFragment.value = DailyReviewSettingsFragment.getNewInstance()
        } else {
            DailyReviewSettingsDetailActivity.launch(context)
        }
    }
}
