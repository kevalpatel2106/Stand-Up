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

package com.kevalpatel2106.standup.reminder.demo

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import android.widget.TextView
import com.google.android.gms.location.DetectedActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.reminder.scheduler.ReminderScheduler

class DemoRecognitionActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var adapter: DetectedActivitiesAdapter
    private val detectedActivities = ArrayList<DetectedActivity>()

    companion object {

        @JvmField
        internal val PREF_KEY = "key_detected_activities"

        /**
         * List of DetectedActivity types that we monitor in this sample.
         */
        @JvmField
        internal val MONITORED_ACTIVITIES = intArrayOf(
                DetectedActivity.STILL,
                DetectedActivity.ON_FOOT,
                DetectedActivity.WALKING,
                DetectedActivity.RUNNING,
                DetectedActivity.ON_BICYCLE,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.TILTING,
                DetectedActivity.UNKNOWN
        )

        /**
         * Returns a human readable String corresponding to a detected activity type.
         */
        @JvmStatic
        fun getActivityString(detectedActivityType: Int): String {
            return when (detectedActivityType) {
                DetectedActivity.IN_VEHICLE -> "In vehicle"
                DetectedActivity.ON_BICYCLE -> "On bicycle"
                DetectedActivity.ON_FOOT -> "On foot"
                DetectedActivity.RUNNING -> "Running"
                DetectedActivity.STILL -> "Still"
                DetectedActivity.TILTING -> "Tilting"
                DetectedActivity.UNKNOWN -> "Unknown"
                DetectedActivity.WALKING -> "Walking"
                else -> "Other"
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_recognise)

        findViewById<TextView>(R.id.activity_detector_status_tv).setOnClickListener({
            if (ReminderScheduler.isActivityDetectionEnabled()) {
                ReminderScheduler.shutDown(this@DemoRecognitionActivity)
            } else {
                ReminderScheduler.startSchedulerIfNotRunning(this@DemoRecognitionActivity)
            }
            setStatus()
        })

        //Add initialize
        MONITORED_ACTIVITIES.mapTo(detectedActivities) { DetectedActivity(it, 0) }

        // Bind the adapter to the ListView responsible for display data for detected activities.
        val detectedActivitiesListView = findViewById<ListView>(R.id.detected_activities_listview)
        adapter = DetectedActivitiesAdapter(this, detectedActivities)
        detectedActivitiesListView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this)
        setStatus()
    }

    override fun onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    @SuppressLint("WrongViewCast")
    private fun setStatus() {
        findViewById<TextView>(R.id.activity_detector_status_tv).text =
                if (ReminderScheduler.isActivityDetectionEnabled())
                    "Detection is enabled."
                else
                    "Detection is disabled."
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        if (s == PREF_KEY) {
            val detectedActivities = detectedActivitiesFromJson(
                    PreferenceManager.getDefaultSharedPreferences(this@DemoRecognitionActivity)
                            .getString(PREF_KEY, ""))

            adapter.updateActivities(detectedActivities)
        }
    }

    private fun detectedActivitiesFromJson(jsonArray: String): java.util.ArrayList<DetectedActivity> {
        val listType = object : TypeToken<java.util.ArrayList<DetectedActivity>>() {

        }.type
        var detectedActivities: java.util.ArrayList<DetectedActivity>? = Gson().fromJson<java.util.ArrayList<DetectedActivity>>(jsonArray, listType)
        if (detectedActivities == null) {
            detectedActivities = java.util.ArrayList()
        }
        return detectedActivities
    }
}
