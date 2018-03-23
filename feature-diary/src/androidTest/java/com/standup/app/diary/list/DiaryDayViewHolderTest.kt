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

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.kevalpatel2106.common.view.BaseTextView
import com.standup.app.diary.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Kevalpatel2106 on 16-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class DiaryDayViewHolderTest {

    @Test
    @Throws(Exception::class)
    fun checkCreate() {
        val holder = DiaryDayViewHolder.create(
                context = InstrumentationRegistry.getContext(),
                parent = RelativeLayout(InstrumentationRegistry.getContext())
        )

        Assert.assertNotNull(holder)
        Assert.assertNotNull(holder.itemView.findViewById<BaseTextView>(R.id.dairy_row_date_tv))
        Assert.assertNotNull(holder.itemView.findViewById<BaseTextView>(R.id.dairy_row_month_tv))
        Assert.assertNotNull(holder.itemView.findViewById<BaseTextView>(R.id.dairy_row_sitting_percent_tv))
        Assert.assertNotNull(holder.itemView.findViewById<BaseTextView>(R.id.dairy_row_standing_time_tv))
        Assert.assertNotNull(holder.itemView.findViewById<BaseTextView>(R.id.dairy_row_sitting_time_tv))
    }

    @Test
    @Throws(Exception::class)
    fun checkBind() {
        val itemView = LayoutInflater.from(InstrumentationRegistry.getContext())
                .inflate(R.layout.row_dairy_list_item, null)

        val holder = DiaryDayViewHolder(itemView)

        val dayActivity = ArrayList<UserActivity>()
        //Not starting activity
        dayActivity.add(UserActivity(eventStartTimeMills = 0,
                eventEndTimeMills = System.currentTimeMillis() - 60_000,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        //Not ending activity
        dayActivity.add(UserActivity(eventStartTimeMills = 30_000,
                eventEndTimeMills = 0,
                type = UserActivityType.SITTING.name,
                isSynced = true))
        val daySummary = DailyActivitySummary(1, 0, 2001, dayActivity)
        holder.setData(daySummary, { /* Do nothing */ })

        Assert.assertEquals(itemView.findViewById<BaseTextView>(R.id.dairy_row_date_tv).text, "1")
        Assert.assertEquals(itemView.findViewById<BaseTextView>(R.id.dairy_row_month_tv).text, "JAN")
        Assert.assertEquals(itemView.findViewById<BaseTextView>(R.id.dairy_row_sitting_percent_tv).text, daySummary.sittingPercent.toString())
        Assert.assertEquals(itemView.findViewById<BaseTextView>(R.id.dairy_row_standing_time_tv).text, daySummary.standingTimeHours)
        Assert.assertEquals(itemView.findViewById<BaseTextView>(R.id.dairy_row_sitting_time_tv).text, daySummary.sittingTimeHours)
    }
}
