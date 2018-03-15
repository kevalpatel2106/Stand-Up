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

package com.standup.core.reminder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.common.ReminderMessageProvider
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.vibrate
import com.standup.core.R
import com.standup.core.di.DaggerCoreComponent
import kotlinx.android.synthetic.main.activity_pop_up.*
import javax.inject.Inject

class PopUpActivity : BaseActivity() {

    /**
     * [ReminderMessageProvider] to get the reminder message.
     */
    @Inject
    internal lateinit var reminderMessageProvider: ReminderMessageProvider

    @Inject
    internal lateinit var userSettingsManager: UserSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inject dependencies
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@PopUpActivity)

        setContentView(R.layout.activity_pop_up)

        setToolbar(R.id.include, R.string.application_name, false)
//        supportActionBar?.setLogo(com.kevalpatel2106.common.R.drawable.logo)

        //Set text
        reminder_pop_up_message_tv.text = reminderMessageProvider.getReminderMessage()

        //Ok button
        reminder_pop_up_ok_btn.setOnClickListener { finish() }

        if (NotificationSchedulerHelper.shouldVibrate(this@PopUpActivity, userSettingsManager)) {
            vibrate(200)
        }

        if (NotificationSchedulerHelper.shouldPlaySound(this@PopUpActivity, userSettingsManager)) {
            NotificationSchedulerHelper.playSound(this@PopUpActivity,
                    userSettingsManager.getReminderToneUri).subscribe()
        }
    }

    override fun onBackPressed() {
        //No OP
    }
    companion object {

        /**
         * Launch [PopUpActivity].
         */
        fun launch(context: Context) {
            context.startActivity(Intent(context, PopUpActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
