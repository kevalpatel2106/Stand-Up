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

package com.kevalpatel2106.standup.about.repo

import com.kevalpatel2106.standup.BuildConfig
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 01-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class CheckVersionResponseTest {

    @Test
    @Throws(IOException::class)
    fun checkInitWithUpdateAvailable() {

        val checkVersionResponse = CheckVersionResponse("1.0",
                BuildConfig.VERSION_CODE + 2, null)

        Assert.assertNull(checkVersionResponse.releaseNotes)
        Assert.assertEquals(checkVersionResponse.latestVersionCode, BuildConfig.VERSION_CODE + 2)
        Assert.assertEquals(checkVersionResponse.latestVersionName, "1.0")
        Assert.assertTrue(checkVersionResponse.isUpdate)
    }

    @Test
    @Throws(IOException::class)
    fun checkInitWithAlreadyLatestVersion() {

        val checkVersionResponse = CheckVersionResponse("1.0",
                BuildConfig.VERSION_CODE, null)

        Assert.assertNull(checkVersionResponse.releaseNotes)
        Assert.assertEquals(checkVersionResponse.latestVersionCode, BuildConfig.VERSION_CODE)
        Assert.assertEquals(checkVersionResponse.latestVersionName, "1.0")
        Assert.assertFalse(checkVersionResponse.isUpdate)
    }


    @Test
    @Throws(IOException::class)
    fun checkInitWithInvalidVersionCode() {
        try {
            CheckVersionResponse("1.0", 0, null)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test Passed
            //NO OP
        }
    }
}
