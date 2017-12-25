package com.kevalpatel2106.standup.reminder.repo

import com.kevalpatel2106.standup.db.userActivity.UserActivity
import io.reactivex.Maybe

/**
 * Created by Keval on 15/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface ReminderRepo {

    /**
     * This will store [newActivity] into the database and terminate the previous event with the
     * [newActivity] start time. If the previous [UserActivity] is already terminated it will just
     * inert [newActivity].
     */
    fun insertNewAndTerminatePreviousActivity(newActivity: UserActivity)

    fun getLatestActivity(): Maybe<UserActivity>
}