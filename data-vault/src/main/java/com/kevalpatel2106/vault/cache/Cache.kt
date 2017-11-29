package com.kevalpatel2106.vault.cache

import com.kevalpatel2106.vault.VaultData

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface Cache<T> {

    /**
     * Write to the cache.
     */
    fun write(t: T?)

    /**
     * Read from the cache.
     */
    fun read(): VaultData<T>
}
