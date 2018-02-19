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

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.db.userActivity.UserActivityType
import com.kevalpatel2106.utils.TimeUtils
import com.standup.app.diary.R
import kotlinx.android.synthetic.main.row_user_activity.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Kevalpatel2106 on 22-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class UserActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())

    @SuppressLint("VisibleForTests")
    fun bind(userActivity: UserActivity) {
        itemView.row_user_activity_icon.setImageResource(when (userActivity.userActivityType) {
            UserActivityType.SITTING -> R.drawable.ic_sitting
            UserActivityType.MOVING -> R.drawable.ic_standing
            UserActivityType.NOT_TRACKED -> R.drawable.ic_standing //TODO Change the icon
        })

        itemView.row_user_activity_name_tv.text = String.format("%s %s.",
                getActivityText(userActivity.userActivityType),
                getDuration(userActivity.eventEndTimeMills, userActivity.eventStartTimeMills))
        itemView.row_user_activity_time_tv.text = sdf.format(Date(userActivity.eventStartTimeMills))
    }

    companion object {

        @VisibleForTesting
        internal fun getDuration(endTime: Long, startTime: Long): String {
            return if (endTime == 0L) {
                TimeUtils.convertToHourMinutes(TimeUtils.getCalender12AM(startTime).timeInMillis
                        + TimeUtils.ONE_DAY_MILLISECONDS - startTime)
            } else {
                TimeUtils.convertToHourMinutes(endTime - startTime)
            }
        }

        @VisibleForTesting
        internal fun getActivityText(userActivityType: UserActivityType): String {
            return when (userActivityType) {
                UserActivityType.SITTING -> "Sitting for"
                UserActivityType.MOVING -> "Standing/Moving for"
                UserActivityType.NOT_TRACKED -> "Activity not tracked for"
            }
        }

        fun createViewHolder(context: Context, parent: ViewGroup?): UserActivityViewHolder {
            return UserActivityViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.row_user_activity, parent, false))
        }
    }
}
