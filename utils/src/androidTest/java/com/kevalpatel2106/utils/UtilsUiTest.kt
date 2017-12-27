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

package com.kevalpatel2106.utils

import android.app.Activity
import android.provider.Settings
import android.support.test.InstrumentationRegistry
import com.kevalpatel2106.testutils.BaseTestClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test


/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class UtilsUiTest : BaseTestClass() {
    override fun getActivity(): Activity? = null

    @Test
    @Throws(Exception::class)
    fun testDeviceId() {
        assertNotNull(Utils.getDeviceId(InstrumentationRegistry.getTargetContext()))
        assertEquals(Utils.getDeviceId(InstrumentationRegistry.getTargetContext()),
                Settings.Secure.getString(InstrumentationRegistry.getContext().contentResolver,
                        Settings.Secure.ANDROID_ID))
    }
}