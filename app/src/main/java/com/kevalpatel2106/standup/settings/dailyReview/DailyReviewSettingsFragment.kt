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

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.misc.UserSettingsManager
import com.kevalpatel2106.standup.settings.di.DaggerSettingsComponent
import com.kevalpatel2106.standup.settings.findPrefrance
import javax.inject.Inject

class DailyReviewSettingsFragment : PreferenceFragmentCompat() {

    @Inject internal lateinit var settingsManager: UserSettingsManager

    init {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.appComponent)
                .build()
                .inject(this@DailyReviewSettingsFragment)
    }

    private lateinit var model: DailyReviewSettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        model = ViewModelProviders.of(this@DailyReviewSettingsFragment)
                .get(DailyReviewSettingsViewModel::class.java)

        addPreferencesFromResource(R.xml.daily_review_setting)

        val dailyReviewTime = findPrefrance(R.string.pref_key_daily_review_time)
        dailyReviewTime.setOnPreferenceClickListener {
            context?.let { model.displayDateDialog(it) }
            return@setOnPreferenceClickListener true
        }
        model.dailyReviewTimeSummary.observe(this@DailyReviewSettingsFragment, android.arch.lifecycle.Observer {
            dailyReviewTime.summary = it
        })

        val dailyReviewEnableSwitch = findPrefrance(R.string.pref_key_daily_review_enable)
        dailyReviewTime.isEnabled = settingsManager.isDailyReviewEnable
        dailyReviewEnableSwitch.setOnPreferenceChangeListener { _, newValue ->
            dailyReviewTime.isEnabled = newValue as Boolean
            return@setOnPreferenceChangeListener true
        }

    }

    companion object {
        fun getNewInstance(): DailyReviewSettingsFragment {
            return DailyReviewSettingsFragment()
        }
    }
}
