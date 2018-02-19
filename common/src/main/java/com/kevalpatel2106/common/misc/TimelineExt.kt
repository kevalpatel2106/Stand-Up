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

package com.kevalpatel2106.common.misc

import com.kevalpatel2106.common.AppConfig
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.standup.timelineview.TimeLineData
import com.standup.timelineview.TimeLineItem
import com.standup.timelineview.TimeLineView

/**
 * Created by Keval on 16/02/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
fun TimeLineView.setUserActivities(userActivity: ArrayList<UserActivity>) {
    val standingItems = ArrayList<TimeLineItem>()
    val sittingItems = ArrayList<TimeLineItem>()
    userActivity.forEach {

        val timelineItem = TimeLineItem.create(
                startTimeUnixMills = it.eventStartTimeMills,
                endTimeUnixMills = it.eventEndTimeMills
        )
        when (it.userActivityType) {
            UserActivityType.MOVING -> {
                standingItems.add(timelineItem)
            }
            UserActivityType.SITTING -> {
                sittingItems.add(timelineItem)
            }
            else -> {
                //NO OP
            }
        }
    }

    val timeLineData = ArrayList<TimeLineData>(2)
    timeLineData.add(TimeLineData(
            AppConfig.COLOR_STANDING,
            AppConfig.STANDING_HEIGHT,
            standingItems
    ))
    timeLineData.add(TimeLineData(
            AppConfig.COLOR_SITTING,
            AppConfig.SITTING_HEIGHT,
            sittingItems
    ))

    this.timelineData = timeLineData
}
