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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.misc.UserSettingsManager
import com.kevalpatel2106.standup.settings.findPrefrance
import com.kevalpatel2106.standup.settings.widget.BaseSwitchPreference
import com.kevalpatel2106.standup.settings.widget.BaseTopSwitchPreference
import com.kevalpatel2106.utils.SharedPrefsProvider

/**
 * Created by Keval on 13/01/18.
 * [PreferenceFragmentCompat] class to display Do not disturb mode settings. Do not disturb settings
 * contains enable/disable DND mode, auto DND enable/disable, auto DND duration. It also contains
 * settings to get user sleep timings.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class DndSettingsFragment : PreferenceFragmentCompat() {

    /**
     * [DndSettingsViewModel] view model.
     */
    internal lateinit var model: DndSettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        model = ViewModelProviders.of(this@DndSettingsFragment).get(DndSettingsViewModel::class.java)
        addPreferencesFromResource(R.xml.dnd_settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manualDndSwitch = findPrefrance(R.string.pref_key_dnd_enable) as BaseTopSwitchPreference
        manualDndSwitch.isChecked = UserSettingsManager(SharedPrefsProvider(context!!)).isDndEnable

        setAutoDndSection()
        setSleepHours()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //Display about the quick toggle.
            //Quick toggle were available after Android N.
            findPrefrance(R.string.pref_key_dnd_quick_toggle_hint).isVisible = false
        }
    }

    /**
     * Set up auto dnd settings.
     */
    private fun setAutoDndSection() {
        val autoDndSwitch = findPrefrance(R.string.pref_key_auto_dnd_enable) as BaseSwitchPreference
        val autoDndTime = findPrefrance(R.string.pref_key_auto_dnd_duration)

        model.autoDndTime.observe(this@DndSettingsFragment, Observer {
            autoDndTime.summary = it

            //Change the summary of switch
            if (model.isAutoDndEnable.value!!)
                autoDndSwitch.summary = String.format(getString(R.string.dnd_pref_auto_dnd_switch_summary_on), it)
        })
        model.isAutoDndEnable.observe(this@DndSettingsFragment, Observer {
            it?.let {
                autoDndSwitch.isChecked = it

                //Set the title
                autoDndSwitch.title = if (it) {
                    getString(R.string.dnd_pref_auto_dnd_switch_title_on)
                } else {
                    getString(R.string.dnd_pref_auto_dnd_switch_title_off)
                }

                //Set the summary
                autoDndSwitch.summary = if (it) {
                    String.format(getString(R.string.dnd_pref_auto_dnd_switch_summary_on), model.autoDndTime.value)
                } else {
                    getString(R.string.dnd_pref_auto_dnd_switch_summary_off)
                }

                autoDndTime.isEnabled = it
            }
        })

        autoDndSwitch.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                model.onAutoDndTurnedOn()
            } else {
                model.onAutoDndTurnedOff()
            }
            return@setOnPreferenceChangeListener true
        }

        autoDndTime.setOnPreferenceClickListener {
            model.onSelectAutoDndTime(childFragmentManager)
            return@setOnPreferenceClickListener true
        }
    }

    /**
     * Set the sleep hour section.
     */
    private fun setSleepHours() {
        val sleepTimePref = findPrefrance(R.string.pref_key_dnd_sleep_hour)
        model.sleepTime.observe(this@DndSettingsFragment, Observer {
            sleepTimePref.summary = it
        })

        sleepTimePref.setOnPreferenceClickListener {
            model.onSelectSleepTime(childFragmentManager)
            return@setOnPreferenceClickListener true
        }
    }

    companion object {

        /**
         * Get the new instance of [DndSettingsFragment].  Use this method to get new instance
         * of this fragment instead of creating using constructor.
         */
        fun getNewInstance(): DndSettingsFragment {
            return DndSettingsFragment()
        }
    }
}
