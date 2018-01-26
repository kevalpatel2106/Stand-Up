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

package com.kevalpatel2106.utils.rxbus

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Keval on 26/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class EventTest {

    @Test
    fun checkTag() {
        val testTag = "test-tag"
        val event = Event(testTag)

        Assert.assertEquals(event.tag, testTag)
    }

    @Test
    fun checkEquals() {
        val event1 = Event("tag")
        val event2 = Event("tag")
        val event3 = Event("different-tag")

        Assert.assertEquals(event1, event2)
        Assert.assertNotEquals(event1, event3)
        Assert.assertNotEquals(event2, event3)
    }

    @Test
    fun checkHashCode() {
        val event1 = Event("tag")
        val event2 = Event("tag")
        val event3 = Event("different-tag")

        Assert.assertEquals(event1.hashCode(), event2.hashCode())
        Assert.assertNotEquals(event1.hashCode(), event3.hashCode())
        Assert.assertNotEquals(event2.hashCode(), event3.hashCode())
    }

}