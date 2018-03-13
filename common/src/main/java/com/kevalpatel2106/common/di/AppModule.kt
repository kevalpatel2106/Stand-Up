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

package com.kevalpatel2106.common.di

import android.app.Application
import android.content.Context
import com.kevalpatel2106.common.ReminderMessageProvider
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.network.NetworkModule
import com.kevalpatel2106.utils.SharedPrefsProvider
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Kevalpatel2106 on 08-Jan-18.
 * This application module provides the building blocks classes of this application. Such as the
 * database manager, prefrance manager, session manager etc. This all the classes are available
 * in this module.
 *
 * Other dagger modules([Module]) can add dependency of [AppComponent] to provide the building block
 * classes instances using this module.
 *
 * @see AppComponent
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Module
class AppModule(private val appContext: Application, private val baseUrl: String) {

    companion object {
        const val WITH_TOKEN = "WITH_TOKEN"
        const val WITHOUT_TOKEN = "WITHOUT_TOKEN"
    }

    @Provides
    @Singleton
    fun provideAppContext(): Application = appContext


    @Provides
    @Singleton
    fun provideContext(): Context = appContext

    @Provides
    @Singleton
    fun provideSharedPrefProvider(appContext: Application): SharedPrefsProvider = SharedPrefsProvider(appContext)

    @Provides
    @Singleton
    fun provideUserSession(sharedPrefsProvider: SharedPrefsProvider): UserSessionManager = UserSessionManager(sharedPrefsProvider)

    @Provides
    @Singleton
    fun provideUserSettingsManager(sharedPrefsProvider: SharedPrefsProvider): UserSettingsManager = UserSettingsManager(sharedPrefsProvider)

    @Provides
    @Singleton
    @Named(WITH_TOKEN)
    fun provideRetrofitClient(userSessionManager: UserSessionManager): Retrofit =
            NetworkModule(userSessionManager.userId.toString(), userSessionManager.token.toString())
                    .getRetrofitClient(baseUrl)

    @Provides
    @Singleton
    @Named(WITHOUT_TOKEN)
    fun provideRetrofitClientWithoutToken(): Retrofit = NetworkModule().getRetrofitClient(baseUrl)

    @Provides
    @Singleton
    fun provideReminderMessages(): ReminderMessageProvider = ReminderMessageProvider()
}
