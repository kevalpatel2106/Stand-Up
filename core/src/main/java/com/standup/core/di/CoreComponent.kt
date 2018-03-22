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

package com.standup.core.di

import com.kevalpatel2106.common.di.AppComponent
import com.kevalpatel2106.utils.annotations.ApplicationScope
import com.standup.core.CorePrefsProvider
import com.standup.core.SystemEventReceiver
import com.standup.core.activityMonitor.ActivityMonitorJob
import com.standup.core.dailyReview.DailyReviewJob
import com.standup.core.dndManager.AutoDndMonitoringJob
import com.standup.core.reminder.NotificationSchedulerJob
import com.standup.core.reminder.PopUpActivity
import com.standup.core.reminder.ReminderNotification
import com.standup.core.sleepManager.SleepModeMonitoringJob
import com.standup.core.sync.SyncJob
import dagger.Component

/**
 * Created by Keval on 10/01/18.
 * Dagger [Component] for core module.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ApplicationScope
@Component(dependencies = [AppComponent::class], modules = [CoreDaggerModule::class])
internal interface CoreComponent {

    fun inject(activityMonitorJob: ActivityMonitorJob)

    fun inject(notificationSchedulerJob: NotificationSchedulerJob)

    fun inject(syncJob: SyncJob)

    fun inject(systemEventReceiver: SystemEventReceiver)

    fun inject(reminderNotification: ReminderNotification)

    fun inject(autoDndMonitoringJob: AutoDndMonitoringJob)

    fun inject(sleepModeMonitoringJob: SleepModeMonitoringJob)

    fun inject(dailyReviewJob: DailyReviewJob)

    fun inject(corePrefsProvider: CorePrefsProvider)

    fun inject(popUpActivity: PopUpActivity)
}
