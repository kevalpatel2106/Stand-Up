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

package com.kevalpatel2106.standup.core.repo

import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

/**
 * Created by Keval on 31/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SaveActivityRequestTest {

    @Test
    fun checkInvalidId() {
        try {
            SaveActivityRequest(-1, System.currentTimeMillis(), System.currentTimeMillis() + 1, 0)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkInvalidEndTime() {
        try {
            SaveActivityRequest(23984L, System.currentTimeMillis(), -System.currentTimeMillis() + 1, 0)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkInvalidStartTime() {
        try {
            SaveActivityRequest(23423, -System.currentTimeMillis(), System.currentTimeMillis() + 1, 0)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkInvalidType() {
        try {
            SaveActivityRequest(349857L, System.currentTimeMillis(), System.currentTimeMillis() + 1, 2)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkInvalidType1() {
        try {
            SaveActivityRequest(349857L, System.currentTimeMillis(), System.currentTimeMillis() + 1, -1)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkInit_WithUserMovingActivity() {
        val userActivity = UserActivity(remoteId = 234L,
                eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 1,
                type = UserActivityType.MOVING.toString().toLowerCase(),
                isSynced = false)

        userActivity.localId = 38475L

        val data = SaveActivityRequest(userActivity)

        Assert.assertEquals(data.id, userActivity.remoteId)
        Assert.assertEquals(data.startTime, TimeUnit.NANOSECONDS.convert(userActivity.eventStartTimeMills, TimeUnit.MILLISECONDS))
        Assert.assertEquals(data.endTime, TimeUnit.NANOSECONDS.convert(userActivity.eventEndTimeMills, TimeUnit.MILLISECONDS))
        Assert.assertEquals(data.type, 1)

    }

    @Test
    fun checkInit_WithUserSittingActivity() {
        val userActivity = UserActivity(remoteId = 234L,
                eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 1,
                type = UserActivityType.SITTING.toString().toLowerCase(),
                isSynced = false)

        userActivity.localId = 38475L

        val data = SaveActivityRequest(userActivity)

        Assert.assertEquals(data.id, userActivity.remoteId)
        Assert.assertEquals(data.startTime, TimeUnit.NANOSECONDS.convert(userActivity.eventStartTimeMills, TimeUnit.MILLISECONDS))
        Assert.assertEquals(data.endTime, TimeUnit.NANOSECONDS.convert(userActivity.eventEndTimeMills, TimeUnit.MILLISECONDS))
        Assert.assertEquals(data.type, 0)

    }

    @Test
    fun checkInit_WithUserUnknownActivity() {
        val userActivity = UserActivity(remoteId = 234L,
                eventStartTimeMills = System.currentTimeMillis(),
                eventEndTimeMills = System.currentTimeMillis() + 1,
                type = UserActivityType.NOT_TRACKED.toString(),
                isSynced = false)

        userActivity.localId = 38475L

        try {
            SaveActivityRequest(userActivity)
            Assert.fail()
        } catch (e: IllegalStateException) {
            //Test passed.
        }
    }

}