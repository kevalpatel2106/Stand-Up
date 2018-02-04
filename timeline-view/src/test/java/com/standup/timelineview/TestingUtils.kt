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

package com.standup.timelineview

import android.graphics.Color

/**
 * Created by Kevalpatel2106 on 05-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object TestingUtils {

    private val ONE_HOUR_MILLS = 3600.times(1000).toLong()

    fun getTestTimelineItems(): ArrayList<TimeLineItem> {
        val timelineItems = ArrayList<TimeLineItem>()
        timelineItems.add(TimeLineItem(
                startTimeMills = ONE_HOUR_MILLS,
                endTimeMills = 2 * ONE_HOUR_MILLS,
                color = Color.GREEN
        ))
        timelineItems.add(TimeLineItem(
                startTimeMills = (2.5 * ONE_HOUR_MILLS).toLong(),
                endTimeMills = 3 * ONE_HOUR_MILLS,
                color = Color.YELLOW
        ))
        timelineItems.add(TimeLineItem(
                startTimeMills = 3 * ONE_HOUR_MILLS,
                endTimeMills = (3.25 * ONE_HOUR_MILLS).toLong(),
                color = Color.BLUE
        ))
        timelineItems.add(TimeLineItem(
                startTimeMills = 6 * ONE_HOUR_MILLS,
                endTimeMills = 8 * ONE_HOUR_MILLS,
                color = Color.GREEN
        ))
        timelineItems.add(TimeLineItem(
                startTimeMills = 5 * ONE_HOUR_MILLS,
                endTimeMills = (5.90 * ONE_HOUR_MILLS).toLong(),
                color = Color.YELLOW
        ))
        return timelineItems
    }
}