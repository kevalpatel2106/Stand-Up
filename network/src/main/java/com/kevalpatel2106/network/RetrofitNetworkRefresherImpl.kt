package com.kevalpatel2106.network

import com.kevalpatel2106.vault.refresher.RetrofitNetworkRefresher
import retrofit2.Call
import retrofit2.Response

/**
 * Created by Kevalpatel2106 on 29-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class RetrofitNetworkRefresherImpl<T>(call: Call<T>) : RetrofitNetworkRefresher<T>(call) {
    override fun parseErrorBody(errorCode: Int, response: Response<T>): String {
        return response.message() ?: NetworkConfig.ERROR_MESSAGE_SOMETHING_WRONG
    }
}