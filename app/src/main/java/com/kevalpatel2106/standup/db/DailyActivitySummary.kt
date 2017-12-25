package com.kevalpatel2106.standup.db

import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
data class DailyActivitySummary(

        /**
         * Day of the month between 1 to 31.
         */
        val dayOfMonth: Int,

        /**
         * Month number from 0 to 11.
         */
        val monthOfYear: Int,

        val startTimeMills: Long,

        val endTimeMills: Long,

        val standingTimeMills: Long,

        val sittingTimeMills: Long
) {
    private val simpleDateFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    /**
     * 3 days initials of the month from [monthOfYear].
     */
    val monthInitials: String
        get() = TimeUtils.getMonthInitials(monthOfYear)

    var userActivities: ArrayList<UserActivity>? = null

    val durationMills: Long
        get() = endTimeMills - startTimeMills

    val durationTimeHours: String
        get() = TimeUtils.convertToHourMinutes(durationMills)

    val startTimeHours: String
        get() = simpleDateFormatter.format(startTimeMills)

    val endTimeHours: String
        get() = simpleDateFormatter.format(endTimeMills)

    val sittingTimeHours: String
        get() = TimeUtils.convertToHourMinutes(sittingTimeMills)

    val sittingPercent: Float
        get() = Utils.calculatePercent(sittingTimeMills, durationMills).toFloat()

    val standingPercent: Float
        get() = Utils.calculatePercent(standingTimeMills, durationMills).toFloat()

    val standingTimeHours: String
        get() = TimeUtils.convertToHourMinutes(standingTimeMills)

    companion object {

        @JvmStatic
        fun fromDayActivityList(dayActivity: ArrayList<UserActivity>): DailyActivitySummary? {
            //Sort by the event start time in descending order
            Collections.sort(dayActivity) { o1, o2 -> (o2.eventStartTimeMills - o1.eventStartTimeMills).toInt() }

            //Remove the last incomplete event
            val itrr = dayActivity.iterator()
            while (itrr.hasNext()) {
                if (itrr.next().eventEndTimeMills <= 0L) itrr.remove()
            }
            if (dayActivity.isEmpty()) return null

            //Get calender
            val dayCal = Calendar.getInstance()
            dayCal.timeInMillis = dayActivity.first().eventStartTimeMills

            var standingMills = 0L
            var sittingMills = 0L
            dayActivity.forEach({
                when (it.userActivityType) {
                    UserActivityType.SITTING -> {
                        sittingMills += (it.eventEndTimeMills - it.eventStartTimeMills)
                    }
                    UserActivityType.MOVING -> {
                        standingMills += (it.eventEndTimeMills - it.eventStartTimeMills)
                    }
                    UserActivityType.NOT_TRACKED -> {
                        //NO OP
                    }
                }
            })

            val dayActivitySummary = DailyActivitySummary(
                    dayOfMonth = dayCal.get(Calendar.DAY_OF_MONTH),
                    monthOfYear = dayCal.get(Calendar.MONTH),
                    startTimeMills = dayActivity.first().eventStartTimeMills,
                    endTimeMills = dayActivity.last().eventEndTimeMills,
                    standingTimeMills = standingMills,
                    sittingTimeMills = sittingMills)
            dayActivitySummary.userActivities = dayActivity
            return dayActivitySummary
        }
    }
}