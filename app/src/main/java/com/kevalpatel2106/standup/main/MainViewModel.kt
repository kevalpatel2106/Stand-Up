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

package com.kevalpatel2106.standup.main

import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.standup.dashboard.DashboardFragment
import com.kevalpatel2106.standup.diary.list.DiaryFragment
import com.kevalpatel2106.standup.stats.StatsFragment

/**
 * Created by Kevalpatel2106 on 12-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class MainViewModel : BaseViewModel() {

    /**
     * [DashboardFragment] instance to display in the [MainActivity].
     */
    val homeFragment = DashboardFragment.getNewInstance()

    /**
     * [DiaryFragment] instance to display in the [MainActivity].
     */
    val diaryFragment = DiaryFragment.getNewInstance()

    /**
     * [StatsFragment] instance to display in the [MainActivity].
     */
    val statsFragment = StatsFragment.getNewInstance()
}