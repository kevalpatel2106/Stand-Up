package com.kevalpatel2106.standup.timelineview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        //Timeline view
        val timeline = findViewById<TimeLineView>(R.id.timeline_view_demo)

    }
}
