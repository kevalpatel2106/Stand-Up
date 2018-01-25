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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.view.View
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.settings.di.DaggerSettingsComponent
import com.kevalpatel2106.standup.settings.findPrefrance
import com.kevalpatel2106.standup.settings.widget.BaseTopSwitchPreference
import javax.inject.Inject

/**
 * Created by Keval on 13/01/18.
 * [PreferenceFragmentCompat] class to display user's daily review settings. Daily review settings
 * contain enable/disable switch and a preference to set the time for daily review notification.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class DailyReviewSettingsFragment : PreferenceFragmentCompat() {

    /**
     * [UserSettingsManager] for getting the settings.
     */
    @Inject
    internal lateinit var settingsManager: UserSettingsManager

    /**
     * [SharedPreferences.OnSharedPreferenceChangeListener] to get callbacks whenever particular
     * preference for this fragment changes.
     *
     * This monitors preferences with below keys:
     * - [R.string.pref_key_daily_review_enable]
     */
    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            getString(R.string.pref_key_daily_review_enable) -> model.onDailyReviewSettingChange()
        }
    }

    init {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@DailyReviewSettingsFragment)
    }

    /**
     * [DailyReviewSettingsViewModel] view model.
     */
    private lateinit var model: DailyReviewSettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        model = ViewModelProviders.of(this@DailyReviewSettingsFragment)
                .get(DailyReviewSettingsViewModel::class.java)

        addPreferencesFromResource(R.xml.daily_review_setting)

        PreferenceManager.getDefaultSharedPreferences(context)
                .registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(context)
                .unregisterOnSharedPreferenceChangeListener(prefChangeListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Review notification time
        val dailyReviewTime = findPrefrance(R.string.pref_key_daily_review_time)
        dailyReviewTime.setOnPreferenceClickListener {
            context?.let { model.displayDateDialog(it, childFragmentManager) }
            return@setOnPreferenceClickListener true
        }
        model.dailyReviewTimeSummary.observe(this@DailyReviewSettingsFragment, android.arch.lifecycle.Observer {
            dailyReviewTime.summary = it
        })

        //Enable/Disable switch
        val dailyReviewEnableSwitch = findPrefrance(R.string.pref_key_daily_review_enable) as BaseTopSwitchPreference
        model.isDailyReviewEnable.observe(this@DailyReviewSettingsFragment, Observer {
            it?.let {
                dailyReviewEnableSwitch.isChecked = it
                dailyReviewTime.isEnabled = it
            }
        })

    }

    companion object {

        /**
         * Get the new instance of [DailyReviewSettingsFragment]. Use this method to get new instance
         * of this fragment instead of creating using constructor.
         */
        fun getNewInstance(): DailyReviewSettingsFragment {
            return DailyReviewSettingsFragment()
        }
    }
}
