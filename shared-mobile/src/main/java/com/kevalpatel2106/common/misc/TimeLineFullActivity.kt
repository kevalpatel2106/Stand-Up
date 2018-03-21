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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kevalpatel2106.common.R
import com.kevalpatel2106.common.misc.timeline.setUserActivities
import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.standup.timelineview.TimeLineLength
import kotlinx.android.synthetic.main.activity_time_line_full.*

class TimeLineFullActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line_full)

        full_screen_timeline.timelineDuration = TimeLineLength.A_DAY
        full_screen_timeline.enableTouch = true

        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.getSerializableExtra(ARG_DAILY_SUMMARY) == null) {
            throw IllegalArgumentException("ARG_DAILY_SUMMARY cannot be empty.")
        }

        val dailyActivitySummary: DailyActivitySummary = intent.getSerializableExtra(ARG_DAILY_SUMMARY) as DailyActivitySummary
        full_screen_timeline.setUserActivities(dailyActivitySummary.dayActivity)
    }

    companion object {
        private const val ARG_DAILY_SUMMARY = "arg_daily_summary"

        fun launch(context: Context, dailyActivitySummary: DailyActivitySummary) {
            context.startActivity(Intent(context, TimeLineFullActivity::class.java).apply {
                putExtra(ARG_DAILY_SUMMARY, dailyActivitySummary)
            })
        }
    }
}
