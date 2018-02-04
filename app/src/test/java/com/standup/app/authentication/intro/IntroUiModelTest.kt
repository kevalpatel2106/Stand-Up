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

package com.standup.app.authentication.intro

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class IntroUiModelTest {

    @Test
    @Throws(IOException::class)
    fun checkIsSuccess() {
        val introUiModel = IntroUiModel(true)
        assertTrue(introUiModel.isSuccess)

        introUiModel.isSuccess = false
        assertFalse(introUiModel.isSuccess)
    }

    @Test
    @Throws(IOException::class)
    fun checkIsNewUser() {
        val introUiModel = IntroUiModel(true)
        assertFalse(introUiModel.isNewUser)

        introUiModel.isNewUser = true
        assertTrue(introUiModel.isNewUser)

        introUiModel.isSuccess = false
        assertFalse(introUiModel.isNewUser)
    }
}