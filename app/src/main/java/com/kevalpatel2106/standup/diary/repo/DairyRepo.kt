package com.kevalpatel2106.standup.diary.repo

import com.kevalpatel2106.standup.db.DailyActivitySummary
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import io.reactivex.Flowable
import java.util.*

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface DairyRepo {

    companion object {
        const val PAGE_SIZE = 10
    }

    fun loadUserActivityByDay(calendar: Calendar): List<UserActivity>

    /**
     * Load the 20 days size of [UserActivity] data.
     */
    fun loadDaysList(oldestActivityMills: Long): Flowable<DailyActivitySummary>
}