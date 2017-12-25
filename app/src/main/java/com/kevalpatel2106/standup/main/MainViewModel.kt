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