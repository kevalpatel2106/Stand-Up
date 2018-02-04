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
import com.standup.R
import kotlinx.android.synthetic.main.row_dairy_list_month.view.*

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DiaryMonthViewHolder(itemView: View) : DiaryBaseViewHolder(itemView) {

    companion object {

        @JvmStatic
        fun create(context: Context, parent: ViewGroup?): DiaryMonthViewHolder {
            return DiaryMonthViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.row_dairy_list_month, parent, false))
        }
    }

    fun setData(monthHeader: MonthHeader) {
        itemView.dairy_row_month_name_header_tv.text = monthHeader.getMonthHeader()
    }

}