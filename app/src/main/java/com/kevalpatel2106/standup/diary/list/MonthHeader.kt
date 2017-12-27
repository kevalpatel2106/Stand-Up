package com.kevalpatel2106.standup.diary.list

import com.kevalpatel2106.standup.db.DailyActivitySummary

/**
 * Created by Keval on 27/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class MonthHeader(monthOfYear: Int, year: Int) : DailyActivitySummary(
        dayOfMonth = 1,
        monthOfYear = monthOfYear,
        year = year,
        startTimeMills = System.currentTimeMillis(),
        endTimeMills = System.currentTimeMillis() + 2,
        sittingTimeMills = 1,
        standingTimeMills = 1
) {

    fun getMonthHeader() = "$monthInitials, $year"
}