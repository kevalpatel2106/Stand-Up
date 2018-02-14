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

package com.standup.app.popup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.common.ReminderMessageProvider
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.standup.R
import com.standup.app.settings.di.DaggerSettingsComponent
import kotlinx.android.synthetic.main.activity_pop_up.*
import javax.inject.Inject

class PopUpActivity : BaseActivity() {

    @Inject
    internal lateinit var reminderMessageProvider: ReminderMessageProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@PopUpActivity)

        setContentView(R.layout.activity_pop_up)


        reminder_pop_up_message_tv.text = reminderMessageProvider.getReminderMessage()
        reminder_pop_up_iv.setImageResource(reminderMessageProvider.getReminderImage())

        reminder_pop_up_ok_btn.setOnClickListener { finish() }
    }

    companion object {

        fun launch(context: Context) {
            context.startActivity(Intent(context, PopUpActivity.javaClass))
        }
    }
}
