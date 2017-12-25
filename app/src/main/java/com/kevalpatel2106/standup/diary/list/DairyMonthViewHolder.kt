package com.kevalpatel2106.standup.diary.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.standup.R

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DairyMonthViewHolder(itemView: View) : DiaryBaseViewHolder(itemView) {

    companion object {

        @JvmStatic
        fun create(context: Context, parent: ViewGroup?): DairyMonthViewHolder {
            return DairyMonthViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.row_dairy_list_month, parent, false))
        }
    }

    init {

    }

}