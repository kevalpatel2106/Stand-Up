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
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.SingleLiveEvent
import com.kevalpatel2106.common.db.userActivity.UserActivityDao
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.annotations.OnlyForTesting
import com.kevalpatel2106.utils.getColorCompat
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
import com.standup.app.settings.whitelisting.WhitelistingUtils
import dagger.Lazy
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Keval on 11/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@SuppressLint("VisibleForTests")
internal class SettingsViewModel : BaseViewModel {

    @Inject
    internal lateinit var userActivityDao: UserActivityDao

    @Inject
    internal lateinit var syncSettingsFragment: Lazy<SyncSettingsFragment>

    @Inject
    internal lateinit var dndSettingsFragment: Lazy<DndSettingsFragment>

    @Inject
    internal lateinit var notificationSettingsFragment: Lazy<NotificationSettingsFragment>

    @Inject
    internal lateinit var dailyReviewSettingsFragment: Lazy<DailyReviewSettingsFragment>

    internal var detailFragment = MutableLiveData<Fragment>()

    internal var showLogoutConformation = SingleLiveEvent<Boolean>()
    internal var openSyncSettings = SingleLiveEvent<Boolean>()
    internal var openDndSettings = SingleLiveEvent<Boolean>()
    internal var openNotificationSettings = SingleLiveEvent<Boolean>()
    internal var openDailyReview = SingleLiveEvent<Boolean>()
    internal var openInstructions = SingleLiveEvent<Boolean>()
    internal var openPrivacyPolicy = SingleLiveEvent<Boolean>()
    internal var showWhitelistDialog = SingleLiveEvent<Boolean>()

    internal var settingsItems = MutableLiveData<ArrayList<MaterialAboutCard>>()

    @Suppress("unused")
    constructor() {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SettingsViewModel)
        init()
    }

    @OnlyForTesting
    @VisibleForTesting
    constructor(userActivityDao: UserActivityDao) {
        this.userActivityDao = userActivityDao
        init()
    }

    private fun init() {
        settingsItems.value = ArrayList()

        showLogoutConformation.value = false
        openSyncSettings.value = false
        openDndSettings.value = false
        openNotificationSettings.value = false
        openDailyReview.value = false
        openInstructions.value = false
        openPrivacyPolicy.value = false
        showWhitelistDialog.value = false
    }

    internal fun prepareSettingsList(context: Context) {
        settingsItems.value?.let {
            it.clear()

            it.add(getSettingsCard())
            addWhiteListAppCard(context, userActivityDao)
            it.add(getHelpCard())
            it.add(getLogoutCard(context))

            //Publish the update.
            settingsItems.value = it
        }
    }

    private fun getSettingsCard(): MaterialAboutCard {
        val syncSettingsItem = prepareCard(
                icon = R.drawable.ic_sync,
                text = R.string.settings_name_sync,
                clickListener = MaterialAboutItemOnClickAction {
                    openSyncSettings.value = true
                }
        )

        val notificationsSettingsItem = prepareCard(
                icon = R.drawable.ic_notifications_bell,
                text = R.string.settings_name_notifications,
                clickListener = MaterialAboutItemOnClickAction {
                    openNotificationSettings.value = true
                }
        )

        val dndSettingsItem = prepareCard(
                icon = R.drawable.ic_dnd_on,
                text = R.string.settings_name_dnd,
                clickListener = MaterialAboutItemOnClickAction {
                    openDndSettings.value = true
                }
        )

        val dailyReviewItem = prepareCard(
                icon = R.drawable.ic_daily_review_notification,
                text = R.string.settings_name_daily_review,
                clickListener = MaterialAboutItemOnClickAction {
                    openDailyReview.value = true
                }
        )

        return MaterialAboutCard.Builder()
                .title(R.string.title_activity_settings)
                .addItem(syncSettingsItem)
                .addItem(dndSettingsItem)
                .addItem(dailyReviewItem)
                .addItem(notificationsSettingsItem)
                .build()
    }

    private fun getLogoutCard(context: Context): MaterialAboutCard {
        val logoutItem = prepareCard(
                icon = R.drawable.ic_logout,
                text = R.string.settings_name_logout,
                clickListener = MaterialAboutItemOnClickAction {
                    showLogoutConformation.value = true
                }
        )

        return MaterialAboutCard.Builder()
                .addItem(logoutItem)
                .cardColor(context.getColorCompat(R.color.logout_card_background))
                .build()
    }

    private fun getWhiteListCard(context: Context): MaterialAboutCard {
        val whitelistItem = prepareCard(
                icon = R.drawable.ic_battery_status,
                text = R.string.settings_name_white_list,
                subtext = R.string.settings_subtitle_white_list,
                clickListener = MaterialAboutItemOnClickAction {
                    showWhitelistDialog.value = true
                }
        )

        return MaterialAboutCard.Builder()
                .addItem(whitelistItem)
                .cardColor(context.getColorCompat(R.color.white_list_card_background))
                .build()
    }

    private fun getHelpCard(): MaterialAboutCard {
        val instructionItem = prepareCard(
                icon = R.drawable.ic_instruction,
                text = R.string.settings_name_instructions,
                clickListener = MaterialAboutItemOnClickAction {
                    openInstructions.value = true
                }
        )

        val privacyPolicyItem = prepareCard(
                icon = R.drawable.ic_privacy,
                text = R.string.settings_name_privacy,
                clickListener = MaterialAboutItemOnClickAction {
                    openPrivacyPolicy.value = true
                }
        )

        return MaterialAboutCard.Builder()
                .title(R.string.title_card_help)
                .addItem(instructionItem)
                .addItem(privacyPolicyItem)
                .build()
    }

    internal fun openSyncSettings(context: Context, isTwoPane: Boolean) {
        if (isTwoPane) {
            detailFragment.value = syncSettingsFragment.get()
        } else {
            SyncSettingsDetailActivity.launch(context)
        }
    }

    internal fun openDNDSettings(context: Context, isTwoPane: Boolean) {
        if (isTwoPane) {
            detailFragment.value = dndSettingsFragment.get()
        } else {
            DndSettingsDetailActivity.launch(context)
        }
    }

    internal fun openNotificationSettings(context: Context, isTwoPane: Boolean) {
        if (isTwoPane) {
            detailFragment.value = notificationSettingsFragment.get()
        } else {
            NotificationSettingsDetailActivity.launch(context)
        }
    }

    internal fun openDailyReviewSettings(context: Context, isTwoPane: Boolean) {
        if (isTwoPane) {
            detailFragment.value = dailyReviewSettingsFragment.get()
        } else {
            DailyReviewSettingsDetailActivity.launch(context)
        }
    }

    @VisibleForTesting
    internal fun addWhiteListAppCard(context: Context, userActivityDao: UserActivityDao) {
        Single.create(SingleOnSubscribe<Boolean> {

            if (WhitelistingUtils.shouldOpenWhiteListDialog(context)) {
                val latestActivity = userActivityDao.getLatestActivity()
                it.onSuccess(latestActivity == null ||
                        System.currentTimeMillis() - latestActivity.eventEndTimeMills > TimeUtils.ONE_DAY_MILLISECONDS)
            }

            it.onSuccess(false)
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it) settingsItems.value?.add(1, getWhiteListCard(context))
                }, {
                    Timber.i(it.printStackTrace().toString())
                })
    }
}
