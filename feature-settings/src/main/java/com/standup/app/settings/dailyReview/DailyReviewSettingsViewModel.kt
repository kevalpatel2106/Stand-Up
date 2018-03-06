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

package com.standup.app.settings.dailyReview

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v4.app.FragmentManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.TimeUtils
import com.philliphsu.bottomsheetpickers.time.grid.GridTimePickerDialog
import com.standup.app.settings.di.DaggerSettingsComponent
import com.standup.app.settings.setApplicationTheme
import com.standup.core.Core
import java.util.*
import javax.inject.Inject

/**
 * Created by Keval on 14/01/18.
 * View model for [DailyReviewSettingsFragment].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

internal class DailyReviewSettingsViewModel : BaseViewModel {

    /**
     * [UserSettingsManager] for getting the settings.
     */
    @Inject
    lateinit var settingsManager: UserSettingsManager

    @Inject
    lateinit var core: Core

    /**
     * [MutableLiveData] to get the daily review time in HH:mm a format. (e.g. 09:04 AM)
     */
    internal val dailyReviewTimeSummary = MutableLiveData<String>()

    internal val isDailyReviewEnable = MutableLiveData<Boolean>()

    @VisibleForTesting
    constructor(settingsManager: UserSettingsManager) {
        this.settingsManager = settingsManager
        init()
    }

    @Suppress("unused")
    constructor() {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this)
        init()
    }

    /**
     * Initialise view model.
     */
    @SuppressLint("VisibleForTests")
    private fun init() {
        isDailyReviewEnable.value = settingsManager.isDailyReviewEnable
        dailyReviewTimeSummary.value = TimeUtils.convertToHHmmaFrom12Am(settingsManager.dailyReviewTimeFrom12Am)
    }

    fun onDailyReviewSettingChange() {
        isDailyReviewEnable.value = settingsManager.isDailyReviewEnable

        //Update the summary time
        dailyReviewTimeSummary.value = TimeUtils.convertToHHmmaFrom12Am(settingsManager.dailyReviewTimeFrom12Am)

        core.refresh()
    }

    /**
     * Display the time picker dialog. This allows user to select the time for review notifications.
     */
    @SuppressLint("VisibleForTests")
    internal fun displayDateDialog(context: Context, fragmentManager: FragmentManager) {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = settingsManager.dailyReviewTimeFrom12Am

        val dialog = GridTimePickerDialog.newInstance({ _, hourOfDay, minute ->
            //Save new time
            settingsManager.dailyReviewTimeFrom12Am = TimeUtils.millsFromMidnight(hourOfDay, minute)

            //daily review timing changed. Update the alarms.
            onDailyReviewSettingChange()
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false)
        dialog.setApplicationTheme(context)
        dialog.show(fragmentManager, "DailyReviewTimePicker")
    }
}
