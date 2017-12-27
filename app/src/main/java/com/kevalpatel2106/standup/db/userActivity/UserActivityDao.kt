package com.kevalpatel2106.standup.db.userActivity

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.kevalpatel2106.base.annotations.Repository
import io.reactivex.Maybe

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
@Dao
interface UserActivityDao {

    @Insert
    fun insert(userActivity: UserActivity): Long

    @Update
    fun update(userActivity: UserActivity): Int

    @Query("SELECT * FROM " + UserActivity.USER_ACTIVITY_TABLE
            + " ORDER BY " + UserActivity.EVENT_START_TIME + " DESC"
            + " LIMIT 1")
    fun getLatestActivity(): Maybe<UserActivity>

    @Query("SELECT * FROM " + UserActivity.USER_ACTIVITY_TABLE
            + " WHERE " + UserActivity.EVENT_START_TIME + " > :afterTimeMills AND "
            + UserActivity.EVENT_START_TIME + " < :beforeTimeMills")
    fun getActivityBetweenDuration(afterTimeMills: Long, beforeTimeMills: Long): List<UserActivity>

    @Query("SELECT * FROM " + UserActivity.USER_ACTIVITY_TABLE
            + " WHERE " + UserActivity.EVENT_START_TIME + " > :afterTimeMills ORDER BY "
            + UserActivity.EVENT_START_TIME + " DESC")
    fun getActivityAfter(afterTimeMills: Long): List<UserActivity>


    @Query("SELECT * FROM " + UserActivity.USER_ACTIVITY_TABLE
            + " ORDER BY " + UserActivity.EVENT_START_TIME + " ASC LIMIT 1")
    fun getOldestActivity(): UserActivity?
}