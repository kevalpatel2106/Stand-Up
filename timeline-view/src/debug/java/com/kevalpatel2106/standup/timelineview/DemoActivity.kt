/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.timelineview

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        //Timeline view
        val timeline = findViewById<TimeLineView>(R.id.timeline_view_demo)
        timeline.timelineDuration = TimeLineLength.A_DAY

        val timelineItems = ArrayList<TimeLineItem>()
        timelineItems.add(TimeLineItem(
                startTime = 3600,
                endTime = 2 * 3600,
                color = Color.GREEN
        ))
        timelineItems.add(TimeLineItem(
                startTime = (2.5 * 3600).toLong(),
                endTime = 3 * 3600,
                color = Color.YELLOW
        ))
        timelineItems.add(TimeLineItem(
                startTime = 3 * 3600,
                endTime = (3.25 * 3600).toLong(),
                color = Color.BLUE
        ))
        timelineItems.add(TimeLineItem(
                startTime = 6 * 3600,
                endTime = 8 * 3600,
                color = Color.GREEN
        ))
        timelineItems.add(TimeLineItem(
                startTime = 5 * 3600,
                endTime = (5.90 * 3600).toLong(),
                color = Color.YELLOW
        ))
        timelineItems.add(TimeLineItem(
                startTime = 12 * 3600,
                endTime = 23 * 3600,
                color = Color.GREEN
        ))
        timeline.timelineItems = timelineItems
    }
}
