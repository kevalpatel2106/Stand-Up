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

package com.kevalpatel2106.common.userActivity.repo

import com.kevalpatel2106.common.userActivity.UserActivityType
import com.kevalpatel2106.utils.TimeUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 13-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ServerActivityTest {

    @Test
    @Throws(Exception::class)
    fun checkType() {
        val serverActivity = ServerActivity(
                remoteId = 2386L,
                eventStartTimeNano = System.currentTimeMillis(),
                eventEndTimeNano = System.currentTimeMillis(),
                type = 0
        )
        Assert.assertEquals(0, serverActivity.type)
    }

    @Test
    @Throws(Exception::class)
    fun checkRemoteId() {
        val serverActivity = ServerActivity(
                remoteId = 2386L,
                eventStartTimeNano = System.currentTimeMillis(),
                eventEndTimeNano = System.currentTimeMillis(),
                type = 0
        )
        Assert.assertEquals(2386L, serverActivity.remoteId)
    }

    @Test
    @Throws(Exception::class)
    fun checkStartTime() {
        val serverActivity = ServerActivity(
                remoteId = 2386L,
                eventStartTimeNano = 123456L,
                eventEndTimeNano = 67890L,
                type = 0
        )
        Assert.assertEquals(123456L, serverActivity.eventStartTimeNano)
    }

    @Test
    @Throws(Exception::class)
    fun checkEndTime() {
        val serverActivity = ServerActivity(
                remoteId = 2386L,
                eventStartTimeNano = 123456L,
                eventEndTimeNano = 67890L,
                type = 0
        )
        Assert.assertEquals(67890L, serverActivity.eventEndTimeNano)
    }

    @Test
    @Throws(Exception::class)
    fun checkConvertUserActivity_SittingActivity() {
        val start = TimeUtils.convertToNano(System.currentTimeMillis())
        val end = TimeUtils.convertToNano(System.currentTimeMillis()) + TimeUtils.convertToNano(10_000)
        val serverActivity = ServerActivity(
                remoteId = 67890L,
                eventStartTimeNano = start,
                eventEndTimeNano = end,
                type = 0
        )

        val userActivity = serverActivity.getUserActivity()
        Assert.assertEquals(TimeUtils.convertToMilli(start), userActivity.eventStartTimeMills)
        Assert.assertEquals(TimeUtils.convertToMilli(end), userActivity.eventEndTimeMills)
        Assert.assertEquals(serverActivity.remoteId, userActivity.remoteId)
        Assert.assertTrue(userActivity.isSynced)
        Assert.assertEquals(UserActivityType.SITTING, userActivity.userActivityType)
    }

    @Test
    @Throws(Exception::class)
    fun checkConvertUserActivity_StandingActivity() {
        val start = TimeUtils.convertToNano(System.currentTimeMillis())
        val end = TimeUtils.convertToNano(System.currentTimeMillis()) + TimeUtils.convertToNano(10_000)
        val serverActivity = ServerActivity(
                remoteId = 67890L,
                eventStartTimeNano = start,
                eventEndTimeNano = end,
                type = 1
        )

        val userActivity = serverActivity.getUserActivity()
        Assert.assertEquals(TimeUtils.convertToMilli(start), userActivity.eventStartTimeMills)
        Assert.assertEquals(TimeUtils.convertToMilli(end), userActivity.eventEndTimeMills)
        Assert.assertEquals(serverActivity.remoteId, userActivity.remoteId)
        Assert.assertTrue(userActivity.isSynced)
        Assert.assertEquals(UserActivityType.MOVING, userActivity.userActivityType)
    }

    @Test
    @Throws(Exception::class)
    fun checkConvertUserActivity_NotTrackedActivity() {
        val start = TimeUtils.convertToNano(System.currentTimeMillis())
        val end = TimeUtils.convertToNano(System.currentTimeMillis()) + TimeUtils.convertToNano(10_000)
        val serverActivity = ServerActivity(
                remoteId = 67890L,
                eventStartTimeNano = start,
                eventEndTimeNano = end,
                type = 2
        )

        val userActivity = serverActivity.getUserActivity()
        Assert.assertEquals(TimeUtils.convertToMilli(start), userActivity.eventStartTimeMills)
        Assert.assertEquals(TimeUtils.convertToMilli(end), userActivity.eventEndTimeMills)
        Assert.assertEquals(serverActivity.remoteId, userActivity.remoteId)
        Assert.assertTrue(userActivity.isSynced)
        Assert.assertEquals(UserActivityType.NOT_TRACKED, userActivity.userActivityType)
    }


    @Test
    @Throws(Exception::class)
    fun checkEquals() {
        val serverActivity = ServerActivity(
                remoteId = 2386L,
                eventStartTimeNano = System.currentTimeMillis(),
                eventEndTimeNano = System.currentTimeMillis(),
                type = 0
        )
        val serverActivity1 = ServerActivity(
                remoteId = 2386L,
                eventStartTimeNano = System.currentTimeMillis(),
                eventEndTimeNano = System.currentTimeMillis(),
                type = 0
        )
        val serverActivity2 = ServerActivity(
                remoteId = 2386L,
                eventStartTimeNano = System.currentTimeMillis(),
                eventEndTimeNano = System.currentTimeMillis(),
                type = 1
        )

        Assert.assertEquals(serverActivity, serverActivity1)
        Assert.assertNotEquals(serverActivity, serverActivity2)
        Assert.assertNotEquals(serverActivity1, serverActivity2)
    }

    @Test
    @Throws(Exception::class)
    fun checkEqualsWithHashCode() {
        val serverActivity = ServerActivity(
                remoteId = 2386L,
                eventStartTimeNano = System.currentTimeMillis(),
                eventEndTimeNano = System.currentTimeMillis(),
                type = 0
        )
        val serverActivity1 = ServerActivity(
                remoteId = 2386L,
                eventStartTimeNano = System.currentTimeMillis(),
                eventEndTimeNano = System.currentTimeMillis(),
                type = 0
        )
        val serverActivity2 = ServerActivity(
                remoteId = 2386L,
                eventStartTimeNano = System.currentTimeMillis(),
                eventEndTimeNano = System.currentTimeMillis(),
                type = 1
        )

        Assert.assertEquals(serverActivity.hashCode(), serverActivity1.hashCode())
        Assert.assertNotEquals(serverActivity.hashCode(), serverActivity2.hashCode())
        Assert.assertNotEquals(serverActivity1.hashCode(), serverActivity2.hashCode())
    }
}
