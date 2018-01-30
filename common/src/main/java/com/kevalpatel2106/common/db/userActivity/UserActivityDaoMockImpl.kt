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

import com.kevalpatel2106.utils.annotations.OnlyForTesting
import java.util.*

/**
 * Created by Kevalpatel2106 on 01-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@OnlyForTesting
class UserActivityDaoMockImpl(val tableItems: ArrayList<UserActivity>) : UserActivityDao {

    init {
        sortDescendingByStartTime()
    }

    override fun insert(userActivity: UserActivity): Long {
        userActivity.localId = tableItems.size.toLong()
        tableItems.add(userActivity)

        sortDescendingByStartTime()

        return tableItems.lastIndex.toLong()
    }

    override fun nukeTable() {
        tableItems.clear()
    }

    override fun update(userActivity: UserActivity): Int {
        val numberOfUpdatedItem = 0
        (0 until tableItems.size)
                .filter { it -> tableItems[it].localId == userActivity.localId }
                .forEach { it ->
                    numberOfUpdatedItem.inc()
                    tableItems[it] = userActivity
                }

        sortDescendingByStartTime()
        return numberOfUpdatedItem
    }

    override fun getLatestActivity(): UserActivity? {
        return if (tableItems.isEmpty()) null else tableItems.first()
    }

    override fun getActivityBetweenDuration(afterTimeMills: Long, beforeTimeMills: Long): List<UserActivity> {
        val activityBetweenDuration = ArrayList<UserActivity>()

        tableItems.filter { it -> it.eventStartTimeMills in (afterTimeMills + 1)..(beforeTimeMills - 1) }
                .forEach { it -> activityBetweenDuration.add(it) }
        return activityBetweenDuration
    }

    override fun getActivityAfter(afterTimeMills: Long): List<UserActivity> {
        val activityAfter = ArrayList<UserActivity>()

        tableItems.filter { it -> it.eventStartTimeMills > afterTimeMills }
                .forEach { it ->
                    activityAfter.add(it)
                }
        return activityAfter
    }

    override fun getOldestActivity(): UserActivity? {
        return if (tableItems.isEmpty()) null else tableItems.last()
    }

    private fun sortDescendingByStartTime() = Collections.sort(tableItems) { o1, o2 ->
        val diff = (o2.eventStartTimeMills - o1.eventStartTimeMills)
        if (diff < 0) return@sort -1
        else if (diff > 0) return@sort 1
        return@sort 0
    }

    override fun getPendingActivity(isPending: Boolean): List<UserActivity> {
        val activityAfter = ArrayList<UserActivity>()

        tableItems.filter { it -> it.isSynced == isPending }
                .forEach { it -> activityAfter.add(it) }
        return activityAfter
    }
}