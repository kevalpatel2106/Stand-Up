package com.kevalpatel2106.vault

import com.kevalpatel2106.vault.cache.Cache
import com.kevalpatel2106.vault.refresher.Refresher

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class VaultBuilder<T> {

    private val vault: Vault<T> = Vault()

    fun addCache(cache: Cache<T>): VaultBuilder<T> {
        vault.mCaches.add(cache)
        return this
    }

    fun addRefresher(refresher: Refresher<T>): VaultBuilder<T> {
        vault.mRefresher.add(refresher)
        return this
    }

    fun build(): Vault<T> {
        if (vault.mRefresher.isEmpty() && vault.mCaches.isEmpty())
            throw IllegalStateException("No cache or refresher added.")
        return vault
    }
}