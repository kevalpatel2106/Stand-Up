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

import com.kevalpatel2106.common.di.CommonsDaggerModule
import com.kevalpatel2106.common.userActivity.UserActivityDao
import com.kevalpatel2106.utils.annotations.ApplicationScope
import com.standup.core.Core
import com.standup.core.CoreHook
import com.standup.core.repo.CoreRepo
import com.standup.core.repo.CoreRepoImpl
import dagger.Module
import dagger.Provides

/**
 * Created by Keval on 10/01/18.
 * Dagger [Module] to provide the dependency for the Core module.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Module(includes = [CommonsDaggerModule::class])
internal class CoreDaggerModule {

    /**
     * Get the instance of [CoreRepo].
     *
     * @see CoreRepoImpl
     */
    @Provides
    @ApplicationScope
    fun provideReminderRepo(userActivityDao: UserActivityDao): CoreRepo = CoreRepoImpl(userActivityDao)

    @Provides
    @ApplicationScope
    fun provideCoreHook(): CoreHook = Core.coreHook
}
