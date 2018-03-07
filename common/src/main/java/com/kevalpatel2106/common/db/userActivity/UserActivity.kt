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

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kevalpatel2106.utils.annotations.Model
import java.io.Serializable

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
        var eventStartTimeMills: Long,

        @ColumnInfo(name = EVENT_END_TIME)
        var eventEndTimeMills: Long,

        @ColumnInfo(name = ACTIVITY_TYPE)
        val type: String,

        @ColumnInfo(name = IS_SYNCED)
        var isSynced: Boolean
) : Serializable {

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
        const val EVENT_START_TIME = "event_end_time"   //TODO Column names are swapped
        const val EVENT_END_TIME = "event_start_time"
        const val ACTIVITY_TYPE = "type"
        const val IS_SYNCED = "is_synced"
        //---- [End] Column names
    }
}
