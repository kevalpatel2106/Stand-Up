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

package com.kevalpatel2106.standup.authentication.verification

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Keval on 26/11/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
@RunWith(JUnit4::class)
class VerifyEmailUiModelTest {

    @Test
    @Throws(IOException::class)
    fun checkIsSuccess() {
        val verifyEmailUiModel = VerifyEmailUiModel(true)
        assertTrue(verifyEmailUiModel.isSuccess)

        verifyEmailUiModel.isSuccess = false
        assertFalse(verifyEmailUiModel.isSuccess)
    }
}