package com.kevalpatel2106.standup.diary.list

import android.content.Context
import android.view.ViewGroup
import com.kevalpatel2106.base.paging.PageRecyclerViewAdapter
import com.kevalpatel2106.standup.db.userActivity.UserActivity

/**
 * Created by Keval on 23/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DiaryListAdapter(context: Context, data: ArrayList<UserActivity>,
                                listener: RecyclerViewListener<UserActivity>?)
    : PageRecyclerViewAdapter<DiaryBaseViewHolder, UserActivity>(context, data, listener) {

    private val TYPE_DAY_VIEW = 1
    private val TYPE_MONTH_VIEW = 2


    override fun bindView(holder: DiaryBaseViewHolder, item: UserActivity) {
        when (holder) {
            is DairyMonthViewHolder -> {

            }
            is DairyDayViewHolder -> {

            }
            else -> throw IllegalStateException("Invalid view holder type.")
        }
    }

    override fun prepareViewHolder(parent: ViewGroup?, viewType: Int): DiaryBaseViewHolder {
        return when (viewType) {
            TYPE_DAY_VIEW -> DairyDayViewHolder.create(context, parent)
            TYPE_MONTH_VIEW -> DairyMonthViewHolder.create(context, parent)
            else -> throw IllegalArgumentException("Invalid view type: ".plus(viewType))
        }
    }

    override fun prepareViewType(position: Int): Int {
        return TYPE_MONTH_VIEW
    }

}