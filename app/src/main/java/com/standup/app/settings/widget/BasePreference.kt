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

package com.standup.app.settings.widget

import android.content.Context
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import com.standup.R

/**
 * Created by Keval on 07-Mar-17.
 */

class BasePreference : Preference {

    private val mContext: Context

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        mContext = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        mContext = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
    }

    constructor(context: Context) : super(context) {
        mContext = context
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.itemView.background = context.getDrawable(R.drawable.bg_list_item_ripple)
    }
}
