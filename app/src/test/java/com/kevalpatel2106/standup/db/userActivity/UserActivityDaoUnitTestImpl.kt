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

package com.kevalpatel2106.standup.db.userActivity

/**
 * Created by Kevalpatel2106 on 01-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class UserActivityDaoUnitTestImpl : UserActivityDao {

    internal var insertItemId = 1234567L
    override fun insert(userActivity: UserActivity): Long {
        return insertItemId
    }

    internal var numberOfUpdatedItem = 1
    override fun update(userActivity: UserActivity): Int {
        return numberOfUpdatedItem
    }

    internal var latestUserActivity: UserActivity? = null
    override fun getLatestActivity(): UserActivity? {
        return latestUserActivity
    }

    internal lateinit var activityBetweenDuration: ArrayList<UserActivity>
    override fun getActivityBetweenDuration(afterTimeMills: Long, beforeTimeMills: Long): List<UserActivity> {
        return activityBetweenDuration
    }

    internal lateinit var activityAfter: ArrayList<UserActivity>
    override fun getActivityAfter(afterTimeMills: Long): List<UserActivity> {
        return activityAfter
    }

    internal var oldestActivity: UserActivity? = null
    override fun getOldestActivity(): UserActivity? {
        return oldestActivity
    }
}