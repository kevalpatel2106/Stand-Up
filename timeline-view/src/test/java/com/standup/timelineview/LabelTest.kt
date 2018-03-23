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

package com.standup.timelineview

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 23-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class LabelTest {

    @Test
    @Throws(Exception::class)
    fun checkTitle() {
        val title = "test-title"
        val label = Label(title, 12F)

        assertEquals(title, label.title)
    }

    @Test
    @Throws(Exception::class)
    fun checkX() {
        val testX = 12F
        val label = Label("test-title", testX)

        assertEquals(testX, label.x)
    }

    @Test
    @Throws(Exception::class)
    fun checkEquals(){
        val label = Label("test-title", 12F)
        val label1 = Label("test-title", 12F)
        val label2 = Label("test-title-1", 12F)

        assertEquals(label, label1)
        assertNotEquals(label2, label1)
        assertNotEquals(label2, label1)
    }


    @Test
    @Throws(Exception::class)
    fun checkEqualsAndHashcode(){
        val label = Label("test-title", 12F)
        val label1 = Label("test-title", 12F)
        val label2 = Label("test-title-1", 12F)

        assertEquals(label.hashCode(), label1.hashCode())
        assertNotEquals(label2.hashCode(), label1.hashCode())
        assertNotEquals(label2.hashCode(), label1.hashCode())
    }
}
