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

package com.standup.app.settings.notifications

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v4.app.FragmentManager
import com.kevalpatel.ringtonepicker.RingtonePickerDialog
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.R
import com.standup.app.settings.di.DaggerSettingsComponent
import javax.inject.Inject

/**
 * Created by Keval on 14/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class NotificationsSettingsViewModel : BaseViewModel {

    @Inject
    internal lateinit var sharedPrefsProvider: SharedPrefsProvider
    @Inject
    internal lateinit var userSettingsManager: UserSettingsManager

    internal val reminderToneName = MutableLiveData<String>()

    constructor(sharedPrefsProvider: SharedPrefsProvider) {
        this.sharedPrefsProvider = sharedPrefsProvider
        init()
    }

    constructor() {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@NotificationsSettingsViewModel)
        init()
    }


    fun init() {
        reminderToneName.value = userSettingsManager.getReminderToneName
    }

    fun openRingtonePicker(context: Context, supportFragmentManager: FragmentManager) {

        //Display the ringtone picker dialog.
        RingtonePickerDialog.Builder(context, supportFragmentManager)
                .addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE)
                .addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM)
                .addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION)
                .setTitle(context.getString(R.string.reminder_ringtone_picker_dialog_title))
                .setPlaySampleWhileSelection(true)
                .setPositiveButtonText(android.R.string.ok)
                .setListener { ringtoneName, ringtoneUri ->
                    userSettingsManager.setReminderTone(ringtoneName, ringtoneUri)
                    reminderToneName.value = ringtoneName
                }
                .show()
    }

}
