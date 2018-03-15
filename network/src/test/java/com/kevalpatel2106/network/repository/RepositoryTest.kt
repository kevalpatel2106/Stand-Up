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

import com.kevalpatel2106.network.repository.cache.Cache
import com.kevalpatel2106.network.repository.refresher.Refresher
import io.reactivex.subscribers.TestSubscriber
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 15-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class RepositoryTest {

    private class TestCache : Cache<String> {
        var cachedData: String? = null

        override fun write(t: String?) {
            cachedData = t
        }

        override fun read(): RepoData<String> {
            val repoData = RepoData(false, cachedData)
            repoData.source = SourceType.CACHE
            return repoData
        }

    }

    private class TestErrorCache : Cache<String> {
        var cachedData: String? = null

        override fun write(t: String?) {
            cachedData = t
        }

        override fun read(): RepoData<String> {
            val repoData = RepoData(true, cachedData)
            repoData.source = SourceType.CACHE
            return repoData
        }

    }

    private class TestRefresherWithSuccess : Refresher<String> {
        val test_response = "test_response_success"
        val test_error = "test_error_success"
        val test_error_code = 200

        override fun read(): RepoData<String> {
            val repoData = RepoData(false, test_response)
            repoData.errorMessage = test_error
            repoData.source = SourceType.NETWORK
            repoData.errorCode = test_error_code
            return repoData
        }
    }

    private class TestRefresherWithError : Refresher<String> {
        val test_response = "test_response_error"
        val test_error = "test_error_error"

        override fun read(): RepoData<String> {
            val repoData = RepoData(true, test_response)
            repoData.errorMessage = test_error
            repoData.source = SourceType.NETWORK
            return repoData
        }
    }

    private lateinit var testRefresherWithSuccess: TestRefresherWithSuccess
    private lateinit var testRefresherWithError: TestRefresherWithError
    private lateinit var testCache: TestCache
    private lateinit var testCacheWithError: TestErrorCache

    private lateinit var repository: Repository<String>

    @Before
    fun setUp() {
        testRefresherWithSuccess = TestRefresherWithSuccess()
        testRefresherWithError = TestRefresherWithError()
        testCache = TestCache()
        testCacheWithError = TestErrorCache()

        repository = Repository()
    }

    @Test
    @Throws(Exception::class)
    fun checkSuccessFromRefresher_WithNoCache() {
        repository.mRefresher = testRefresherWithSuccess

        val testSubscriber = TestSubscriber<RepoData<String>>()

        repository.fetch().subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0, { it.errorMessage == null })
                .assertValueAt(0, { it.source == SourceType.NETWORK })
                .assertValueAt(0, { it.data == testRefresherWithSuccess.test_response })
                .assertValueAt(0, { it.errorCode == testRefresherWithSuccess.test_error_code })
                .assertComplete()

        //No cache
        //There must no effect in cache
        Assert.assertNull(testCache.cachedData)
    }

    @Test
    @Throws(Exception::class)
    fun checkSuccessFromRefresher_WithEmptyCache() {
        repository.mCaches.add(testCache)
        repository.mRefresher = testRefresherWithSuccess

        val testSubscriber = TestSubscriber<RepoData<String>>()

        repository.fetch().subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0, { it.errorMessage == null })
                .assertValueAt(0, { it.source == SourceType.NETWORK })
                .assertValueAt(0, { it.data == testRefresherWithSuccess.test_response })
                .assertValueAt(0, { it.errorCode == testRefresherWithSuccess.test_error_code })
                .assertComplete()

        //Check if the value from the refresher cached?
        Assert.assertEquals(testRefresherWithSuccess.test_response, testCache.cachedData)
    }

    @Test
    @Throws(Exception::class)
    fun checkSuccessFromRefresher_WithMultipleEmptyCache() {
        val testCache2 = TestCache()
        repository.mCaches.add(testCache)
        repository.mCaches.add(testCache2)
        repository.mRefresher = testRefresherWithSuccess

        val testSubscriber = TestSubscriber<RepoData<String>>()

        repository.fetch().subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0, { it.errorMessage == null })
                .assertValueAt(0, { it.source == SourceType.NETWORK })
                .assertValueAt(0, { it.data == testRefresherWithSuccess.test_response })
                .assertValueAt(0, { it.errorCode == testRefresherWithSuccess.test_error_code })
                .assertComplete()

        //Check if the value from the refresher cached?
        Assert.assertEquals(testRefresherWithSuccess.test_response, testCache.cachedData)
        Assert.assertEquals(testRefresherWithSuccess.test_response, testCache2.cachedData)
    }

    @Test
    @Throws(Exception::class)
    fun checkSuccessFromRefresher_WithMultipleFilledCache() {
        val testCacheData1 = "data-1"
        val testCacheData2 = "data-2"

        val testCache2 = TestCache()

        //Fill the cache
        testCache.cachedData = testCacheData1
        testCache2.cachedData = testCacheData2

        //Add cache and refresher
        repository.mCaches.add(testCache)
        repository.mCaches.add(testCache2)
        repository.mRefresher = testRefresherWithSuccess

        val testSubscriber = TestSubscriber<RepoData<String>>()

        repository.fetch().subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertValueCount(2)

                //Result from cache 1
                .assertValueAt(0, { it.errorMessage == null })
                .assertValueAt(0, { it.source == SourceType.CACHE })
                .assertValueAt(0, { it.data == testCacheData1 })
                .assertValueAt(0, { it.errorCode == 0 })

                //Result from refresher
                .assertValueAt(1, { it.errorMessage == null })
                .assertValueAt(1, { it.source == SourceType.NETWORK })
                .assertValueAt(1, { it.data == testRefresherWithSuccess.test_response })
                .assertValueAt(1, { it.errorCode == testRefresherWithSuccess.test_error_code })
                .assertComplete()

        //Check if the value from the refresher cached?
        Assert.assertEquals(testRefresherWithSuccess.test_response, testCache.cachedData)
        Assert.assertEquals(testRefresherWithSuccess.test_response, testCache2.cachedData)
    }

    @Test
    @Throws(Exception::class)
    fun checkSuccessFromRefresher_WithOneCorrectCache() {
        val testCacheData1 = "data-1"
        val testCacheData2 = "data-2"

        //Fill the cache
        testCache.cachedData = testCacheData1
        testCacheWithError.cachedData = testCacheData2

        //Add cache and refresher
        repository.mCaches.add(testCacheWithError)
        repository.mCaches.add(testCache)
        repository.mRefresher = testRefresherWithSuccess

        val testSubscriber = TestSubscriber<RepoData<String>>()

        repository.fetch().subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertValueCount(2)

                //Result from cache 1
                .assertValueAt(0, { it.errorMessage == null })
                .assertValueAt(0, { it.source == SourceType.CACHE })
                .assertValueAt(0, { it.data == testCacheData1 })
                .assertValueAt(0, { it.errorCode == 0 })

                //Result from refresher
                .assertValueAt(1, { it.errorMessage == null })
                .assertValueAt(1, { it.source == SourceType.NETWORK })
                .assertValueAt(1, { it.data == testRefresherWithSuccess.test_response })
                .assertValueAt(1, { it.errorCode == testRefresherWithSuccess.test_error_code })
                .assertComplete()

        //Check if the value from the refresher cached?
        Assert.assertEquals(testRefresherWithSuccess.test_response, testCache.cachedData)
        Assert.assertEquals(testRefresherWithSuccess.test_response, testCacheWithError.cachedData)
    }

    @Test
    @Throws(Exception::class)
    fun checkErrorFromRefresher_WithEmptyCache() {
        repository.mCaches.add(testCache)
        repository.mRefresher = testRefresherWithError

        val testSubscriber = TestSubscriber<RepoData<String>>()

        repository.fetch().subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertErrorMessage(testRefresherWithError.test_error)
                .assertValueCount(0)

        //Check if the value from the refresher cached?
        // Refresher had error so it must be null
        Assert.assertNull(testCache.cachedData)
    }

    @Test
    @Throws(Exception::class)
    fun checkErrorFromRefresher_WithNoCache() {
        repository.mRefresher = testRefresherWithError

        val testSubscriber = TestSubscriber<RepoData<String>>()

        repository.fetch().subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertErrorMessage(testRefresherWithError.test_error)
                .assertValueCount(0)

        //No cache
        //There must no effect in cache
        Assert.assertNull(testCache.cachedData)
    }

    @Test
    @Throws(Exception::class)
    fun checkErrorFromRefresher_WithMultipleEmptyCache() {
        val testCache2 = TestCache()
        repository.mCaches.add(testCache)
        repository.mCaches.add(testCache2)
        repository.mRefresher = testRefresherWithError

        val testSubscriber = TestSubscriber<RepoData<String>>()

        repository.fetch().subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertErrorMessage(testRefresherWithError.test_error)
                .assertValueCount(0)

        //No cache
        //There must no effect in cache
        Assert.assertNull(testCache.cachedData)
        Assert.assertNull(testCache2.cachedData)
    }


    @Test
    @Throws(Exception::class)
    fun checkErrorFromRefresher_WithMultipleFilledCache() {
        val testCacheData1 = "data-1"
        val testCacheData2 = "data-2"

        val testCache2 = TestCache()

        //Fill the cache
        testCache.cachedData = testCacheData1
        testCache2.cachedData = testCacheData2

        //Add cache and refresher
        repository.mCaches.add(testCache)       //Priority 1
        repository.mCaches.add(testCache2)      //Priority 2
        repository.mRefresher = testRefresherWithError

        val testSubscriber = TestSubscriber<RepoData<String>>()

        repository.fetch().subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNotComplete()
                .assertValueCount(1)

                //Result from cache 1
                .assertValueAt(0, { it.errorMessage == null })
                .assertValueAt(0, { it.source == SourceType.CACHE })
                .assertValueAt(0, { it.data == testCacheData1 })
                .assertValueAt(0, { it.errorCode == 0 })

                //Error from network
                .assertErrorMessage(testRefresherWithError.test_error)

        //Cache states remain intact
        Assert.assertEquals(testCacheData1, testCache.cachedData)
        Assert.assertEquals(testCacheData2, testCache2.cachedData)
    }

    @Test
    @Throws(Exception::class)
    fun checkErrorFromRefresher_WithOneCorrectCache() {
        val testCacheData1 = "data-1"
        val testCacheData2 = "data-2"

        //Fill the cache
        testCache.cachedData = testCacheData1
        testCacheWithError.cachedData = testCacheData2

        //Add cache and refresher
        repository.mCaches.add(testCacheWithError)  //Priority 1
        repository.mCaches.add(testCache)           //Priority 2
        repository.mRefresher = testRefresherWithError

        val testSubscriber = TestSubscriber<RepoData<String>>()

        repository.fetch().subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNotComplete()
                .assertValueCount(1)

                //Result from cache 1
                .assertValueAt(0, { it.errorMessage == null })
                .assertValueAt(0, { it.source == SourceType.CACHE })
                .assertValueAt(0, { it.data == testCacheData1 })
                .assertValueAt(0, { it.errorCode == 0 })

                //Error from network
                .assertErrorMessage(testRefresherWithError.test_error)


        //Cache states remain intact
        Assert.assertEquals(testCacheData1, testCache.cachedData)
        Assert.assertEquals(testCacheData2, testCacheWithError.cachedData)
    }
}
