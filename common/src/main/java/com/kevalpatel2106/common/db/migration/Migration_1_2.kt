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

package com.kevalpatel2106.common.db.migration

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.db.userActivity.UserActivity
import timber.log.Timber

/**
 * Created by Kevalpatel2106 on 07-Mar-18.
 * This class provides the migration from 1 to 2 database version which will swap the column end time
 * and start time from the version 1 into version 2. See issue [40](https://github.com/kevalpatel2106/Stand-Up/issues/40).
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Suppress("ClassName")
object Migration_1_2 : Migration(1, 2) {

    @VisibleForTesting
    internal const val OLD_COLUMN_EVENT_START = "event_end_time"

    @VisibleForTesting
    internal const val OLD_COLUMN_EVENT_END = "event_start_time"

    private const val TEMP_TABLE = "tmp_table"

    override fun migrate(database: SupportSQLiteDatabase) {
        //Rename the current table to temporary table
        database.execSQL("ALTER TABLE ${UserActivity.USER_ACTIVITY_TABLE} RENAME TO $TEMP_TABLE;")

        //Create table
        database.execSQL("CREATE TABLE IF NOT EXISTS ${UserActivity.USER_ACTIVITY_TABLE} (" +
                "${UserActivity.ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "${UserActivity.REMOTE_ID} INTEGER NOT NULL, " +
                "${UserActivity.EVENT_START_TIME} INTEGER NOT NULL, " +
                "${UserActivity.EVENT_END_TIME} INTEGER NOT NULL, " +
                "${UserActivity.ACTIVITY_TYPE} TEXT NOT NULL, " +
                "${UserActivity.IS_SYNCED} INTEGER NOT NULL)")

        //Create indexes
        createIndex(database)

        //Copy all the data from temporary table to new table
        database.execSQL("INSERT INTO ${UserActivity.USER_ACTIVITY_TABLE}(${UserActivity.ID}, ${UserActivity.REMOTE_ID}, ${UserActivity.EVENT_START_TIME}, ${UserActivity.EVENT_END_TIME}, ${UserActivity.ACTIVITY_TYPE}, ${UserActivity.IS_SYNCED})" +
                " SELECT ${UserActivity.ID}, ${UserActivity.REMOTE_ID}, $OLD_COLUMN_EVENT_START, $OLD_COLUMN_EVENT_END, ${UserActivity.ACTIVITY_TYPE}, ${UserActivity.IS_SYNCED} FROM $TEMP_TABLE;")

        // Drop the temporary table.
        database.execSQL("DROP TABLE $TEMP_TABLE;")
    }

    private fun createIndex(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("CREATE INDEX ${UserActivity.INDEX_START_END_TIME} ON " +
                    "${UserActivity.USER_ACTIVITY_TABLE}(${UserActivity.EVENT_START_TIME}, ${UserActivity.EVENT_END_TIME});")

            db.execSQL("CREATE INDEX ${UserActivity.INDEX_IS_SYNC} ON " +
                    "${UserActivity.USER_ACTIVITY_TABLE}(${UserActivity.IS_SYNCED});")
        } catch (e: Exception) {
            Timber.e(e)
        }

    }
}
