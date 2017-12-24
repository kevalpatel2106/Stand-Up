package com.kevalpatel2106.standup.userActivity.repo

import com.kevalpatel2106.standup.userActivity.UserActivity
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.*

/**
 * Created by Keval on 15/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface UserActivityRepo {

    /**
     * This will store [newActivity] into the database and terminate the previous event with the
     * [newActivity] start time. If the previous [UserActivity] is already terminated it will just
     * inert [newActivity].
     */
    fun insertNewAndTerminatePreviousActivity(newActivity: UserActivity)

    fun getLatestActivity(): Maybe<UserActivity>

    fun getTodayEvents(): Flowable<List<UserActivity>>

    fun loadUserActivityByDay(calendar: Calendar): Flowable<List<UserActivity>>

    /**
     * Load the 20 [UserActivity] data.
     */
    fun loadUserActivities(afterMills: Long): Flowable<List<UserActivity>>
}