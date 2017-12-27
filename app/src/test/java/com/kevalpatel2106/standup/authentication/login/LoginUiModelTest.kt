/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.authentication.login

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
@RunWith(JUnit4::class)
class LoginUiModelTest {

    @Test
    @Throws(IOException::class)
    fun checkIsSuccess() {
        val loginUiModel = LoginUiModel(true)
        assertTrue(loginUiModel.isSuccess)

        loginUiModel.isSuccess = false
        assertFalse(loginUiModel.isSuccess)
    }

    @Test
    @Throws(IOException::class)
    fun checkIsNewUser() {
        val loginUiModel = LoginUiModel(true)
        assertFalse(loginUiModel.isNewUser)

        loginUiModel.isNewUser = true
        assertTrue(loginUiModel.isNewUser)

        loginUiModel.isSuccess = false
        assertFalse(loginUiModel.isNewUser)
    }

    @Test
    @Throws(IOException::class)
    fun checkIsVerified() {
        val loginUiModel = LoginUiModel(true)
        assertTrue(loginUiModel.isVerify)

        loginUiModel.isNewUser = true
        assertTrue(loginUiModel.isVerify)

        loginUiModel.isSuccess = false
        assertFalse(loginUiModel.isVerify)
    }
}