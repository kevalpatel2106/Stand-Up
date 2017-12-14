package com.kevalpatel2106.base.repository.refresher

import com.kevalpatel2106.base.repository.RepoData

/**
 * Created by Kevalpatel2106 on 29-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface Refresher<out T> {

    /**
     * Read from the cache.
     */
    fun read(): RepoData<T>
}