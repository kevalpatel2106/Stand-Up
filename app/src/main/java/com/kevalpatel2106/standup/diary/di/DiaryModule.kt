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

package com.kevalpatel2106.standup.diary.di

import com.kevalpatel2106.standup.db.DbModule
import com.kevalpatel2106.standup.db.userActivity.UserActivityDao
import com.kevalpatel2106.standup.diary.repo.DiaryRepo
import com.kevalpatel2106.standup.diary.repo.DiaryRepoImpl
import com.kevalpatel2106.standup.misc.di.AppScope
import com.kevalpatel2106.standup.misc.di.NetworkModule
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * Created by Keval on 10/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Module(includes = [NetworkModule::class, DbModule::class])
class DiaryModule {

    @Provides
    @AppScope
    fun provideDiaryRepo(userActivityDao: UserActivityDao,
                         retrofit: Retrofit): DiaryRepo
            = DiaryRepoImpl(retrofit, userActivityDao)

}