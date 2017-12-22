package com.kevalpatel2106.standup.dashboard

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.userActivity.UserActivity
import com.kevalpatel2106.standup.userActivity.UserActivityType
import com.kevalpatel2106.standup.userActivity.repo.UserActivityRepo
import com.kevalpatel2106.standup.userActivity.repo.UserActivityRepoImpl
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Keval on 21/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class DashboardViewModel : BaseViewModel {

    private val simpleDateFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val userActivityRepo: UserActivityRepo

    val standingPercent = MutableLiveData<Float>()

    val sittingPercent = MutableLiveData<Float>()

    val trackedDuration = MutableLiveData<String>()

    val trackedTime = MutableLiveData<String>()

    val sittingTime = MutableLiveData<String>()

    val standingTime = MutableLiveData<String>()

    val todayActivities = MutableLiveData<ArrayList<UserActivity>>()

    /**
     * Private constructor to add the custom [UserActivityRepo] for testing.
     *
     * @param userActivityRepo Add your own [UserActivityRepo].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(userActivityRepoImpl: UserActivityRepo) : super() {
        this.userActivityRepo = userActivityRepoImpl

        //Start observing the database
        startObservingTodayEvents()
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : super() {
        //This is the original user authentication repo.
        userActivityRepo = UserActivityRepoImpl()

        //Start observing the database
        startObservingTodayEvents()
    }

    @VisibleForTesting
    internal fun startObservingTodayEvents() {
        userActivityRepo.getTodayEvents().subscribe({

            // Refresh the list
            if (todayActivities.value != null) {
                todayActivities.value!!.clear()
            } else {
                todayActivities.value = ArrayList()
            }
            todayActivities.value!!.addAll(it)

            //Calculate total time
            val startTime = simpleDateFormatter.format(it.first().eventStartTimeMills)
            val endTime = simpleDateFormatter.format(it.last().eventEndTimeMills)
            trackedTime.value = startTime.plus("-").plus(endTime)

            //Calculate duration
            val durationMills = it.last().eventEndTimeMills - it.first().eventStartTimeMills
            trackedDuration.value = convertToHourMinutes(durationMills)

            // Calculate standing and sitting time.
            var standingMills = 0L
            var sittingMills = 0L
            it.forEach({
                when (it.userActivityType) {
                    UserActivityType.SITTING -> {
                        sittingMills += (it.eventEndTimeMills - it.eventStartTimeMills)
                    }
                    UserActivityType.MOVING -> {
                        standingMills += (it.eventEndTimeMills - it.eventStartTimeMills)
                    }
                    UserActivityType.SLEEPING -> {
                        //NO OP
                    }
                }
            })

            sittingTime.value = convertToHourMinutes(sittingMills)
            sittingPercent.value = calculatePercent(sittingMills, durationMills)
            standingTime.value = convertToHourMinutes(standingMills)
            standingPercent.value = calculatePercent(standingMills, durationMills)
        }, {
            //Error message
            errorMessage.value = ErrorMessage(it.message)
        })
    }


    /**
     * Converts [timeMills] into (hour)h (minutes)m format. Here hour will be in 24 hours format.
     */
    @VisibleForTesting
    internal fun convertToHourMinutes(timeMills: Long): String {
        val hours = timeMills % 3600000
        val mins = (timeMills - (hours * 3600000)) / 60000
        return "${hours}h ${mins}m"
    }

    /**
     * Converts [timeMills] into (hour)h (minutes)m format. Here hour will be in 24 hours format.
     */
    @VisibleForTesting
    internal fun calculatePercent(value: Long, total: Long): Float {
        return ((value / total) * 100).toFloat()
    }
}