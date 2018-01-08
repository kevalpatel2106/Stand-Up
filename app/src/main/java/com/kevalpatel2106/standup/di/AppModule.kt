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

package com.kevalpatel2106.standup.di

import android.app.Application
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.db.StandUpDb
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Kevalpatel2106 on 08-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Module
class AppModule(private val appContext: Application) {

    @Provides
    @Singleton
    fun provideAppContext() = appContext

    @Provides
    @Singleton
    fun provideDatabase() = StandUpDb.getDb()

    @Provides
    @Singleton
    fun provideSharedPrefProvider() = SharedPrefsProvider(appContext)

    @Provides
    @Singleton
    fun provideUserSession() = UserSessionManager(provideSharedPrefProvider())

    @Provides
    @Singleton
    fun provideApiProvider() = ApiProvider(appContext, provideUserSession())


}
