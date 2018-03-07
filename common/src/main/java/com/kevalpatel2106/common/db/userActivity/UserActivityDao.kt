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

package com.kevalpatel2106.common.db.userActivity

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.kevalpatel2106.utils.annotations.Repository

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
    fun getLatestActivity(): UserActivity?

    @Query("SELECT * FROM " + UserActivity.USER_ACTIVITY_TABLE
            + " WHERE " + UserActivity.EVENT_END_TIME + " > :afterTimeMills AND "
            + UserActivity.EVENT_START_TIME + " < :beforeTimeMills")
    fun getActivityBetweenDuration(afterTimeMills: Long, beforeTimeMills: Long): List<UserActivity>

    @Query("SELECT * FROM " + UserActivity.USER_ACTIVITY_TABLE
            + " WHERE " + UserActivity.EVENT_START_TIME + " > :afterTimeMills ORDER BY "
            + UserActivity.EVENT_START_TIME + " DESC")
    fun getActivityAfter(afterTimeMills: Long): List<UserActivity>


    @Query("SELECT * FROM " + UserActivity.USER_ACTIVITY_TABLE
            + " ORDER BY " + UserActivity.EVENT_START_TIME + " ASC LIMIT 1")
    fun getOldestActivity(): UserActivity?

    @Query("SELECT * FROM " + UserActivity.USER_ACTIVITY_TABLE
            + " WHERE " + UserActivity.IS_SYNCED + " = :isPending"
            + " ORDER BY " + UserActivity.EVENT_START_TIME + " ASC")
    fun getPendingActivity(isPending: Boolean): List<UserActivity>

    @Query("SELECT MIN(" + UserActivity.EVENT_START_TIME + ") FROM "
            + UserActivity.USER_ACTIVITY_TABLE)
    fun getOldestTimestamp(): Long

    @Query("SELECT * FROM " + UserActivity.USER_ACTIVITY_TABLE + " WHERE " + UserActivity.ID + " = :localId")
    fun getActivityForLocalId(localId: Long): UserActivity?

    @Query("DELETE FROM " + UserActivity.USER_ACTIVITY_TABLE)
    fun nukeTable()
}
