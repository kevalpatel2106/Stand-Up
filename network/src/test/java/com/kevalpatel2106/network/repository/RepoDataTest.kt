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

package com.kevalpatel2106.network.repository

import org.junit.Assert
import org.junit.Test

/**
 * Created by Keval on 04/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class RepoDataTest {

    @Test
    fun checkSuccess_WithoutErrorMessage() {
        val repoData = RepoData(false, "This is test data.")

        Assert.assertFalse(repoData.isError)
        Assert.assertEquals(repoData.data, "This is test data.")

        Assert.assertNull(repoData.errorMessage)
        Assert.assertEquals(repoData.errorCode, 0)
    }

    @Test
    fun checkSuccess_WithoutData() {
        val repoData = RepoData<String>(false)

        Assert.assertFalse(repoData.isError)
        Assert.assertNull(repoData.data)

        Assert.assertNull(repoData.errorMessage)
        Assert.assertEquals(repoData.errorCode, 0)
    }


    @Test
    fun checkError_WithoutErrorMessage() {
        val repoData = RepoData(true, "This is test data.")

        Assert.assertTrue(repoData.isError)
        Assert.assertEquals(repoData.data, "This is test data.")

        Assert.assertNull(repoData.errorMessage)
        Assert.assertEquals(repoData.errorCode, 0)
    }

    @Test
    fun checkError_WithoutData() {
        val repoData = RepoData<String>(true)

        Assert.assertTrue(repoData.isError)
        Assert.assertNull(repoData.data)

        Assert.assertNull(repoData.errorMessage)
        Assert.assertEquals(repoData.errorCode, 0)
    }

    @Test
    fun checkError_WithoutData_WithErrorMessage() {
        val repoData = RepoData<String>(true)

        Assert.assertTrue(repoData.isError)

        repoData.errorMessage = "This is test message."
        Assert.assertEquals(repoData.errorMessage, "This is test message.")

        repoData.errorCode = 401
        Assert.assertEquals(repoData.errorCode, 401)
    }

    @Test
    fun checkGetErrorMessage_WithoutError_WithoutData() {
        val repoData = RepoData<String>(false)

        repoData.errorMessage = "This is test message."

        Assert.assertNull(repoData.errorMessage)
        Assert.assertEquals(repoData.errorCode, 0)
    }

    @Test
    fun checkErrorCode_WithoutError() {
        val repoData = RepoData<String>(false)
        repoData.errorCode = 401
        Assert.assertEquals(repoData.errorCode, 401)
    }

    @Test
    fun checkErrorCode_WithError() {
        val repoData = RepoData<String>(true)
        repoData.errorCode = 401
        Assert.assertEquals(repoData.errorCode, 401)
    }

    @Test
    fun checkSourceTypeCache() {
        val repoData = RepoData<String>(false)

        repoData.source = SourceType.CACHE
        Assert.assertEquals(repoData.source, SourceType.CACHE)
    }

    @Test
    fun checkSourceTypeNetwork() {
        val repoData = RepoData<String>(false)

        repoData.source = SourceType.NETWORK
        Assert.assertEquals(repoData.source, SourceType.NETWORK)
    }
}
