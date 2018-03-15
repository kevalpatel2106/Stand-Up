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
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 05-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class RepoBuilderTest {

    @Test
    fun testAddingCache() {
        val repo = RepoBuilder<String>()
                .addCache(object : Cache<String> {
                    override fun read(): RepoData<String> {
                        //NO OP
                        return RepoData(false)
                    }

                    override fun write(t: String?) {
                        //NO OP
                    }
                })
                .build()

        Assert.assertEquals(repo.mCaches.size, 1)
        Assert.assertTrue(repo.mRefresher.isEmpty())
    }

    @Test
    fun testAddingRefresher() {
        val repo = RepoBuilder<String>()
                .addRefresher(object : Refresher<String> {
                    override fun read(): RepoData<String> {
                        //NO OP
                        return RepoData(false)
                    }
                })
                .build()

        Assert.assertEquals(repo.mRefresher.size, 1)
        Assert.assertTrue(repo.mCaches.isEmpty())
    }

    @Test
    fun testAddingRefresherAndCache() {
        val repo = RepoBuilder<String>()
                .addRefresher(object : Refresher<String> {
                    override fun read(): RepoData<String> {
                        //NO OP
                        return RepoData(false)
                    }
                })
                .addCache(object : Cache<String> {
                    override fun read(): RepoData<String> {
                        //NO OP
                        return RepoData(false)
                    }

                    override fun write(t: String?) {
                        //NO OP
                    }
                })
                .build()

        Assert.assertEquals(repo.mRefresher.size, 1)
        Assert.assertEquals(repo.mCaches.size, 1)
    }

    @Test
    fun testWithNoRefresherAndCache() {
        try {
            RepoBuilder<String>().build()

            Assert.fail()
        } catch (e: IllegalStateException) {
            //Test passed
            //NO OP
        }
    }
}
