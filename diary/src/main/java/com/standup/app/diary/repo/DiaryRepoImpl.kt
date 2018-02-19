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

package com.standup.app.diary.repo

import com.kevalpatel2106.common.application.di.AppModule
import com.kevalpatel2106.common.db.DailyActivitySummary
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityDao
import com.kevalpatel2106.utils.TimeUtils
import com.standup.app.diary.repo.DiaryRepo.Companion.PAGE_SIZE
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import retrofit2.Retrofit
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DiaryRepoImpl @Inject constructor(@Named(AppModule.WITH_TOKEN) private val retrofit: Retrofit,
                                                 private val userActivityDao: UserActivityDao) : DiaryRepo {

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
    override fun loadDaysSummaryList(beforeMills: Long): Flowable<DailyActivitySummary> {

        val flowable = Flowable.create(FlowableOnSubscribe<List<UserActivity>> { e ->

            //Setup the calender object.
            val calender = Calendar.getInstance()
            calender.timeInMillis = beforeMills

            //Get the oldest day
            val oldestActivity = userActivityDao.getOldestActivity()

            //Loop until the oldest event received
            while (oldestActivity != null && calender.timeInMillis > oldestActivity.eventStartTimeMills) {
                //Query the database.
                val dbList = ArrayList(loadUserActivityByDay(calender.clone() as Calendar))

                //Emit the list if data is available
                DailyActivitySummary.convertToValidUserActivityList(dbList)
                if (dbList.isNotEmpty()) {
                    e.onNext(dbList)
                }

                //Go one day before.
                calender.add(Calendar.DAY_OF_MONTH, -1)
            }

            //TODO call the server for more events.

            //Nothing from the network
            e.onComplete()
        }, BackpressureStrategy.BUFFER)
                .map(Function<List<UserActivity>, DailyActivitySummary> {
                    return@Function DailyActivitySummary.fromDayActivityList(ArrayList(it))
                })

        return flowable.zipWith(Flowable.range(1, DiaryRepo.PAGE_SIZE)  /*Flowable that emits 1 to 10*/,
                BiFunction<DailyActivitySummary, Int, DailyActivitySummary> { t1, _ ->
                    t1 /* Emit the DailyActivitySummary list */
                })
    }

    override fun loadUserActivityByDay(dayOfMonth: Int, month: Int, year: Int): Flowable<ArrayList<UserActivity>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        return Flowable.create({
            it.onNext(ArrayList(loadUserActivityByDay(calendar)))
            it.onComplete()
        }, BackpressureStrategy.DROP)
    }

    override fun loadSummary(dayOfMonth: Int, month: Int, year: Int): Flowable<DailyActivitySummary> {
        //Get the calender for 12 AM of that day
        var dayCal = Calendar.getInstance()
        dayCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        dayCal.set(Calendar.MONTH, month)
        dayCal.set(Calendar.YEAR, year)
        dayCal = TimeUtils.getCalender12AM(dayCal.timeInMillis)

        val startTimeMills = dayCal.timeInMillis

        dayCal.set(Calendar.HOUR_OF_DAY, 24)
        val endTimeMills = dayCal.timeInMillis

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

    internal fun loadUserActivityByDay(calendar: Calendar): List<UserActivity> {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTimeMills = calendar.timeInMillis - 1

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTimeMills = calendar.timeInMillis + 1

        return userActivityDao.getActivityBetweenDuration(startTimeMills, endTimeMills)
    }
}
