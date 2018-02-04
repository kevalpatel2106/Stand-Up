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

package com.standup.app.dashboard.repo

import com.kevalpatel2106.common.application.di.AppModule
import com.kevalpatel2106.common.db.DailyActivitySummary
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityDao
import com.kevalpatel2106.utils.TimeUtils
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import retrofit2.Retrofit
import java.util.*
import javax.inject.Named
import kotlin.collections.ArrayList

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class DashboardRepoImpl constructor(private val userActivityDao: UserActivityDao,
                                    @Named(AppModule.WITH_TOKEN) private val retrofit: Retrofit) : DashboardRepo {

    override fun getTodaySummary(): Flowable<DailyActivitySummary> {
        val calendar = TimeUtils.getTodaysCalender12AM()
        val startTimeMills = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 24)
        val endTimeMills = calendar.timeInMillis

        return Flowable.create(FlowableOnSubscribe<List<UserActivity>> {
            val item = userActivityDao.getActivityBetweenDuration(startTimeMills, endTimeMills)

            it.onNext(item)
            it.onComplete()
        }, BackpressureStrategy.DROP)
                .filter { t -> t.isNotEmpty() }
                .map { t -> ArrayList(t) }
                .map { arrayList ->
                    //Generate the summary
                    DailyActivitySummary.convertToValidUserActivityList(arrayList)
                    DailyActivitySummary.fromDayActivityList(arrayList)
                }
    }

}
