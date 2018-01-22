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

package com.kevalpatel2106.standup.diary.repo

import com.kevalpatel2106.common.db.DailyActivitySummary
import com.kevalpatel2106.common.db.userActivity.UserActivity
import io.reactivex.Flowable

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface DiaryRepo {

    companion object {
        const val PAGE_SIZE = 10
    }

    fun loadUserActivityByDay(dayOfMonth: Int, month: Int, year: Int): Flowable<ArrayList<UserActivity>>

    fun loadSummary(dayOfMonth: Int, month: Int, year: Int): Flowable<DailyActivitySummary>

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
    fun loadDaysSummaryList(beforeMills: Long): Flowable<DailyActivitySummary>
}
