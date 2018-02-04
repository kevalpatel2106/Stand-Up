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

package com.standup.app.settings.list

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by Keval on 11/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SettingsListAdapter(private val context: Context,
                          private val data: ArrayList<SettingsItem>,
                          private val clickListener: SettingsClickListener?)
    : RecyclerView.Adapter<SettingListViewHolder>() {

    override fun getItemCount(): Int = data.size

    @SuppressLint("VisibleForTests")
    override fun onBindViewHolder(holder: SettingListViewHolder, position: Int) = holder.bind(getItem(position), {
        clickListener?.onItemClick(it)
    })

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SettingListViewHolder = SettingListViewHolder.createViewHolder(context, parent)

    @VisibleForTesting
    internal fun getItem(position: Int): SettingsItem = data[position]
}