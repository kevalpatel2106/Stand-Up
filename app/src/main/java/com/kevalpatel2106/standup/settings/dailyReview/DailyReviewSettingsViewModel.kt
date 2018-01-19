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

package com.kevalpatel2106.standup.settings.dailyReview

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.UserSettingsManager
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.core.dailyReview.DailyReviewHelper
import com.kevalpatel2106.standup.settings.di.DaggerSettingsComponent
import com.kevalpatel2106.utils.TimeUtils
import java.util.*
import javax.inject.Inject

/**
 * Created by Keval on 14/01/18.
 * View model for [DailyReviewSettingsFragment].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

class DailyReviewSettingsViewModel : BaseViewModel {

    /**
     * [UserSettingsManager] for getting the settings.
     */
    @Inject lateinit var settingsManager: UserSettingsManager

    /**
     * [MutableLiveData] to get the daily review time in HH:mm a format. (e.g. 09:04 AM)
     */
    internal val dailyReviewTimeSummary = MutableLiveData<String>()

    @VisibleForTesting
    constructor(settingsManager: UserSettingsManager) {
        this.settingsManager = settingsManager
        init()
    }

    @Suppress("unused")
    constructor() {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.appComponent)
                .build()
                .inject(this)
        init()
    }

    /**
     * Initialise view model.
     */
    @SuppressLint("VisibleForTests")
    private fun init() {
        dailyReviewTimeSummary.value = TimeUtils.convertToHHmmaFrom12Am(settingsManager.dailyReviewTimeFrom12Am)
    }

    internal fun onDailyReviewTurnedOff(context: Context) {
        //Cancel the upcoming alarm, if any.
        DailyReviewHelper.cancelAlarm(context)
    }

    internal fun onDailyReviewTurnedOn(context: Context) = rescheduleDailyReviewAlarm(context)

    private fun rescheduleDailyReviewAlarm(context: Context) {
        //Cancel the upcoming alarm, if any.
        DailyReviewHelper.cancelAlarm(context)

        //Register new alarm
        DailyReviewHelper.registerDailyReview(context, settingsManager)
    }

    /**
     * Display the time picker dialog. This allows user to select the time for review notifications.
     */
    @SuppressLint("VisibleForTests")
    internal fun displayDateDialog(context: Context) {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = settingsManager.dailyReviewTimeFrom12Am

        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hours, mins ->

            //Save new time
            settingsManager.dailyReviewTimeFrom12Am = TimeUtils.getMilliSecFrom12AM(hours, mins)

            //Update the summary time
            dailyReviewTimeSummary.value = TimeUtils.convertToHHmmaFrom12Am(settingsManager.dailyReviewTimeFrom12Am)

            //daily review timing changed. Update the alarms.
            rescheduleDailyReviewAlarm(context)
        }, cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false)
                .show()
    }
}
