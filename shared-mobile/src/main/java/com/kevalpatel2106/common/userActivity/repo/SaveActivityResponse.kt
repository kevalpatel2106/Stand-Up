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

package com.kevalpatel2106.common.userActivity.repo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Keval on 30/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
internal data class SaveActivityResponse(

        @SerializedName("id")
        @Expose
        val id: Long,

        @SerializedName("startTime")
        @Expose
        val startTime: Long,

        @SerializedName("endTime")
        @Expose
        val endTime: Long,

        @SerializedName("type")
        @Expose
        @SupportedActivityType
        val type: Int,

        @SerializedName("uid")
        @Expose
        val userId: Long
) {

    init {
        if (id < 0) throw IllegalArgumentException("id Cannot be less than 0.")
        if (startTime < 0) throw IllegalArgumentException("startTime Cannot be less than 0.")
        if (endTime < 0) throw IllegalArgumentException("endTime Cannot be less than 0.")
        if (type !in 0..1) throw IllegalArgumentException("type Cannot be other than 0 and 1.")
        if (userId < 0) throw IllegalArgumentException("userId Cannot be less than 0.")
    }

    override fun equals(other: Any?): Boolean {
        other?.let { if (other is SaveActivityResponse) return other.id == id }
        return false
    }

    override fun hashCode(): Int = id.hashCode()

}
