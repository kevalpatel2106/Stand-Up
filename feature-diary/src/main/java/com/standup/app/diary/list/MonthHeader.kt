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

import com.kevalpatel2106.common.userActivity.DailyActivitySummary

/**
 * Created by Keval on 27/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Suppress("DEPRECATION")
internal class MonthHeader(monthOfYear: Int, year: Int) : DailyActivitySummary(
        dayOfMonth = 1,
        monthOfYear = monthOfYear,
        year = year,
        dayActivity = ArrayList(0)
) {

    fun getMonthHeader() = "$monthInitials, $year"
}
