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

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.standup.app.settings.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 12-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class InstructionViewModelTest {
    private val TITLE = "test title"
    private val MESSAGE = "test message"

    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var model: InstructionViewModel

    private lateinit var application: Application

    @Before
    fun setUp() {
        application = Mockito.mock(Application::class.java)
        Mockito.`when`(application.getString(R.string.instruction_title_1)).thenReturn(TITLE)
        Mockito.`when`(application.getString(R.string.instruction_title_2)).thenReturn(TITLE)
        Mockito.`when`(application.getString(R.string.instruction_title_3)).thenReturn(TITLE)
        Mockito.`when`(application.getString(R.string.instruction_title_4)).thenReturn(TITLE)
        Mockito.`when`(application.getString(R.string.instruction_title_5)).thenReturn(TITLE)
        Mockito.`when`(application.getString(R.string.instruction_title_6)).thenReturn(TITLE)

        Mockito.`when`(application.getString(R.string.instruction_message_1)).thenReturn(MESSAGE)
        Mockito.`when`(application.getString(R.string.instruction_message_2)).thenReturn(MESSAGE)
        Mockito.`when`(application.getString(R.string.instruction_message_3)).thenReturn(MESSAGE)
        Mockito.`when`(application.getString(R.string.instruction_message_4)).thenReturn(MESSAGE)
        Mockito.`when`(application.getString(R.string.instruction_message_5)).thenReturn(MESSAGE)
        Mockito.`when`(application.getString(R.string.instruction_message_6)).thenReturn(MESSAGE)

        model = InstructionViewModel(application)
    }

    @Test
    @Throws(Exception::class)
    fun checkInit() {
        model.init()

        assertNotNull(model.instructions.value)
        assertEquals(model.instructions.value!!.size, 6)
    }

    @Test
    @Throws(Exception::class)
    fun checkFirstInstruction() {
        Mockito.`when`(application.getString(R.string.instruction_title_1)).thenReturn(TITLE)
        Mockito.`when`(application.getString(R.string.instruction_message_1)).thenReturn(MESSAGE)

        model.loadInstructions()

        assertEquals(TITLE, model.instructions.value!![0].heading)
        assertEquals(MESSAGE, model.instructions.value!![0].message)
        assertEquals(R.drawable.ic_intro_how_to, model.instructions.value!![0].icon)
    }

    @Test
    @Throws(Exception::class)
    fun checkSecondInstruction() {
        Mockito.`when`(application.getString(R.string.instruction_title_2)).thenReturn(TITLE)
        Mockito.`when`(application.getString(R.string.instruction_message_2)).thenReturn(MESSAGE)

        model.loadInstructions()

        assertEquals(TITLE, model.instructions.value!![1].heading)
        assertEquals(MESSAGE, model.instructions.value!![1].message)
        assertEquals(R.drawable.ic_intro_drive_steps, model.instructions.value!![1].icon)
    }

    @Test
    @Throws(Exception::class)
    fun checkThirdInstruction() {
        Mockito.`when`(application.getString(R.string.instruction_title_3)).thenReturn(TITLE)
        Mockito.`when`(application.getString(R.string.instruction_message_3)).thenReturn(MESSAGE)

        model.loadInstructions()

        assertEquals(TITLE, model.instructions.value!![2].heading)
        assertEquals(MESSAGE, model.instructions.value!![2].message)
        assertEquals(R.drawable.ic_intro_accuracy, model.instructions.value!![2].icon)
    }

    @Test
    @Throws(Exception::class)
    fun checkFourthInstruction() {
        Mockito.`when`(application.getString(R.string.instruction_title_4)).thenReturn(TITLE)
        Mockito.`when`(application.getString(R.string.instruction_message_4)).thenReturn(MESSAGE)

        model.loadInstructions()

        assertEquals(TITLE, model.instructions.value!![3].heading)
        assertEquals(MESSAGE, model.instructions.value!![3].message)
        assertEquals(R.drawable.ic_intro_placement_suggestion, model.instructions.value!![3].icon)
    }

    @Test
    @Throws(Exception::class)
    fun checkFifthInstruction() {
        Mockito.`when`(application.getString(R.string.instruction_title_5)).thenReturn(TITLE)
        Mockito.`when`(application.getString(R.string.instruction_message_5)).thenReturn(MESSAGE)

        model.loadInstructions()

        assertEquals(TITLE, model.instructions.value!![4].heading)
        assertEquals(MESSAGE, model.instructions.value!![4].message)
        assertEquals(R.drawable.ic_intro_battery_saving, model.instructions.value!![4].icon)
    }

    @Test
    @Throws(Exception::class)
    fun checkSixInstruction() {
        Mockito.`when`(application.getString(R.string.instruction_title_6)).thenReturn(TITLE)
        Mockito.`when`(application.getString(R.string.instruction_message_6)).thenReturn(MESSAGE)

        model.loadInstructions()

        assertEquals(TITLE, model.instructions.value!![5].heading)
        assertEquals(MESSAGE, model.instructions.value!![5].message)
        assertEquals(R.drawable.ic_intro_stops_counting, model.instructions.value!![5].icon)
    }
}
