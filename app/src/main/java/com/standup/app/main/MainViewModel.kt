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

package com.standup.app.main

import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.standup.app.dashboard.DashboardFragment
import com.standup.app.dashboard.DashboardModule
import com.standup.app.diary.DiaryModule
import com.standup.app.diary.list.DiaryFragment
import com.standup.app.stats.StatsFragment
import com.standup.app.stats.StatsModule
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 12-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class MainViewModel : BaseViewModel() {

    init {
        DaggerMainComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@MainViewModel)
    }

    @Inject
    internal lateinit var dairyModule: DiaryModule

    @Inject
    internal lateinit var statsModule: StatsModule

    @Inject
    internal lateinit var dashboardModule: DashboardModule

    /**
     * [DashboardFragment] instance to display in the [MainActivity].
     */
    val homeFragment = dashboardModule.getDashboard()

    /**
     * [DiaryFragment] instance to display in the [MainActivity].
     */
    val diaryFragment = dairyModule.getDiary()

    /**
     * [StatsFragment] instance to display in the [MainActivity].
     */
    val statsFragment = statsModule.getStatsFragment()
}
