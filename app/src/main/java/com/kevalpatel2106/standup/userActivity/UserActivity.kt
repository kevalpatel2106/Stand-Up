package com.kevalpatel2106.standup.userActivity

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.annotations.Model
import timber.log.Timber

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

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    var localId: Long = 0

    val userActivityType: UserActivityType
        @SuppressLint("VisibleForTests")
        get() = getActivityType(type)


    @VisibleForTesting
    internal fun getActivityType(type: String): UserActivityType {

        return when (type) {
            UserActivityType.SITTING.name.toLowerCase() -> UserActivityType.SITTING
            UserActivityType.MOVING.name.toLowerCase() -> UserActivityType.MOVING
            UserActivityType.SLEEPING.name.toLowerCase() -> UserActivityType.SLEEPING
            else -> {/*This should never happen*/
                Timber.e("Invalid user activity type ->".plus(type))
                UserActivityType.SLEEPING
            }
        }
    }


    companion object {
        @JvmStatic
        fun createLocalUserActivity(type: UserActivityType) = UserActivity(
                eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = 0, /*We don't know when the event will end*/
                type = type.name.toLowerCase(),
                isSynced = false)


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