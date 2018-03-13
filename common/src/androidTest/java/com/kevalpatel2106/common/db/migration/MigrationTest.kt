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

import android.app.Application
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.testing.MigrationTestHelper
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.common.db.StandUpDb
import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityType
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Kevalpatel2106 on 07-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@Suppress("ClassName")
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB_NAME = StandUpDb.DB_NAME

    private val id = 43543L
    private val remoteId = 4354L
    private val startTime = 7854637895L
    private val endTime = 78507884434L
    private val activityType = UserActivityType.MOVING.toString().toLowerCase()
    private val isSync = 1

    @Rule
    @JvmField
    val migrationHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
            StandUpDb::class.java.canonicalName, FrameworkSQLiteOpenHelperFactory())

    @Test
    fun testMigration_1To2() {
        // Create the database with version 2
        val db = migrationHelper.createDatabase(TEST_DB_NAME, 1)

        //Inject data in version 1.
        db.execSQL("INSERT INTO ${UserActivity.USER_ACTIVITY_TABLE} (${UserActivity.ID}, ${UserActivity.REMOTE_ID}, ${Migration_1_2.OLD_COLUMN_EVENT_START}, ${Migration_1_2.OLD_COLUMN_EVENT_END}, ${UserActivity.ACTIVITY_TYPE}, ${UserActivity.IS_SYNCED})" +
                " VALUES ($id, $remoteId, $startTime, $endTime, \"$activityType\", $isSync);")

        //Run the migration
        try {
            migrationHelper.runMigrationsAndValidate(TEST_DB_NAME, 2, true, Migration_1_2)
        } catch (e: Exception) {
            //Migration failed.
            Assert.fail(e.message)
            return
        }

        // Get the data back from version 2.
        val dbActivity = StandUpDb.getDb(InstrumentationRegistry.getContext().applicationContext as Application)
                .userActivityDao()
                .getActivityForLocalId(id)

        //Assert the data
        Assert.assertNotNull(dbActivity)
        Assert.assertEquals(id, dbActivity!!.localId)
        Assert.assertEquals(remoteId, dbActivity.remoteId)
        Assert.assertEquals(startTime, dbActivity.eventStartTimeMills)
        Assert.assertEquals(endTime, dbActivity.eventEndTimeMills)
        Assert.assertTrue(dbActivity.isSynced)
        Assert.assertEquals(UserActivityType.MOVING, dbActivity.userActivityType)
    }
}
