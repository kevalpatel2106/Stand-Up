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

package com.kevalpatel2106.standup.settings.dnd

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import android.support.v4.app.FragmentManager
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.misc.UserSettingsManager
import com.kevalpatel2106.standup.settings.di.DaggerSettingsComponent
import com.kevalpatel2106.timepicker.DualTimePicker
import com.kevalpatel2106.timepicker.DualTimePickerListener
import com.kevalpatel2106.utils.TimeUtils
import javax.inject.Inject

/**
 * Created by Keval on 15/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class DndSettingsViewModel : BaseViewModel {

    @Inject lateinit var userSettingsManager: UserSettingsManager

    @Suppress("unused")
    constructor() {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.appComponent)
                .build()
                .inject(this@DndSettingsViewModel)
        init()
    }

    @VisibleForTesting
    constructor(userSettingsManager: UserSettingsManager) {
        this.userSettingsManager = userSettingsManager
        init()
    }

    internal val autoDndTime = MutableLiveData<String>()
    internal val sleepTime = MutableLiveData<String>()
    internal val isAutoDndEnable = MutableLiveData<Boolean>()

    fun init() {
        isAutoDndEnable.value = userSettingsManager.isAutoDndEnable
        autoDndTime.value = "${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.autoDndStartTime)} - ${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.autoDndEndTime)}"
        sleepTime.value = "${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.sleepStartTime)} - ${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.sleepEndTime)}"
    }

    fun onAutoDndTurnedOn() {
        isAutoDndEnable.value = true
    }

    fun onAutoDndTurnedOff() {
        isAutoDndEnable.value = false
    }

    fun onSelectAutoDndTime(supportFragmentManager: FragmentManager) {
        DualTimePicker.show(supportFragmentManager = supportFragmentManager, dualTimePickerListener = object : DualTimePickerListener {
            override fun onTimeSelected(startHourOfDay: Int,
                                        startMinutes: Int,
                                        endHourOfDay: Int,
                                        endMins: Int) {

                //Save the time
                val startTimeMills = TimeUtils.getMilliSecFrom12AM(startHourOfDay, startMinutes)
                val endTimeMils = TimeUtils.getMilliSecFrom12AM(endHourOfDay, endMins)
                userSettingsManager.setAutoDndTime(startTimeMills, endTimeMils)

                //Publish the update
                autoDndTime.value = "${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.autoDndStartTime)} - ${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.autoDndEndTime)}"
            }
        })
    }

    fun onSelectSleepTime(supportFragmentManager: FragmentManager) {
        DualTimePicker.show(supportFragmentManager = supportFragmentManager, dualTimePickerListener = object : DualTimePickerListener {
            override fun onTimeSelected(startHourOfDay: Int,
                                        startMinutes: Int,
                                        endHourOfDay: Int,
                                        endMins: Int) {

                //Save the time
                val startTimeMills = TimeUtils.getMilliSecFrom12AM(startHourOfDay, startMinutes)
                val endTimeMils = TimeUtils.getMilliSecFrom12AM(endHourOfDay, endMins)
                userSettingsManager.setSleepime(startTimeMills, endTimeMils)

                //Publish the update
                sleepTime.value = "${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.sleepStartTime)} - ${TimeUtils.convertToHHmmaFrom12Am(userSettingsManager.sleepEndTime)}"
            }
        })
    }
}