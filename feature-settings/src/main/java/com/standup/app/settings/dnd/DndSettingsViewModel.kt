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

package com.standup.app.settings.dnd

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import android.support.v4.app.FragmentManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.timepicker.DualTimePicker
import com.kevalpatel2106.timepicker.DualTimePickerListener
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.annotations.ViewModel
import com.standup.app.settings.dailyReview.DailyReviewSettingsFragment
import com.standup.app.settings.di.DaggerSettingsComponent
import com.standup.core.Core
import javax.inject.Inject

/**
 * Created by Keval on 15/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(DailyReviewSettingsFragment::class)
internal class DndSettingsViewModel : BaseViewModel {

    @Inject
    lateinit var userSettingsManager: UserSettingsManager

    @Inject
    lateinit var core: Core

    @Suppress("unused")
    constructor() {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@DndSettingsViewModel)
        init()
    }

    @VisibleForTesting
    constructor(userSettingsManager: UserSettingsManager) {
        this.userSettingsManager = userSettingsManager
        init()
    }

    internal val isDndEnable = MutableLiveData<Boolean>()
    internal val isAutoDndEnable = MutableLiveData<Boolean>()
    internal val autoDndTime = MutableLiveData<String>()
    internal val sleepTime = MutableLiveData<String>()

    fun init() {
        isDndEnable.value = userSettingsManager.isCurrentlyDndEnable
        isAutoDndEnable.value = userSettingsManager.isAutoDndEnable
        autoDndTime.value = "${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.autoDndStartTime)} - ${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.autoDndEndTime)}"
        sleepTime.value = "${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.sleepStartTime)} - ${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.sleepEndTime)}"
    }

    /**
     * Call this method when user turns on/off DND mode manually.
     */
    fun onManualDadChanged() {
        //Check iff the DND status
        isDndEnable.value = userSettingsManager.isCurrentlyDndEnable

        //Schedule the dnd monitoring job
        core.refresh()
    }

    /**
     * Call this method whenever user turns on/off the auto dnd or the timing for auto dnd changes.
     * This will refresh the human readable DND hours and refresh [Core] to reschedule with new jobs.
     */
    fun onAutoDndChanged() {
        isAutoDndEnable.value = userSettingsManager.isAutoDndEnable

        //Check iff the DND status
        isDndEnable.value = userSettingsManager.isCurrentlyDndEnable

        //Publish the update
        autoDndTime.value = "${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.autoDndStartTime)} - ${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.autoDndEndTime)}"

        //Schedule the dnd monitoring job
        core.refresh()
    }

    /**
     * Call this method whenever the sleep hours changes. This will refresh the human readable sleep
     * hours and refresh [Core] to reschedule with new jobs.
     */
    fun onSleepTimeChanged() {
        //Publish the update
        sleepTime.value = "${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.sleepStartTime)} - ${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.sleepEndTime)}"

        //Reschedule the sleep monitoring job
        core.refresh()
    }

    /**
     * Call this method to open the [DualTimePicker] for taking input for start and end time of the
     * auto dnd from the user.
     */
    fun onSelectAutoDndTime(supportFragmentManager: FragmentManager) {

        DualTimePicker.show(supportFragmentManager = supportFragmentManager, dualTimePickerListener = object : DualTimePickerListener {
            override fun onTimeSelected(startHourOfDay: Int,
                                        startMinutes: Int,
                                        endHourOfDay: Int,
                                        endMins: Int) {

                //Save the time
                val startTimeMills = TimeUtils.millsFromMidnight(startHourOfDay, startMinutes)
                val endTimeMils = TimeUtils.millsFromMidnight(endHourOfDay, endMins)
                userSettingsManager.setAutoDndTime(startTimeMills, endTimeMils)

                onAutoDndChanged()
            }
        })
    }

    /**
     * Call this method to open the [DualTimePicker] for taking input for start and end time of
     * sleep mode to turn on from the user.
     */
    fun onSelectSleepTime(supportFragmentManager: FragmentManager) {

        DualTimePicker.show(supportFragmentManager = supportFragmentManager, dualTimePickerListener = object : DualTimePickerListener {
            override fun onTimeSelected(startHourOfDay: Int,
                                        startMinutes: Int,
                                        endHourOfDay: Int,
                                        endMins: Int) {

                //Save the time
                val startTimeMills = TimeUtils.millsFromMidnight(startHourOfDay, startMinutes)
                val endTimeMils = TimeUtils.millsFromMidnight(endHourOfDay, endMins)
                userSettingsManager.setSleepTime(startTimeMills, endTimeMils)

                onSleepTimeChanged()
            }
        })
    }
}
