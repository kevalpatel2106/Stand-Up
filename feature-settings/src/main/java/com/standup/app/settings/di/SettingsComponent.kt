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

package com.standup.app.settings.di

import com.kevalpatel2106.common.application.di.AppComponent
import com.kevalpatel2106.common.application.di.ApplicationScope
import com.standup.app.settings.dailyReview.DailyReviewSettingsFragment
import com.standup.app.settings.dailyReview.DailyReviewSettingsViewModel
import com.standup.app.settings.dnd.DndSettingsViewModel
import com.standup.app.settings.list.SettingsViewModel
import com.standup.app.settings.notifications.NotificationSettingsFragment
import com.standup.app.settings.notifications.NotificationsSettingsViewModel
import com.standup.app.settings.syncing.SyncSettingsFragment
import com.standup.app.settings.syncing.SyncSettingsViewModel
import dagger.Component

/**
 * Created by Keval on 11/01/18.
 * Dagger [Component] for settings package.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ApplicationScope
@Component(dependencies = [AppComponent::class], modules = [SettingsModule::class])
internal interface SettingsComponent {

    fun inject(syncSettingsViewModel: SyncSettingsViewModel)

    fun inject(settingsViewModel: SettingsViewModel)

    fun inject(notificationSyncSettingsViewModel: NotificationsSettingsViewModel)

    fun inject(syncSettingsFragment: SyncSettingsFragment)

    fun inject(notificationSettingsFragment: NotificationSettingsFragment)

    fun inject(dailyReviewSettingsFragment: DailyReviewSettingsFragment)

    fun inject(dailyReviewSettingsViewModel: DailyReviewSettingsViewModel)

    fun inject(dndSettingsViewModel: DndSettingsViewModel)
}
