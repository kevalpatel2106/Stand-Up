/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.db

import android.arch.persistence.db.SupportSQLiteOpenHelper
import android.arch.persistence.room.DatabaseConfiguration
import android.arch.persistence.room.InvalidationTracker
import com.kevalpatel2106.standup.db.userActivity.UserActivityDao
import com.kevalpatel2106.standup.db.userActivity.UserActivityDaoMockImpl

/**
 * Created by Kevalpatel2106 on 01-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class StandUpDbMockImpl : StandUpDb() {
    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper? {
        return null
    }

    override fun createInvalidationTracker(): InvalidationTracker? {
        return null
    }

    override fun userActivityDao(): UserActivityDao = UserActivityDaoMockImpl()
}