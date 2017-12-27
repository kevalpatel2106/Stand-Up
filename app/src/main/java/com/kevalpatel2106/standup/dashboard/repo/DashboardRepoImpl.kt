package com.kevalpatel2106.standup.dashboard.repo

import com.kevalpatel2106.standup.db.StandUpDb
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class DashboardRepoImpl : DashboardRepo {

    override fun getTodayEvents(): Flowable<ArrayList<UserActivity>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTimeMills = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 24)
        val endTimeMills = calendar.timeInMillis


        return Flowable.create(FlowableOnSubscribe<List<UserActivity>> {
            val item = StandUpDb.getDb().userActivityDao()
                    .getActivityBetweenDuration(startTimeMills, endTimeMills)

            if (item.isNotEmpty()) it.onNext(item)
            it.onComplete()
        }, BackpressureStrategy.DROP).map { t -> ArrayList(t) }
    }

}