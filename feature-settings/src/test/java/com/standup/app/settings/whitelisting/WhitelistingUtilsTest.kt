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

package com.standup.app.settings.whitelisting

import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Created by Kevalpatel2106 on 23-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(Enclosed::class)
class WhitelistingUtilsTest {

    @RunWith(RobolectricTestRunner::class)
    @Config(manifest = Config.NONE, sdk = [21])
    class WhitelistingUtilsTest23 {

        @Test
        @Throws(Exception::class)
        fun checkShouldOpenWhiteListDialog_LessThan23() {
            assertFalse(WhitelistingUtils().shouldOpenWhiteListDialog(RuntimeEnvironment.application))
        }
    }
//
//    @RunWith(RobolectricTestRunner::class)
//    @Config(manifest = Config.NONE, sdk = [26])
//    class WhitelistingUtilsTest26 {
//
//        private lateinit var context: Context
//        private lateinit var powerManager: PowerManager
//
//        @Before
//        fun setUp() {
//            context = Mockito.spy(RuntimeEnvironment.application)
//            Mockito.`when`(context.getString(anyInt())).thenReturn("com.standup")
//
//            powerManager = Mockito.mock(PowerManager::class.java)
//            Mockito.`when`(context.getSystemService(anyString())).thenReturn(powerManager)
//        }
//
//        @Test
//        @Throws(Exception::class)
//        fun checkShouldOpenWhiteListDialog_IgnoringBatteryOptimizations() {
//            Mockito.`when`(powerManager.isIgnoringBatteryOptimizations(anyString())).thenReturn(true)
//
//            assertFalse(WhitelistingUtils().shouldOpenWhiteListDialog(context))
//        }
//
//        @Test
//        @Throws(Exception::class)
//        fun checkShouldOpenWhiteListDialog_NotIgnoringBatteryOptimizations() {
//            Mockito.`when`(powerManager.isIgnoringBatteryOptimizations(anyString())).thenReturn(false)
//
//            assertTrue(WhitelistingUtils().shouldOpenWhiteListDialog(context))
//        }
//    }
}
