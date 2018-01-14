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

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.kevalpatel2106.standup.R

class DailyReviewSettingsFragment : PreferenceFragmentCompat() {

    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.daily_review_setting)
    }

    companion object {
        fun getNewInstance(): DailyReviewSettingsFragment {
            return DailyReviewSettingsFragment()
        }
    }
}
