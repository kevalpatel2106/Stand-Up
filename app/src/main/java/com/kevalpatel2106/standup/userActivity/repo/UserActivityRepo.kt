package com.kevalpatel2106.standup.userActivity.repo

import com.kevalpatel2106.standup.userActivity.UserActivity
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Created by Keval on 15/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface UserActivityRepo {

    fun insertNewAndTerminatePreviousActivity(newActivity: UserActivity)

    fun getLatestActivity(): Maybe<UserActivity>

    fun update(userActivity: UserActivity): Flowable<Int>

    fun insert(userActivity: UserActivity): Flowable<Long>

}