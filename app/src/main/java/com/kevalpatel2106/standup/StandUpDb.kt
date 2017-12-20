package com.kevalpatel2106.standup

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.kevalpatel2106.standup.userActivity.UserActivity
import com.kevalpatel2106.standup.userActivity.repo.UserActivityDao
import timber.log.Timber

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
        fun init(context: Context) {
            database = Room.databaseBuilder(context, StandUpDb::class.java, StandUpDb::class.simpleName.toString())
                    .fallbackToDestructiveMigration()
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
                    .build()
        }

        fun getDb() = database ?: throw ExceptionInInitializerError("You must call init() to initialize the database.")
    }

    abstract fun userActivityDao(): UserActivityDao

}