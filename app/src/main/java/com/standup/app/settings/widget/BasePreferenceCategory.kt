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
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceCategory
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import com.standup.R


/**
 * Created by Keval Patel on 19/03/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

class BasePreferenceCategory : PreferenceCategory {
    private val mContext: Context

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        mContext = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        mContext = context
    }

    constructor(context: Context, attrs: AttributeSet)
            : super(context, attrs) {
        mContext = context
    }

    constructor(context: Context) : super(context) {
        mContext = context
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        holder.itemView.setPadding(holder.itemView.paddingLeft, 20, holder.itemView.paddingRight, 10)

        val titleView = holder.findViewById(android.R.id.title) as TextView
        titleView.setAllCaps(false)
        titleView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
    }
}
