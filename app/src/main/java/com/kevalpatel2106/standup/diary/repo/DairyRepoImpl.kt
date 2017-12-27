package com.kevalpatel2106.standup.diary.repo

import com.kevalpatel2106.standup.db.DailyActivitySummary
import com.kevalpatel2106.standup.db.StandUpDb
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class DairyRepoImpl : DairyRepo {

    /**
     * Load the 20 days size of [UserActivity] data.
     */
    override fun loadDaysList(oldestActivityMills: Long): Flowable<DailyActivitySummary> {
        @Suppress("SENSELESS_COMPARISON")
        val flowable = Flowable.create(FlowableOnSubscribe<List<UserActivity>> { e ->
            //Setup the calender object.
            val calender = Calendar.getInstance()
            calender.timeInMillis = oldestActivityMills

            //Get the oldest day
            val oldestActivity = StandUpDb.getDb().userActivityDao().getOldestActivity()

            //Loop until the oldest event received
            while (oldestActivity != null && calender.timeInMillis > oldestActivity.eventStartTimeMills) {
                //Go one day before.
                calender.add(Calendar.DAY_OF_MONTH, -1)

                //Query the database.
                val dbList = ArrayList(loadUserActivityByDay(calender.clone() as Calendar))

                //Emit the list if data is available
                DailyActivitySummary.convertToValidUserActivityList(dbList)
                if (dbList.isNotEmpty()) {
                    e.onNext(dbList)
                }
            }

            //TODO call the server for more events.

            //Nothing from the network
            e.onComplete()
        }, BackpressureStrategy.BUFFER)
                .map(Function<List<UserActivity>, DailyActivitySummary> {
                    return@Function DailyActivitySummary.fromDayActivityList(ArrayList(it))
                })

        return flowable.zipWith(Flowable.range(1, DairyRepo.PAGE_SIZE)  /*Flowable that emits 1 to 10*/,
                BiFunction<DailyActivitySummary, Int, DailyActivitySummary> { t1, _ ->
                    t1 /* Emit the DailyActivitySummary list */
                })
    }

    override fun loadUserActivityByDay(calendar: Calendar): List<UserActivity> {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTimeMills = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTimeMills = calendar.timeInMillis

        return StandUpDb.getDb()
                .userActivityDao()
                .getActivityBetweenDuration(startTimeMills, endTimeMills)
    }
}