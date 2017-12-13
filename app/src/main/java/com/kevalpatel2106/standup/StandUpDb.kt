package com.kevalpatel2106.standup

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.kevalpatel2106.standup.userActivity.UserActivity
import com.kevalpatel2106.standup.userActivity.UserActivityDao

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Database(entities = [(UserActivity::class)], exportSchema = true, version = 1)
abstract class StandUpDb : RoomDatabase() {

    companion object {
        private var database: StandUpDb? = null

        @Suppress("NO_REFLECTION_IN_CLASS_PATH")
        fun init(context: Context) = Room
                .databaseBuilder(context, StandUpDb::class.java, StandUpDb::class.simpleName.toString())
                .fallbackToDestructiveMigration()
                .build()

        fun getDb() = database!!
    }

    abstract fun userActivityDao(): UserActivityDao

}