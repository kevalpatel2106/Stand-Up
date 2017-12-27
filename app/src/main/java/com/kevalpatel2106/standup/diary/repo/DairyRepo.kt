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
     * Calling this function will load maximum [PAGE_SIZE] number of days summary which is [beforeMills]
     * and emit the summary for each day one-by-one. The emitted days will order by the day of year
     * in descending order.
     *
     * Internally this will load the [UserActivity] data from locally cached database using [loadUserActivityByDay]
     * and or from the network if the database cache is missing. After that, it will process the
     * [UserActivity] and create the summary of the day in [DailyActivitySummary] and emmit the data.
     *
     * @see loadUserActivityByDay
     */
    fun loadDaysList(beforeMills: Long): Flowable<DailyActivitySummary>
}