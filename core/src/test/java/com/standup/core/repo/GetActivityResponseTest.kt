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

package com.standup.core.repo

import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Keval on 31/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class GetActivityResponseTest {

    @Test
    fun checkInit_EmptyList() {
        try {
            val response = GetActivityResponse(ArrayList())

            Assert.assertTrue(response.activities.isEmpty())
        } catch (e: IllegalArgumentException) {
            Assert.fail()
        }
    }

    @Test
    fun checkInit_WithFilledList() {
        try {

            val userActivities = java.util.ArrayList<UserActivity>(1)
            userActivities.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                    eventEndTimeMills = System.currentTimeMillis() + 60_000,
                    isSynced = true,
                    type = UserActivityType.MOVING.name.toLowerCase()))
            userActivities.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                    eventEndTimeMills = System.currentTimeMillis() + 120_000,
                    isSynced = true,
                    type = UserActivityType.SITTING.name.toLowerCase()))

            val response = GetActivityResponse(userActivities)

            Assert.assertEquals(2, response.activities.size)
            Assert.assertEquals(UserActivityType.MOVING, response.activities[0].userActivityType)
            Assert.assertEquals(UserActivityType.SITTING, response.activities[1].userActivityType)
        } catch (e: IllegalArgumentException) {
            Assert.fail()
        }
    }

    @Test
    fun checkEquals() {
        val userActivities = java.util.ArrayList<UserActivity>(1)
        userActivities.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 60_000,
                isSynced = true,
                type = UserActivityType.MOVING.name.toLowerCase()))
        userActivities.add(UserActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 120_000,
                isSynced = true,
                type = UserActivityType.SITTING.name.toLowerCase()))

        val response = GetActivityResponse(userActivities)
        val response1 = GetActivityResponse(userActivities)

        Assert.assertEquals(response, response1)
    }

}