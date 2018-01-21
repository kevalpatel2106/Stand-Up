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

package com.kevalpatel2106.standup.settings.notifications

import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.notifications.addReminderNotificationChannel
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.core.Core
import com.kevalpatel2106.standup.settings.di.DaggerSettingsComponent
import com.kevalpatel2106.standup.settings.findPrefrance
import com.kevalpatel2106.standup.settings.widget.BaseSwitchPreference
import com.kevalpatel2106.utils.vibrate
import javax.inject.Inject

class NotificationSettingsFragment : PreferenceFragmentCompat() {

    @Inject
    internal lateinit var sessionManager: UserSessionManager
    @Inject
    internal lateinit var settingsManager: UserSettingsManager

    private lateinit var model: NotificationsSettingsViewModel

    init {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@NotificationSettingsFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(this@NotificationSettingsFragment)
                .get(NotificationsSettingsViewModel::class.java)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.notifications_settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set the notification sound.
        val notificationTone = findPrefrance(R.string.pref_key_reminder_notifications_tone)
        model.reminderToneName.observe(this@NotificationSettingsFragment, Observer {
            notificationTone.summary = it
        })
        notificationTone.setOnPreferenceClickListener {
            context?.let { model.openRingtonePicker(it, childFragmentManager) }
            return@setOnPreferenceClickListener true
        }

        //Set the vibrate
        val notificationVibrate = findPrefrance(R.string.pref_key_reminder_notifications_vibrate) as BaseSwitchPreference
        notificationVibrate.isChecked = settingsManager.shouldVibrate
        notificationVibrate.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) context?.vibrate(200)
            return@setOnPreferenceChangeListener true
        }

        //Set the color of LED pulse
        val ledLight = findPrefrance(R.string.pref_key_reminder_notifications_led) as ListPreference
        ledLight.value = settingsManager.ledColorValue
        ledLight.summary = ledLight.entry
        ledLight.setOnPreferenceChangeListener { _, newValue ->
            ledLight.value = newValue as String
            ledLight.summary = ledLight.entry
            return@setOnPreferenceChangeListener false
        }

        //Set the silent mode
        val silentPref = findPrefrance(R.string.pref_key_reminder_notifications_play_in_silent) as ListPreference
        silentPref.value = settingsManager.silentModeRawValue
        silentPref.summary = silentPref.entry
        silentPref.setOnPreferenceChangeListener { _, newValue ->
            silentPref.value = newValue as String
            silentPref.summary = silentPref.entry
            return@setOnPreferenceChangeListener false
        }

        //Test notification
        findPrefrance(R.string.pref_key_reminder_notifications_test).setOnPreferenceClickListener {
            context?.let { Core.fireTestReminder(it) }
            return@setOnPreferenceClickListener true
        }

    }

    override fun onStop() {
        super.onStop()
        //Update the channel settings
        context?.let { updateReminderChannel(it, settingsManager) }
    }

    companion object {
        fun getNewInstance(): NotificationSettingsFragment {
            return NotificationSettingsFragment()
        }
    }

    /**
     * We have to update the notification channel accordingly the change is the application settings.
     * We cannot relay on [NotificationManager] to set the prams while building the notification.
     * [Here is the explanation.](https://stackoverflow.com/q/45081815)
     */
    private fun updateReminderChannel(context: Context, userSettingsManager: UserSettingsManager) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.addReminderNotificationChannel(context, userSettingsManager)
    }
}
