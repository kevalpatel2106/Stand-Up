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

package com.standup.app.settings.instructions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 12-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class InstructionTest {

    private val test_title = "TEST TITLE"
    private val test_message = "TEST MESSAGE"
    private val test_icon = 435897

    @Test
    @Throws(Exception::class)
    fun checkHeading() {
        val instruction = Instruction(test_title, test_message, test_icon)
        assertEquals(test_title, instruction.heading)
    }

    @Test
    @Throws(Exception::class)
    fun checkMessage() {
        val instruction = Instruction(test_title, test_message, test_icon)
        assertEquals(test_message, instruction.message)
    }

    @Test
    @Throws(Exception::class)
    fun checkIcon() {
        val instruction = Instruction(test_title, test_message, test_icon)
        assertEquals(test_icon, instruction.icon)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetChildList_Size() {
        val instruction = Instruction(test_title, test_message, test_icon)
        assertEquals(1, instruction.childList.size)
    }

    @Test
    @Throws(Exception::class)
    fun checkGetChildList_Item() {
        val instruction = Instruction(test_title, test_message, test_icon)
        assertEquals(test_message, instruction.childList.first())
    }

    @Test
    @Throws(Exception::class)
    fun checkInitiallyExpanded() {
        val instruction = Instruction(test_title, test_message, test_icon)
        assertEquals(false, instruction.isInitiallyExpanded)
    }

    @Test
    @Throws(Exception::class)
    fun checkEquals() {
        val instruction = Instruction(test_title, test_message, test_icon)
        val instruction1 = Instruction(test_title, test_message, test_icon)
        val instruction2 = Instruction(test_title.plus("1"), test_message.plus("1"), test_icon + 45)

        assertEquals(instruction, instruction1)
        assertNotEquals(instruction, instruction2)
        assertNotEquals(instruction1, instruction2)
    }

    @Test
    @Throws(Exception::class)
    fun checkHashCode() {
        val instruction = Instruction(test_title, test_message, test_icon)
        val instruction1 = Instruction(test_title, test_message, test_icon)
        val instruction2 = Instruction(test_title.plus("1"), test_message.plus("1"), test_icon + 45)

        assertEquals(instruction.hashCode(), instruction1.hashCode())
        assertNotEquals(instruction.hashCode(), instruction2.hashCode())
        assertNotEquals(instruction1.hashCode(), instruction2.hashCode())
    }

}
