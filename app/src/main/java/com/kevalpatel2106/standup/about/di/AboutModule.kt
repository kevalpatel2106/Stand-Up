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

package com.kevalpatel2106.standup.about.di

import com.kevalpatel2106.standup.about.repo.AboutRepository
import com.kevalpatel2106.standup.about.repo.AboutRepositoryImpl
import com.kevalpatel2106.standup.misc.di.AppScope
import com.kevalpatel2106.utils.UserSessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

/**
 * Created by Kevalpatel2106 on 09-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Module
class AboutModule {

    @Provides
    @AppScope
    fun provideUserAuthRepo(@Named("WITH_TOKEN") retrofit: Retrofit,
                            userSessionManager: UserSessionManager): AboutRepository
            = AboutRepositoryImpl(retrofit, userSessionManager)
}
