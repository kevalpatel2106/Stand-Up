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

package com.kevalpatel2106.common.repository

import org.junit.Assert
import org.junit.Test

/**
 * Created by Keval on 04/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class RepoDataTest {

    @Test
    fun checkConstructorInit() {
        val repoData = RepoData(true, "This is test data.")

        Assert.assertTrue(repoData.isError)
        Assert.assertEquals(repoData.data, "This is test data.")
        Assert.assertNull(repoData.errorMessage)
        Assert.assertEquals(repoData.errorCode, 0)
    }

    @Test
    fun checkConstructorInit1() {
        val repoData = RepoData<String>(true)

        Assert.assertTrue(repoData.isError)
        Assert.assertNull(repoData.data)
        Assert.assertNull(repoData.errorMessage)
        Assert.assertEquals(repoData.errorCode, 0)
    }

    @Test
    fun checkSettingError() {
        val repoData = RepoData<String>(true)

        Assert.assertTrue(repoData.isError)

        repoData.errorMessage = "This is test message."
        Assert.assertEquals(repoData.errorMessage, "This is test message.")

        repoData.errorCode = 401
        Assert.assertEquals(repoData.errorCode, 401)
    }

    @Test
    fun checkGetErrorMessageWithoutError() {
        val repoData = RepoData<String>(false)

        repoData.errorMessage = "This is test message."
        repoData.errorCode = 401

        Assert.assertNull(repoData.errorMessage)
        Assert.assertEquals(repoData.errorCode, 401)
    }

    @Test
    fun checkSourceType() {
        val repoData = RepoData<String>(false)

        repoData.source = SourceType.CACHE
        Assert.assertEquals(repoData.source, SourceType.CACHE)

        repoData.source = SourceType.NETWORK
        Assert.assertEquals(repoData.source, SourceType.NETWORK)
    }
}
