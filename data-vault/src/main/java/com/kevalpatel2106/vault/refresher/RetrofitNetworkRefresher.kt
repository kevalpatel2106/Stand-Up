package com.kevalpatel2106.vault.refresher

import com.kevalpatel2106.vault.SourceType
import com.kevalpatel2106.vault.VaultData
import retrofit2.Call
import retrofit2.Response

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
abstract class RetrofitNetworkRefresher<T>(private val call: Call<T>) : Refresher<T> {

    /**
     * Read from the cache. This is the internal method not part of the public api.
     */
    override fun read(): VaultData<T> {
        val response = call.execute()

        return if (response.isSuccessful && response.body() != null) {

            //Prepare the success data
            val vaultData = VaultData(false, response.body())
            vaultData.source = SourceType.NETWORK
            vaultData
        } else {

            //Prepare the error data
            val vaultData = VaultData<T>(true)
            vaultData.errorMessage = parseErrorBody(response.code(), response)
            vaultData.source = SourceType.NETWORK
            vaultData
        }
    }

    abstract fun parseErrorBody(errorCode: Int, response: Response<T>): String
}