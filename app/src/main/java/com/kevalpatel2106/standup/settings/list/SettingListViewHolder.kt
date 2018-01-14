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

package com.kevalpatel2106.standup.settings.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.base.view.BaseImageView
import com.kevalpatel2106.base.view.BaseTextView
import com.kevalpatel2106.standup.R

/**
 * Created by Keval on 11/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SettingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val iconIv: BaseImageView = itemView.findViewById(R.id.setting_row_icon_iv)
    private val titleTv: BaseTextView = itemView.findViewById(R.id.setting_row_title_tv)
    private val rootView: View = itemView.findViewById(R.id.root_view)

    fun bind(settingsItem: SettingsItem, onClick: (settingsItem: SettingsItem) -> Unit) {
        rootView.isSelected = settingsItem.isSelected

        iconIv.setImageResource(settingsItem.icon)
        titleTv.text = settingsItem.title

        itemView.setOnClickListener {
            onClick.invoke(settingsItem)
        }
    }

    companion object {

        fun createViewHolder(context: Context, parent: ViewGroup?): SettingListViewHolder {
            return SettingListViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.row_settings, parent, false))
        }
    }
}
