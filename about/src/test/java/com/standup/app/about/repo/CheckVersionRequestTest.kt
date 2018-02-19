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

package com.standup.app.about.repo

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
class CheckVersionRequestTest {

    @Test
    @Throws(IOException::class)
    fun checkInit() {
        val request = CheckVersionRequest()
        Assert.assertEquals(request.platform, CheckVersionRequest.PLATFORM_NAME_ANDROID)
    }

    @Test
    @Throws(IOException::class)
    fun checkEquals() {
        val request = CheckVersionRequest()
        val request1 = CheckVersionRequest()
        Assert.assertEquals(request, request1)
        Assert.assertEquals(request, CheckVersionRequest.PLATFORM_NAME_ANDROID)
    }

    @Test
    @Throws(IOException::class)
    fun checkHashCode() {
        val request = CheckVersionRequest()
        Assert.assertEquals(request.hashCode(), CheckVersionRequest.PLATFORM_NAME_ANDROID.hashCode())
    }
}
