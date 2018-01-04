/*
 *  Copyright 2017 Keval Patel.
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
import com.kevalpatel2106.standup.db.DailyActivitySummary
import com.kevalpatel2106.standup.diary.repo.DiaryRepo
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 03-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class DiaryListAdapterTest {

    private lateinit var diaryListAdapter: DiaryListAdapter
    private val arrayList = ArrayList<DailyActivitySummary>()

    @Before
    fun setUp() {
        val context = Mockito.mock(Context::class.java)
        diaryListAdapter = DiaryListAdapter(context, arrayList, null)
    }

    @Test
    fun checkPrepareViewTypeForHeaderType() {
        arrayList.add(MonthHeader(9, 2016))
        val type = diaryListAdapter.prepareViewType(0)

        Assert.assertEquals(DiaryListAdapter.TYPE_MONTH_VIEW, type)
    }

    @Test
    fun checkPrepareViewTypeForUserActivityType() {
        @Suppress("DEPRECATION")
        arrayList.add(DailyActivitySummary(
                dayOfMonth = 1,
                monthOfYear = 11,
                year = 2017,
                dayActivity = ArrayList(0)
        ))
        val type = diaryListAdapter.prepareViewType(0)

        Assert.assertEquals(DiaryListAdapter.TYPE_DAY_VIEW, type)
    }

    @Test
    fun checkGetPageSize() {
        Assert.assertEquals(diaryListAdapter.getPageSize(), DiaryRepo.PAGE_SIZE)
    }

}