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
                startTimeMillsFrom12Am = ONE_HOUR_MILLS,
                endTimeMillsFrom12Am = 2 * ONE_HOUR_MILLS
        ))
        timelineItems.add(TimeLineItem(
                startTimeMillsFrom12Am = (2.5 * ONE_HOUR_MILLS).toLong(),
                endTimeMillsFrom12Am = 3 * ONE_HOUR_MILLS
        ))
        timelineItems.add(TimeLineItem(
                startTimeMillsFrom12Am = 3 * ONE_HOUR_MILLS,
                endTimeMillsFrom12Am = (3.25 * ONE_HOUR_MILLS).toLong()
        ))
        timelineItems.add(TimeLineItem(
                startTimeMillsFrom12Am = 6 * ONE_HOUR_MILLS,
                endTimeMillsFrom12Am = 8 * ONE_HOUR_MILLS
        ))
        timelineItems.add(TimeLineItem(
                startTimeMillsFrom12Am = 5 * ONE_HOUR_MILLS,
                endTimeMillsFrom12Am = (5.90 * ONE_HOUR_MILLS).toLong()
        ))
        return timelineItems
    }
}