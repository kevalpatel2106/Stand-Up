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

package com.standup.core.repo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.utils.TimeUtils

/**
 * Created by Keval on 30/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@Suppress("MemberVisibilityCanBePrivate")
internal data class SaveActivityRequest(

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
        val type: Int
) {

    init {
        if (id < 0) throw IllegalArgumentException("id Cannot be less than 0.")
        if (startTime < 0) throw IllegalArgumentException("startTime Cannot be less than 0.")
        if (endTime < 0) throw IllegalArgumentException("endTime Cannot be less than 0.")
        if (type !in 0..1) throw IllegalArgumentException("type Cannot be other than 0 and 1.")
    }


    constructor(userActivity: UserActivity) : this(id = userActivity.remoteId,
            startTime = TimeUtils.convertToNano(userActivity.eventStartTimeMills),
            endTime = TimeUtils.convertToNano(userActivity.eventEndTimeMills),
            type = when (userActivity.userActivityType) {
                UserActivityType.SITTING -> 0
                UserActivityType.MOVING -> 1
                else -> throw IllegalStateException("Unsupported user activity type.")
            })
}
