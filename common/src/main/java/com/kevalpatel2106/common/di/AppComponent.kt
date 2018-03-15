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
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton


/**
 * Created by Kevalpatel2106 on 08-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(application: BaseApplication)

    fun getContext(): Context

    fun getApplication(): Application

    @Named(AppModule.WITH_TOKEN)
    fun geRetrofit(): Retrofit

    @Named(AppModule.WITHOUT_TOKEN)
    fun getRetrofit(): Retrofit

    fun getUserSessionManagerProvider(): UserSessionManager

    fun getUserSettingsManagerProvider(): UserSettingsManager

    fun getSharedPrefsProvider(): SharedPrefsProvider

    fun getReminderMessageProvider(): ReminderMessageProvider
}
