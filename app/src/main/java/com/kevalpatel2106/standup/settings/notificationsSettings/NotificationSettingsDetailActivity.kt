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

package com.kevalpatel2106.standup.settings.notificationsSettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.settings.BaseSettingsDetailActivity

/**
 * Created by Keval on 13/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class NotificationSettingsDetailActivity : BaseSettingsDetailActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = getString(R.string.title_activity_notification_settings)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, NotifficationSettingsFragment.getNewInstance())
                .commit()
    }

    companion object {

        fun launch(context: Context) {
            context.startActivity(Intent(context, NotificationSettingsDetailActivity::class.java))
        }
    }
}