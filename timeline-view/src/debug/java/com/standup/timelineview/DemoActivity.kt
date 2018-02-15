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
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        //Timeline view
        val timeline = findViewById<TimeLineView>(R.id.timeline_view_demo)
        timeline.timelineDuration = TimeLineLength.A_DAY
        timeline.blockIndicatorColor = Color.WHITE

        val timelineItems = ArrayList<TimeLineItem>()
        timelineItems.add(TimeLineItem(
                startTimeMillsFrom12Am = 3600_000,
                endTimeMillsFrom12Am = 2 * 3600_000
        ))
        timelineItems.add(TimeLineItem(
                startTimeMillsFrom12Am = (2.5 * 3600_000).toLong(),
                endTimeMillsFrom12Am = 3 * 3600_000
        ))
        timelineItems.add(TimeLineItem(
                startTimeMillsFrom12Am = 3 * 3600_000,
                endTimeMillsFrom12Am = (3.25 * 3600_000).toLong()
        ))
        timelineItems.add(TimeLineItem(
                startTimeMillsFrom12Am = 6 * 3600_000,
                endTimeMillsFrom12Am = 8 * 3600_000
        ))
        timelineItems.add(TimeLineItem(
                startTimeMillsFrom12Am = 5 * 3600_000,
                endTimeMillsFrom12Am = (5.90 * 3600_000).toLong()
        ))
        timelineItems.add(TimeLineItem(
                startTimeMillsFrom12Am = 12 * 3600_000,
                endTimeMillsFrom12Am = 23 * 3600_000
        ))
        timeline.timelineItems = timelineItems
    }
}
