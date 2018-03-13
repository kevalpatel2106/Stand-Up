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

import com.kevalpatel2106.utils.TimeUtils
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
class GetActivityRequestTest {

    @Test
    fun checkInit() {
        try {
            val oldestTime = TimeUtils.convertToNano(System.currentTimeMillis())
            val request = GetActivityRequest(oldestTime)

            Assert.assertEquals(oldestTime, request.oldestTimeStampNano)
        } catch (e: IllegalArgumentException) {
            Assert.fail()
        }
    }

    @Test
    fun checkInvalidOldestTime() {
        try {
            val oldestTime = TimeUtils.convertToNano(System.currentTimeMillis() + 10_000L /* 10 secs */)
            GetActivityRequest(oldestTime)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    fun checkEquals() {
        val oldestTime = TimeUtils.convertToNano(System.currentTimeMillis())
        val request = GetActivityRequest(oldestTime)
        val request1 = GetActivityRequest(oldestTime)
        val request2 = GetActivityRequest(oldestTime - 10_000L /* 10000 nano seconds */)


        Assert.assertEquals(request, request1)
        Assert.assertNotEquals(request, request2)
        Assert.assertNotEquals(request1, request2)
    }
}