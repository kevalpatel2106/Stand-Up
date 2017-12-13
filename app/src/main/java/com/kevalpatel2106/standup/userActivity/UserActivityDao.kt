package com.kevalpatel2106.standup.userActivity

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Dao
interface UserActivityDao {

    @Insert
    fun insert(userActivity: UserActivity): Long

}