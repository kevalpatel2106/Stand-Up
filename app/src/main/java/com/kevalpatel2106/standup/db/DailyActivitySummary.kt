package com.kevalpatel2106.standup.db

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.standup.Validator
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

        /**
         * Year of the day.
         */
        val year: Int,

        /**
         * Starting time of the activity tracking.
         */
        val startTimeMills: Long,

        /**
         * Ending time of the activity tracking.
         */
        val endTimeMills: Long,

        /**
         * Total standing activity time in milliseconds.
         */
        val standingTimeMills: Long,

        /**
         * Total sitting activity time in milliseconds.
         */
        val sittingTimeMills: Long
) : Parcelable {

    var userActivities: ArrayList<UserActivity>? = null
        @SuppressLint("VisibleForTests")
        set(value) {
            value?.let { convertToValidUserActivityList(value) }

            field = value
        }

    /**
     * 3 days initials of the month from [monthOfYear].
     */
    val monthInitials: String

    val durationMills: Long

    val durationTimeHours: String

    val startTimeHours: String

    val endTimeHours: String

    val sittingTimeHours: String

    val sittingPercent: Float

    val standingPercent: Float

    val standingTimeHours: String

    val notTrackedTime: Long

    init {
        if (!Validator.isValidDate(dayOfMonth)) {
            throw IllegalArgumentException("Day of month must be between 1 to 31. Current: "
                    .plus(dayOfMonth))
        }

        if (!Validator.isValidMonth(monthOfYear)) {
            throw IllegalArgumentException("Month must be between 0 to 11. Current: ".plus(monthOfYear))
        }

        if (!Validator.isValidYear(year)) {
            throw IllegalArgumentException("Year must be between 1900 to 2100. Current: ".plus(year))
        }

        if (startTimeMills <= 0L) {
            throw IllegalArgumentException("Start time must be positive number. Current: "
                    .plus(startTimeMills))
        }

        if (endTimeMills <= 0L || endTimeMills < startTimeMills) {
            throw IllegalArgumentException("End time must be positive number and >= start time. Current: "
                    .plus(endTimeMills).plus(" Start time:").plus(startTimeMills))
        }

        if (standingTimeMills < 0) {
            throw IllegalArgumentException("Standing time must be positive number or 0. Current: "
                    .plus(standingTimeMills))
        }

        if (sittingTimeMills < 0) {
            throw IllegalArgumentException("Sitting time must be positive number or 0. Current: "
                    .plus(sittingTimeMills))
        }

        val simpleDateFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

        monthInitials = TimeUtils.getMonthInitials(monthOfYear)

        durationMills = endTimeMills - startTimeMills
        if (standingTimeMills + sittingTimeMills > durationMills) {
            throw IllegalStateException("Total of standing and sitting time is more than tracking duration.")
        }
        durationTimeHours = TimeUtils.convertToHourMinutes(durationMills)

        notTrackedTime = durationMills - (sittingTimeMills + standingTimeMills)

        startTimeHours = simpleDateFormatter.format(startTimeMills)
        endTimeHours = simpleDateFormatter.format(endTimeMills)

        sittingTimeHours = TimeUtils.convertToHourMinutes(sittingTimeMills)
        sittingPercent = Utils.calculatePercent(sittingTimeMills, durationMills).toFloat()

        standingPercent = Utils.calculatePercent(standingTimeMills, durationMills).toFloat()
        standingTimeHours = TimeUtils.convertToHourMinutes(standingTimeMills)
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong())

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is DailyActivitySummary) return false
        if (other == this) return true
        if (other.dayOfMonth == dayOfMonth && other.monthOfYear == monthOfYear && other.year == year)
            return true
        return false
    }

    override fun hashCode(): Int = dayOfMonth * 1000 + monthOfYear * 100 + dayOfMonth * 10

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(dayOfMonth)
        parcel.writeInt(monthOfYear)
        parcel.writeInt(year)
        parcel.writeLong(startTimeMills)
        parcel.writeLong(endTimeMills)
        parcel.writeLong(standingTimeMills)
        parcel.writeLong(sittingTimeMills)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DailyActivitySummary> {

        override fun createFromParcel(parcel: Parcel): DailyActivitySummary {
            return DailyActivitySummary(parcel)
        }

        override fun newArray(size: Int): Array<DailyActivitySummary?> {
            return arrayOfNulls(size)
        }

        /**
         * Create the [DailyActivitySummary] object from the list of [UserActivity] for the
         * given day. Make sure that [dayActivity] should contain the [UserActivity] which
         * happened on the same day (i.e. between 12 AM to 11:59:59 PM of the day).
         *
         * @throws IllegalArgumentException if the [dayActivity] doesn't contain [UserActivity] for
         * the same day.
         */
        @SuppressLint("VisibleForTests")
        @JvmStatic
        fun fromDayActivityList(dayActivity: ArrayList<UserActivity>): DailyActivitySummary {
            if (dayActivity.isEmpty()) throw IllegalStateException("Activity list cannot be null. Check first.")

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
                    year = dayCal.get(Calendar.YEAR),
                    startTimeMills = dayActivity.first().eventStartTimeMills,
                    endTimeMills = dayActivity.last().eventEndTimeMills,
                    standingTimeMills = standingMills,
                    sittingTimeMills = sittingMills)
            dayActivitySummary.userActivities = dayActivity
            return dayActivitySummary
        }

        internal fun convertToValidUserActivityList(dayActivity: ArrayList<UserActivity>) {
            //Sort by the event start time in descending order
            Collections.sort(dayActivity) { o1, o2 -> (o1.eventStartTimeMills - o2.eventStartTimeMills).toInt() }

            //Remove the last incomplete event
            val iter = dayActivity.iterator()
            while (iter.hasNext()) {
                val value = iter.next()
                if (value.eventEndTimeMills <= 0L || value.eventStartTimeMills <= 0L) {
                    iter.remove()
                }
            }
        }
    }
}
