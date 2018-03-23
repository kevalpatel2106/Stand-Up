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
class DiaryMonthViewHolderTest {

    @Test
    @Throws(Exception::class)
    fun checkCreate() {
        val holder = DiaryMonthViewHolder.create(
                context = InstrumentationRegistry.getContext(),
                parent = RelativeLayout(InstrumentationRegistry.getContext())
        )

        Assert.assertNotNull(holder)
        Assert.assertNotNull(holder.itemView.findViewById<BaseTextView>(R.id.dairy_row_month_name_header_tv))
    }

    @Test
    @Throws(Exception::class)
    fun checkBind() {
        val itemView = LayoutInflater.from(InstrumentationRegistry.getContext())
                .inflate(R.layout.row_dairy_list_month, null)

        val holder = DiaryMonthViewHolder(itemView)

        holder.setData(MonthHeader(0, 2000))

        Assert.assertEquals("JAN, 2000", itemView.findViewById<BaseTextView>(R.id.dairy_row_month_name_header_tv).text)
    }
}
