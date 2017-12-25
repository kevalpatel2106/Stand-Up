package com.kevalpatel2106.standup.db.userActivity

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kevalpatel2106.base.annotations.Model

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Model
@Entity(tableName = UserActivity.USER_ACTIVITY_TABLE)
data class UserActivity(
        @ColumnInfo(name = REMOTE_ID)
        var remoteId: Long = 0,

        @ColumnInfo(name = EVENT_START_TIME)
        val eventStartTimeMills: Long,

        @ColumnInfo(name = EVENT_END_TIME)
        var eventEndTimeMills: Long,

        @ColumnInfo(name = ACTIVITY_TYPE)
        val type: String,

        @ColumnInfo(name = IS_SYNCED)
        val isSynced: Boolean
) {

    init {
        if (eventEndTimeMills != 0L && eventEndTimeMills < eventStartTimeMills) {
            throw IllegalArgumentException("End time cannot be less than start time.")
        }
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    var localId: Long = 0

    val userActivityType: UserActivityType
        @SuppressLint("VisibleForTests")
        get() = UserActivityHelper.getActivityType(type)


    companion object {
        /**
         * Name of the table. This is the primary key.
         */
        const val USER_ACTIVITY_TABLE = "user_activity"

        //---- [Start] Column names
        const val ID = "_id"
        const val REMOTE_ID = "remote_id"
        const val EVENT_START_TIME = "event_end_time"
        const val EVENT_END_TIME = "event_start_time"
        const val ACTIVITY_TYPE = "type"
        const val IS_SYNCED = "is_synced"
        //---- [End] Column names
    }
}