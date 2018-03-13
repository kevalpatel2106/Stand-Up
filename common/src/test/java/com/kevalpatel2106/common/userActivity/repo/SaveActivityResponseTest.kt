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
class SaveActivityResponseTest {

    @Test
    fun checkInvalidId() {
        try {
            SaveActivityResponse(-1, System.currentTimeMillis(), System.currentTimeMillis() + 1, 0, 10)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkInvalidEndTime() {
        try {
            SaveActivityResponse(23984L, System.currentTimeMillis(), -System.currentTimeMillis() + 1, 0, 10)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkInvalidStartTime() {
        try {
            SaveActivityResponse(23423, -System.currentTimeMillis(), System.currentTimeMillis() + 1, 0, 10)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkInvalidUserId() {
        try {
            SaveActivityResponse(349857L, System.currentTimeMillis(), System.currentTimeMillis() + 1, 0, -10)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkInvalidType() {
        try {
            SaveActivityResponse(349857L, System.currentTimeMillis(), System.currentTimeMillis() + 1, 2, 10)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkInvalidType1() {
        try {
            SaveActivityResponse(349857L, System.currentTimeMillis(), System.currentTimeMillis() + 1, -1, 10)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkEqualsWithNull() {
        val data = SaveActivityResponse(349857L, System.currentTimeMillis(), System.currentTimeMillis() + 1, 1, 10)
        Assert.assertNotEquals(data, null)
    }

    @Test
    fun checkEqualsWithOtherObject() {
        val data = SaveActivityResponse(349857L, System.currentTimeMillis(), System.currentTimeMillis() + 1, 1, 10)
        Assert.assertNotEquals(data, "String Object")
    }

    @Test
    fun checkInit() {
        val id = 8237L
        val startTime = System.currentTimeMillis()
        val endTime = System.currentTimeMillis() + 1000
        val userId = 10L
        val type = 1
        val data = SaveActivityResponse(id, startTime, endTime, type, userId)

        Assert.assertEquals(id, data.id)
        Assert.assertEquals(startTime, data.startTime)
        Assert.assertEquals(endTime, data.endTime)
        Assert.assertEquals(type, data.type)
        Assert.assertEquals(userId, data.userId)
    }

    @Test
    fun checkEquals() {
        val data = SaveActivityResponse(349857L, System.currentTimeMillis(), System.currentTimeMillis() + 1, 1, 10)
        val data1 = SaveActivityResponse(349857L, System.currentTimeMillis(), System.currentTimeMillis() + 10, 0, 348957L)
        val data2 = SaveActivityResponse(349858L, System.currentTimeMillis(), System.currentTimeMillis() + 1, 1, 10)
        Assert.assertNotEquals(data, data2)
        Assert.assertNotEquals(data1, data2)
        Assert.assertEquals(data, data1)
    }

    @Test
    fun checkHashcode() {
        val id = 349857L
        val data = SaveActivityResponse(id, System.currentTimeMillis(), System.currentTimeMillis() + 1, 1, 10)
        Assert.assertEquals(data.hashCode(), id.hashCode())
    }

    @Test
    fun checkHashCodeWithEquals() {
        val data = SaveActivityResponse(349857L, System.currentTimeMillis(), System.currentTimeMillis() + 1, 1, 10)
        val data1 = SaveActivityResponse(349857L, System.currentTimeMillis(), System.currentTimeMillis() + 10, 0, 348957L)
        val data2 = SaveActivityResponse(349858L, System.currentTimeMillis(), System.currentTimeMillis() + 1, 1, 10)
        Assert.assertNotEquals(data.hashCode(), data2.hashCode())
        Assert.assertNotEquals(data1.hashCode(), data2.hashCode())
        Assert.assertEquals(data.hashCode(), data1.hashCode())
    }
}
