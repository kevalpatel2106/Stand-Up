package com.kevalpatel2106.standup.userActivity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Entity(tableName = UserActivity.USER_ACTIVITY_TABLE)
data class UserActivity(

        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = UserActivity.ID)
        val id: Long,

        @ColumnInfo(name = UserActivity.EVENT_START_TIME)
        val eventTimeMills: Long,

        @ColumnInfo(name = UserActivity.EVENT_END_TIME)
        val eventEndTimeMills: Long,

        @ColumnInfo(name = UserActivity.IS_SITTING)
        val isSitting: Boolean,

        @ColumnInfo(name = UserActivity.IS_SYNCED)
        val isSynced: Boolean
) {

    companion object {

        /**
         * Name of the table. This is the primary key.
         */
        const val USER_ACTIVITY_TABLE = "user_activity"

        //---- [Start] Column names
        const val ID = "_id"
        const val EVENT_START_TIME = "event_end_time"
        const val EVENT_END_TIME = "event_start_time"
        const val IS_SITTING = "is_sitting"
        const val IS_SYNCED = "is_synced"
        //---- [End] Column names
    }
}