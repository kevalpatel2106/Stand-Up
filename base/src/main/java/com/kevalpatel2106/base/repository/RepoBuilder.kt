package com.kevalpatel2106.base.repository

import com.kevalpatel2106.base.repository.cache.Cache
import com.kevalpatel2106.base.repository.refresher.Refresher

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