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
        timeline.timelineLength = TimeLineLength.A_DAY

        val timelineItems = ArrayList<TimeLineItem>()
        timelineItems.add(TimeLineItem(
                startTime = 3600,
                endTime = 2 * 3600,
                color = Color.BLACK
        ))
        timelineItems.add(TimeLineItem(
                startTime = (2.5 * 3600).toLong(),
                endTime = 3 * 3600,
                color = Color.CYAN
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
                color = Color.RED
        ))
        timelineItems.add(TimeLineItem(
                startTime = 12 * 3600,
                endTime = 23 * 3600,
                color = Color.MAGENTA
        ))
        timeline.setTimeLineItems(timelineItems)
    }
}
