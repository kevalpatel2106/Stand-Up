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

package com.standup.app.diary.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.common.db.DailyActivitySummary
import com.standup.R
import kotlinx.android.synthetic.main.row_dairy_list_item.view.*

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DiaryDayViewHolder(itemView: View) : DiaryBaseViewHolder(itemView) {


    companion object {
        @JvmStatic
        fun create(context: Context, parent: ViewGroup?): DiaryDayViewHolder {
            return DiaryDayViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.row_dairy_list_item, parent, false))
        }
    }

    fun setData(summary: DailyActivitySummary, onClick: () -> Unit) {

        itemView.dairy_row_date_tv.text = summary.dayOfMonth.toString()
        itemView.dairy_row_date_tv.setOnClickListener { onClick.invoke() }

        itemView.dairy_row_month_tv.text = summary.monthInitials
        itemView.dairy_row_month_tv.setOnClickListener { onClick.invoke() }

        itemView.dairy_row_day_of_week_tv.text = "Monday"   //TODO
        itemView.dairy_row_day_of_week_tv.setOnClickListener { onClick.invoke() }

        itemView.dairy_row_sitting_percent_tv.text = summary.sittingPercent.toString()
        itemView.dairy_row_sitting_percent_tv.setOnClickListener { onClick.invoke() }

        itemView.dairy_row_sitting_time_tv.text = summary.sittingTimeHours
        itemView.dairy_row_sitting_time_tv.setOnClickListener { onClick.invoke() }

        itemView.dairy_row_standing_time_tv.text = summary.standingTimeHours
        itemView.dairy_row_standing_time_tv.setOnClickListener { onClick.invoke() }

        itemView.setOnClickListener { onClick.invoke() }
    }
}
