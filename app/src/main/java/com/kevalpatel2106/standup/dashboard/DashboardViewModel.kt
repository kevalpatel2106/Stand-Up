package com.kevalpatel2106.standup.dashboard

import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.standup.diary.DiaryFragment
import com.kevalpatel2106.standup.stats.StatsFragment

/**
 * Created by Kevalpatel2106 on 12-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DashboardViewModel : BaseViewModel() {

    /**
     * [HomeFragment] instance to display in the [DashboardActivity].
     */
    val homeFragment = HomeFragment.getNewInstance()

    /**
     * [DiaryFragment] instance to display in the [DashboardActivity].
     */
    val diaryFragment = DiaryFragment.getNewInstance()

    /**
     * [StatsFragment] instance to display in the [DashboardActivity].
     */
    val statsFragment = StatsFragment.getNewInstance()
}