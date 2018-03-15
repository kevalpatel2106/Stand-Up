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

package com.standup.app.features.di

import com.kevalpatel2106.common.di.ApplicationScope
import com.standup.app.about.AboutApi
import com.standup.app.authentication.AuthenticationApi
import com.standup.app.dashboard.DashboardApi
import com.standup.app.diary.DiaryModule
import com.standup.app.features.AuthenticationHookImpl
import com.standup.app.features.ProfileHookImpl
import com.standup.app.features.SettingsHookImpl
import com.standup.app.profile.ProfileApi
import com.standup.app.settings.SettingsApi
import com.standup.app.stats.StatsModule
import dagger.Module
import dagger.Provides

/**
 * Created by Kevalpatel2106 on 09-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Module
internal class FeatureModule {

    @Provides
    @ApplicationScope
    fun provideSettingsModule(): SettingsApi = SettingsApi(SettingsHookImpl())

    @Provides
    @ApplicationScope
    fun provideProfileModule(): ProfileApi = ProfileApi(ProfileHookImpl())

    @Provides
    @ApplicationScope
    fun provideDiaryModule(): DiaryModule = DiaryModule()

    @Provides
    @ApplicationScope
    fun provideAboutModule(): AboutApi = AboutApi()

    @Provides
    @ApplicationScope
    fun provideStatsModule(): StatsModule = StatsModule()

    @Provides
    @ApplicationScope
    fun provideAuthenticationModule(): AuthenticationApi = AuthenticationApi(AuthenticationHookImpl())

    @Provides
    @ApplicationScope
    fun provideDashboardModule(): DashboardApi = DashboardApi()
}
