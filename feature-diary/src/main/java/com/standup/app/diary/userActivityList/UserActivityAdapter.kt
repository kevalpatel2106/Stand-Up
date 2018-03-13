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

package com.standup.app.diary.userActivityList

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.kevalpatel2106.common.userActivity.UserActivity

/**
 * Created by Kevalpatel2106 on 22-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class UserActivityAdapter(private val context: Context,
                                   private val userActivities: ArrayList<UserActivity>)
    : RecyclerView.Adapter<UserActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserActivityViewHolder {
        return UserActivityViewHolder.createViewHolder(context, parent)
    }

    override fun getItemCount(): Int = userActivities.size

    override fun onBindViewHolder(holder: UserActivityViewHolder, position: Int) {
        holder.bind(userActivities[position])
    }
}
