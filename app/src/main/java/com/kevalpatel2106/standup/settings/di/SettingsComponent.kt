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

package com.kevalpatel2106.standup.settings.di

import com.kevalpatel2106.standup.application.di.AppComponent
import com.kevalpatel2106.standup.authentication.di.UserAuthModule
import com.kevalpatel2106.standup.misc.ApplicationScope
import com.kevalpatel2106.standup.settings.dailyReview.DailyReviewSettingsFragment
import com.kevalpatel2106.standup.settings.dailyReview.DailyReviewSettingsViewModel
import com.kevalpatel2106.standup.settings.dnd.DndSettingsViewModel
import com.kevalpatel2106.standup.settings.list.SettingsViewModel
import com.kevalpatel2106.standup.settings.notifications.NotificationSettingsFragment
import com.kevalpatel2106.standup.settings.notifications.NotificationsSettingsViewModel
import com.kevalpatel2106.standup.settings.syncing.SyncSettingsFragment
import com.kevalpatel2106.standup.settings.syncing.SyncSettingsViewModel
import dagger.Component

/**
 * Created by Keval on 11/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ApplicationScope
@Component(dependencies = [AppComponent::class], modules = [UserAuthModule::class])
interface SettingsComponent {

    fun inject(syncSettingsViewModel: SyncSettingsViewModel)

    fun inject(settingsViewModel: SettingsViewModel)

    fun inject(notificationSyncSettingsViewModel: NotificationsSettingsViewModel)

    fun inject(syncSettingsFragment: SyncSettingsFragment)

    fun inject(notificationSettingsFragment: NotificationSettingsFragment)

    fun inject(dailyReviewSettingsFragment: DailyReviewSettingsFragment)

    fun inject(dailyReviewSettingsViewModel: DailyReviewSettingsViewModel)

    fun inject(dndSettingsViewModel: DndSettingsViewModel)
}