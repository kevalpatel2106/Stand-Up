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
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceViewHolder
import android.support.v7.preference.SwitchPreferenceCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import com.kevalpatel2106.utils.ViewUtils
import com.standup.app.settings.R


/**
 * Created by Keval on 07-Mar-17.
 */

class BaseTopSwitchPreference : SwitchPreferenceCompat {
    private val mContext: Context

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        mContext = context
        title = "Enable"
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        mContext = context
        title = "Enable"
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        title = "Enable"
    }

    constructor(context: Context) : super(context) {
        mContext = context
        title = "Enable"
    }


    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bg_color_top_pref_switch))
        holder.itemView.setPadding(ViewUtils.toPx(context, 50),
                holder.itemView.paddingTop,
                holder.itemView.paddingEnd,
                holder.itemView.paddingBottom)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            holder.itemView.elevation = ViewUtils.toPx(context, 6).toFloat()

        //Set the title
        val titleView = holder.findViewById(android.R.id.title) as TextView
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.toFloat())
        titleView.text = if (isChecked) "On" else "Off"

        holder.isDividerAllowedAbove = false
        holder.isDividerAllowedBelow = false
    }
}
