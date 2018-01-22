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

package com.kevalpatel2106.standup.diary.userActivityList

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.standup.R

/**
 * Created by Kevalpatel2106 on 22-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class UserActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(userActivity: UserActivity) {

    }

    companion object {

        fun createViewHolder(context: Context, parent: ViewGroup?): UserActivityViewHolder {
            return UserActivityViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.row_user_activity, parent, false))
        }
    }
}
