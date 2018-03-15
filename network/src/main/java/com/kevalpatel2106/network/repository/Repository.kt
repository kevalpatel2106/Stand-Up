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
import com.kevalpatel2106.network.repository.refresher.RefreshException
import com.kevalpatel2106.network.repository.refresher.Refresher
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter

/**
 * Created by Keval on 28/11/17.
 * Class that exposes public api of the module.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class Repository<T> internal constructor() {

    internal val mCaches: ArrayList<Cache<T>> = ArrayList()

    internal var mRefresher: Refresher<T>? = null

    fun fetch(): Flowable<RepoData<T>> {

        @Suppress("LoopToCallChain", "UnnecessaryVariable")
        val flowable = Flowable.create<RepoData<T>>({ emitter: FlowableEmitter<RepoData<T>> ->

            //Check for the caches.
            for (it in mCaches) {
                val cacheData = it.read()

                if (cacheData.isError || cacheData.data == null) {
                    //Error occurred.
                    //May be cache is not ready or there is no data in cache?
                    //Caches don't have error data to send. So we don't fire on error here.
                    //Move to the next cache.
                } else {
                    //We hit the cache.
                    //Pass the result.
                    cacheData.source = SourceType.CACHE
                    emitter.onNext(cacheData)

                    //No need the check the next cache
                    break
                }
            }

            //Check for the data refresher.
            val freshData = mRefresher!!.read()
            if (freshData.isError) {
                //Error occurred.
                //May be network is down? OR bad response from the server
                //Send error message and error code.
                emitter.onError(RefreshException(freshData.errorMessage, freshData.errorCode))
            } else {
                //We hit the cache.
                //Pass the result.
                freshData.source = SourceType.NETWORK

                emitter.onNext(freshData)

                //Write the data to all the cache
                mCaches.forEach { it.write(freshData.data) }
            }

            emitter.onComplete()
        }, BackpressureStrategy.LATEST)
        return flowable
    }

}
