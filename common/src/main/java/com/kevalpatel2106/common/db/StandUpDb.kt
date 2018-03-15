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

package com.kevalpatel2106.common.db

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.kevalpatel2106.common.db.migration.Migration_1_2
import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityDao
import timber.log.Timber


/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Database(entities = [UserActivity::class], exportSchema = true, version = 2)
abstract class StandUpDb : RoomDatabase() {

    companion object {
        const val DB_NAME = "StandUpDb"

        private var sDatabase: StandUpDb? = null

        @Suppress("NO_REFLECTION_IN_CLASS_PATH")
        private fun createDb(application: Application): StandUpDb {
            sDatabase = Room.databaseBuilder(application, StandUpDb::class.java, DB_NAME)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Timber.i("Database created successfully.")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Timber.i("Database connection opened.")
                        }
                    })
                    .addMigrations(Migration_1_2)
                    .build()
            return sDatabase!!
        }

        fun getDb(application: Application): StandUpDb = sDatabase
                ?: createDb(application)

    }

    abstract fun userActivityDao(): UserActivityDao
}
