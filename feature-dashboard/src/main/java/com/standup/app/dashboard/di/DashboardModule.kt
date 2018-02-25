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

package com.standup.app.dashboard.di

import android.app.Application
import com.kevalpatel2106.common.application.di.AppModule
import com.kevalpatel2106.common.application.di.ApplicationScope
import com.kevalpatel2106.common.db.DbModule
import com.kevalpatel2106.common.db.userActivity.UserActivityDao
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.app.dashboard.repo.DashboardRepo
import com.standup.app.dashboard.repo.DashboardRepoImpl
import com.standup.core.CorePrefsProvider
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

/**
 * Created by Kevalpatel2106 on 09-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@Module(includes = [DbModule::class])
internal class DashboardModule {

    @Provides
    @ApplicationScope
    fun provideDashboardRepo(application: Application,
                             userActivityDao: UserActivityDao,
                             userSettingsManager: UserSettingsManager,
                             sharedPrefsProvider: SharedPrefsProvider,
                             @Named(AppModule.WITH_TOKEN) retrofit: Retrofit): DashboardRepo = DashboardRepoImpl(application,
            userSettingsManager,
            userActivityDao,
            CorePrefsProvider(sharedPrefsProvider),
            retrofit
    )
}
