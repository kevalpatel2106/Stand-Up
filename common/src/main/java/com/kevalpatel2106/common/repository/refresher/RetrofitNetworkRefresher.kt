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

package com.kevalpatel2106.common.repository.refresher

import com.kevalpatel2106.common.repository.RepoData
import com.kevalpatel2106.common.repository.SourceType
import com.kevalpatel2106.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class RetrofitNetworkRefresher<out T>(private val call: Call<T>) : Refresher<T> {

    /**
     * Read from the cache. This is the internal method not part of the public api.
     */
    override fun read(): RepoData<T> {
        val response = call.execute()

        return if (response.isSuccessful && response.body() != null) {

            //Prepare the success data
            val vaultData = RepoData(false, response.body())
            vaultData.source = SourceType.NETWORK
            vaultData
        } else {

            //Prepare the error data
            val vaultData = RepoData<T>(true)
            vaultData.errorMessage = parseErrorBody(response.code(), response)
            vaultData.source = SourceType.NETWORK
            vaultData
        }
    }

    private fun parseErrorBody(errorCode: Int, response: Response<T>): String {
        return response.message() ?: NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG
    }
}
