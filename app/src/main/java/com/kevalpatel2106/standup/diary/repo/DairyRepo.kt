package com.kevalpatel2106.standup.diary.repo

import com.kevalpatel2106.standup.db.userActivity.UserActivity
import io.reactivex.Flowable
import java.util.*

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface DairyRepo {

    fun loadUserActivityByDay(calendar: Calendar): Flowable<List<UserActivity>>

    /**
     * Load the 20 [UserActivity] data.
     */
    fun loadUserActivities(afterMills: Long): Flowable<List<UserActivity>>
}