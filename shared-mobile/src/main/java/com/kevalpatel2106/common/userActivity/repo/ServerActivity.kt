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
import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.kevalpatel2106.utils.TimeUtils

/**
 * Created by Kevalpatel2106 on 13-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal data class ServerActivity(

        @Expose
        @SerializedName("id")
        var remoteId: Long,

        @Expose
        @SerializedName("startTime")
        var eventStartTimeNano: Long,

        @Expose
        @SerializedName("endTime")
        var eventEndTimeNano: Long,

        @Expose
        @SerializedName("type")
        val type: Int
) {

    fun getUserActivity(): UserActivity {
        return UserActivity(
                remoteId = remoteId,
                isSynced = true,
                eventEndTimeMills = TimeUtils.convertToMilli(eventEndTimeNano),
                eventStartTimeMills = TimeUtils.convertToMilli(eventStartTimeNano),
                type = when (type) {
                    0 -> UserActivityType.SITTING.name.toLowerCase()
                    1 -> UserActivityType.MOVING.name.toLowerCase()
                    else -> UserActivityType.NOT_TRACKED.name.toLowerCase()
                }
        )

    }

}
