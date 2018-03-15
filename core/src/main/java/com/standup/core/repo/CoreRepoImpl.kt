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

package com.standup.core.repo

import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityDao
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.annotations.Repository
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import javax.inject.Inject

/**
 * Created by Keval on 14/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
internal class CoreRepoImpl @Inject constructor(private val userActivityDao: UserActivityDao)
    : CoreRepo {

    /**
     * Load user activity summary for the previous day.
     *
     * @see DailyActivitySummary
     */
    override fun loadYesterdaySummary(): Flowable<DailyActivitySummary> {
        //Get the calender for 12 AM of that day
        val endTimeMills = TimeUtils.todayMidnightCal(false).timeInMillis
        val startTimeMills = endTimeMills - TimeUtils.ONE_DAY_MILLISECONDS

        return Flowable.create(FlowableOnSubscribe<List<UserActivity>> {
            val item = userActivityDao.getActivityBetweenDuration(startTimeMills, endTimeMills)

            it.onNext(item)
            it.onComplete()
        }, BackpressureStrategy.DROP)
                .filter { t -> t.isNotEmpty() }
                .map { t -> ArrayList(t) }
                .map { arrayList ->
                    //Generate the summary
                    DailyActivitySummary.fromDayActivityList(arrayList)
                }
    }
}
