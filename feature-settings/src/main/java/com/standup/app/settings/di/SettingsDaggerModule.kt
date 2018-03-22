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

import com.danielstone.materialaboutlibrary.adapters.MaterialAboutListAdapter
import com.kevalpatel2106.common.di.CommonsDaggerModule
import com.kevalpatel2106.utils.annotations.ApplicationScope
import com.standup.app.settings.SettingsApi
import com.standup.app.settings.SettingsHook
import com.standup.app.settings.whitelisting.WhitelistingUtils
import dagger.Module
import dagger.Provides

/**
 * Created by Kevalpatel2106 on 20-Feb-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Module(includes = [CommonsDaggerModule::class])
internal class SettingsDaggerModule {

    @Provides
    @ApplicationScope
    fun provideSettingHook(): SettingsHook {
        return SettingsApi.settingsHook!!
    }

    @Provides
    @ApplicationScope
    fun provideAboutListAdapter(): MaterialAboutListAdapter {
        return MaterialAboutListAdapter()
    }

    @Provides
    @ApplicationScope
    fun provideWhiteListUtils(): WhitelistingUtils {
        return WhitelistingUtils()
    }
}
