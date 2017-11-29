package com.kevalpatel2106.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Kevalpatel2106 on 29-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class BaseResponse {
    @SerializedName("s")
    @Expose
    lateinit var status: Status

    @SerializedName("d")
    @Expose
    lateinit var d: String

    data class Status(
            @SerializedName("c")
            val statusCode: Int,

            @SerializedName("m")
            val message: String? = null
    )
}