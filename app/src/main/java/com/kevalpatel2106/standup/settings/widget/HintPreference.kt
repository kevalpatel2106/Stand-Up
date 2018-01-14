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

package com.kevalpatel2106.standup.settings.widget

import android.content.Context
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import android.widget.TextView

import com.kevalpatel2106.standup.R

/**
 * Created by Keval Patel on 08/03/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

class HintPreference : Preference {

    constructor(context: Context) : super(context, null) {
        super.setLayoutResource(R.layout.layout_hint_prefrance)
        isEnabled = false
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        super.setLayoutResource(R.layout.layout_hint_prefrance)
        isEnabled = false
    }

    override fun setLayoutResource(layoutResId: Int) {
        super.setLayoutResource(R.layout.layout_hint_prefrance)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.isDividerAllowedAbove = false
        holder.isDividerAllowedBelow = true

        val hintTv = holder.findViewById(R.id.hint_pref_tv) as TextView
        hintTv.text = summary

        holder.isDividerAllowedBelow = false
        holder.isDividerAllowedAbove = false
    }
}
