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

            val serverActivities = java.util.ArrayList<ServerActivity>(1)
            serverActivities.add(ServerActivity(eventStartTimeMills = System.currentTimeMillis(),
                    eventEndTimeMills = System.currentTimeMillis() + 60_000,
                    type = 1,
                    remoteId = 894375L))
            serverActivities.add(ServerActivity(eventStartTimeMills = System.currentTimeMillis(),
                    eventEndTimeMills = System.currentTimeMillis() + 120_000,
                    type = 0,
                    remoteId = 994375L))

            val response = GetActivityResponse(serverActivities)

            Assert.assertEquals(2, response.activities.size)
            Assert.assertEquals(1, response.activities[0].type)
            Assert.assertEquals(0, response.activities[1].type)
        } catch (e: IllegalArgumentException) {
            Assert.fail()
        }
    }

    @Test
    fun checkEquals() {

        val serverActivities = java.util.ArrayList<ServerActivity>(1)
        serverActivities.add(ServerActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 60_000,
                type = 1,
                remoteId = 894375L))
        serverActivities.add(ServerActivity(eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 120_000,
                type = 0,
                remoteId = 994375L))

        val response = GetActivityResponse(serverActivities)
        val response1 = GetActivityResponse(serverActivities)

        Assert.assertEquals(response, response1)
    }

}
