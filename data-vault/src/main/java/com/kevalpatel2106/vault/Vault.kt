package com.kevalpatel2106.vault

import com.kevalpatel2106.vault.cache.Cache
import com.kevalpatel2106.vault.refresher.RefreshException
import com.kevalpatel2106.vault.refresher.Refresher
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter

/**
 * Created by Keval on 28/11/17.
 * Class that exposes public api of the module.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class Vault<T> internal constructor() {
    internal val mCaches: ArrayList<Cache<T>> = ArrayList()
    internal val mRefresher: ArrayList<Refresher<T>> = ArrayList()

    fun fetch(): Flowable<VaultData<T>> {

        @Suppress("LoopToCallChain", "UnnecessaryVariable")
        val flowable = Flowable.create<VaultData<T>>({ emitter: FlowableEmitter<VaultData<T>> ->

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
            for (it in mRefresher) {
                val freshData = it.read()

                if (freshData.isError) {
                    //Error occurred.
                    //May be network is down? OR bad response from the server
                    //Send error message and error code.
                    emitter.onError(RefreshException(freshData.errorMessage, freshData.errorCode))
                } else {
                    //We hit the cache.
                    //Pass the result.
                    freshData.source = SourceType.CACHE
                    emitter.onNext(freshData)

                    //Write the data to all the cache
                    mCaches.forEach {
                        it.write(freshData.data)
                    }

                    //No need the check the next refresher
                    break
                }
            }

            emitter.onComplete()
        }, BackpressureStrategy.LATEST)
        return flowable
    }

}