package com.kevalpatel2106.standup.diary.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.db.DailyActivitySummary
import kotlinx.android.synthetic.main.row_dairy_list_item.view.*

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DairyDayViewHolder(itemView: View) : DiaryBaseViewHolder(itemView) {


    companion object {
        @JvmStatic
        fun create(context: Context, parent: ViewGroup?): DairyDayViewHolder {
            return DairyDayViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.row_dairy_list_item, parent, false))
        }
    }

    fun setData(summary: DailyActivitySummary) {
        itemView.dairy_row_date_tv.text = summary.dayOfMonth.toString()
        itemView.dairy_row_month_tv.text = summary.monthInitials
        itemView.dairy_row_day_of_week_tv.text = "Monday"   //TODO
        itemView.dairy_row_sitting_percent_tv.text = summary.sittingPercent.toString()
        itemView.dairy_row_sitting_time_tv.text = summary.sittingTimeHours
        itemView.dairy_row_standing_time_tv.text = summary.standingTimeHours
    }
}