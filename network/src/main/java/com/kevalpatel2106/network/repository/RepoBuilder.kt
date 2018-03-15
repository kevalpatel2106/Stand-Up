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

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class RepoBuilder<T> {

    private val mRepository: Repository<T> = Repository()

    fun addCache(cache: Cache<T>): RepoBuilder<T> {
        mRepository.mCaches.add(cache)
        return this
    }

    fun addRefresher(refresher: Refresher<T>): RepoBuilder<T> {
        mRepository.mRefresher.add(refresher)
        return this
    }

    fun build(): Repository<T> {
        if (mRepository.mRefresher.isEmpty() && mRepository.mCaches.isEmpty())
            throw IllegalStateException("No cache or refresher added.")
        return mRepository
    }
}
