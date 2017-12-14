package com.kevalpatel2106.standup.userActivity

import android.arch.persistence.room.*

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Dao
abstract class UserActivityDao {

    @Transaction
    open fun insertNewAndTerminatePreviousAcivity(newActivity: UserActivity) {
        val lastActivity = getLatestActivity()

        //Check if the user activity is changed or not?
        if (lastActivity.type != newActivity.type) {

            //This is new user activity.
            //Update the end time of the last user event.
            lastActivity.eventEndTimeMills = System.currentTimeMillis()
            update(lastActivity)

            //Add the event to the database
            insert(newActivity)
        }
    }

    @Insert
    abstract fun insert(userActivity: UserActivity): Long

    @Update
    abstract fun update(userActivity: UserActivity): Int

    @Query("SELECT * FROM " + UserActivity.USER_ACTIVITY_TABLE
            + " ORDER BY " + UserActivity.EVENT_START_TIME + " DESC"
            + " LIMIT 1")
    abstract fun getLatestActivity(): UserActivity
}