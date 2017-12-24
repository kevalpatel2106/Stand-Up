package com.kevalpatel2106.standup.diary

import android.content.Context
import android.view.ViewGroup
import com.kevalpatel2106.base.paging.PageRecyclerViewAdapter
import com.kevalpatel2106.standup.userActivity.UserActivity

/**
 * Created by Keval on 23/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DiaryListAdapter(context: Context, data: ArrayList<UserActivity>,
                                listener: RecyclerViewListener<UserActivity>?)
    : PageRecyclerViewAdapter<DiaryViewHolder, UserActivity>(context, data, listener) {


    override fun bindView(holder: DiaryViewHolder, item: UserActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun prepareViewHolder(parent: ViewGroup?, viewType: Int): DiaryViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun prepareViewType(position: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}