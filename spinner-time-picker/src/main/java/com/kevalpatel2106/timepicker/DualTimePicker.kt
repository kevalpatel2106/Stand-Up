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

package com.kevalpatel2106.timepicker

import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_dualtime_picker.*
import java.util.*

/**
 * Created by Keval on 15/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class DualTimePicker : DialogFragment() {

    companion object {
        private val ARG_LISTENER = "listener"
        private val ARG_START_HOUR_OF_DAY = "LISTENER"
        private val ARG_START_MINUTE = "arg_start_minute"
        private val ARG_END_HOUR_OF_DAY = "arg_end_hour_of_day"
        private val ARG_END_MINUTE = "arg_end_minute"

        fun show(supportFragmentManager: FragmentManager,
                 startHourOfDay: Int = -1,
                 startMinute: Int = -1,
                 endHourOfDay: Int = -1,
                 endMinute: Int = -1,
                 dualTimePickerListener: DualTimePickerListener) {
            DualTimePicker().apply {
                arguments = Bundle().apply {
                    putInt(ARG_START_HOUR_OF_DAY, startHourOfDay)
                    putInt(ARG_END_HOUR_OF_DAY, endHourOfDay)
                    putInt(ARG_START_MINUTE, startMinute)
                    putInt(ARG_END_MINUTE, endMinute)
                    putSerializable(ARG_LISTENER, dualTimePickerListener)
                }
            }.show(supportFragmentManager, "DualTimePicker")
        }
    }

    private lateinit var listener: DualTimePickerListener
    private var startHourOfDay: Int = 0
    private var startMinute: Int = 0
    private var endHourOfDay: Int = 0
    private var endMinute: Int = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        arguments?.let {
            listener = it.getSerializable(ARG_LISTENER) as DualTimePickerListener

            startHourOfDay = getValidStartHour(it)
            startMinute = getValidStartMinute(it)

            endHourOfDay = getValidEndHour(it)
            endMinute = getValidEndMinute(it)
        }
        return inflater.inflate(R.layout.dialog_dualtime_picker, container, false)
    }

    override fun onStart() {
        super.onStart()
        val params = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams

    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set start time picker
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            start_time_picker.hour = startHourOfDay
            start_time_picker.minute = startMinute
        } else {
            start_time_picker.currentHour = startHourOfDay
            start_time_picker.currentMinute = startMinute
        }
        start_time_picker.setIs24HourView(false)
        start_time_picker.setOnTimeChangedListener { _, hour, minute ->
            startHourOfDay = hour
            startMinute = minute
        }

        //Set end time picker
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            end_time_picker.hour = endHourOfDay
            end_time_picker.minute = endMinute
        } else {
            end_time_picker.currentHour = endHourOfDay
            end_time_picker.currentMinute = endMinute
        }
        end_time_picker.setIs24HourView(false)
        end_time_picker.setOnTimeChangedListener { _, hour, minute ->
            endHourOfDay = hour
            endMinute = minute
        }

        //set ok button
        btn_ok.setOnClickListener {
            listener.onTimeSelected(startHourOfDay, startMinute, endHourOfDay, endMinute)
            dismiss()
        }

        //Set cancel button
        btn_cancel.setOnClickListener { dismiss() }
    }

    internal fun getValidStartHour(argument: Bundle): Int {
        return if (argument.containsKey(ARG_START_HOUR_OF_DAY) && argument.getInt(ARG_START_HOUR_OF_DAY) in 0..23)
            argument.getInt(ARG_START_HOUR_OF_DAY)
        else
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }

    internal fun getValidStartMinute(argument: Bundle): Int {
        return if (argument.containsKey(ARG_START_MINUTE) && argument.getInt(ARG_START_MINUTE) in 0..59)
            argument.getInt(ARG_START_MINUTE)
        else
            Calendar.getInstance().get(Calendar.MINUTE)
    }

    internal fun getValidEndHour(argument: Bundle): Int {
        return if (argument.containsKey(ARG_END_HOUR_OF_DAY) && argument.getInt(ARG_END_HOUR_OF_DAY) in 0..23)
            argument.getInt(ARG_START_HOUR_OF_DAY)
        else {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, startHourOfDay)
            cal.add(Calendar.HOUR_OF_DAY, 1)
            cal.get(Calendar.HOUR_OF_DAY)
        }
    }

    internal fun getValidEndMinute(argument: Bundle): Int {
        return if (argument.containsKey(ARG_END_MINUTE) && argument.getInt(ARG_END_MINUTE) in 0..59)
            argument.getInt(ARG_END_MINUTE)
        else
            Calendar.getInstance().get(Calendar.MINUTE)
    }
}