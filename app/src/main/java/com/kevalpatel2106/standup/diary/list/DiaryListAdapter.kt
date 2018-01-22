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

package com.kevalpatel2106.standup.diary.list

import android.content.Context
import android.support.annotation.VisibleForTesting
import android.view.ViewGroup
import com.kevalpatel2106.common.base.paging.PageRecyclerViewAdapter
import com.kevalpatel2106.common.db.DailyActivitySummary
import com.kevalpatel2106.standup.diary.detail.DetailActivity
import com.kevalpatel2106.standup.diary.repo.DiaryRepo

/**
 * Created by Keval on 23/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DiaryListAdapter(context: Context, data: ArrayList<DailyActivitySummary>,
                                listener: RecyclerViewListener<DailyActivitySummary>?)
    : PageRecyclerViewAdapter<DiaryBaseViewHolder, DailyActivitySummary>(context, data, listener) {

    companion object {

        @VisibleForTesting
        internal val TYPE_DAY_VIEW = 1

        @VisibleForTesting
        internal val TYPE_MONTH_VIEW = 2
    }


    override fun bindView(holder: DiaryBaseViewHolder, item: DailyActivitySummary) {
        when (holder) {
            is DiaryMonthViewHolder -> {
                holder.setData(item as MonthHeader)
            }
            is DiaryDayViewHolder -> {
                holder.setData(item, {
                    DetailActivity.launch(context = context, dayOfMonth = item.dayOfMonth,
                            monthOfYear = item.monthOfYear, year = item.year)
                })
            }
            else -> throw IllegalStateException("Invalid view holder type.")
        }
    }

    override fun prepareViewHolder(parent: ViewGroup?, viewType: Int): DiaryBaseViewHolder {
        return when (viewType) {
            TYPE_DAY_VIEW -> DiaryDayViewHolder.create(context, parent)
            TYPE_MONTH_VIEW -> DiaryMonthViewHolder.create(context, parent)
            else -> throw IllegalArgumentException("Invalid view type: ".plus(viewType))
        }
    }

    override fun prepareViewType(position: Int): Int {
        return if (getItem(position) is MonthHeader) TYPE_MONTH_VIEW else TYPE_DAY_VIEW
    }

    override fun getPageSize(): Int = DiaryRepo.PAGE_SIZE
}
