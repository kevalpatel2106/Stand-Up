package com.kevalpatel2106.standup.dashboard

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.userActivity.UserActivity
import com.kevalpatel2106.standup.userActivity.UserActivityType
import com.kevalpatel2106.standup.userActivity.repo.UserActivityRepo
import com.kevalpatel2106.standup.userActivity.repo.UserActivityRepoImpl
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
     * @param userActivityRepoImpl Add your own [UserActivityRepo].
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

            //Remove the last incomplete event
            val list: List<UserActivity> = if (it.last().eventEndTimeMills == 0L) {
                it.subList(0, it.lastIndex - 1)
            } else {
                it
            }

            //Calculate total time
            val startTime = simpleDateFormatter.format(list.first().eventStartTimeMills)
            val endTime = simpleDateFormatter.format(list.last().eventEndTimeMills)
            trackedTime.value = startTime.plus("-").plus(endTime)

            //Calculate duration
            val durationMills = list.last().eventEndTimeMills - list.first().eventStartTimeMills
            trackedDuration.value = TimeUtils.convertToHourMinutes(durationMills)

            // Calculate standing and sitting time.
            var standingMills = 0L
            var sittingMills = 0L
            list.forEach({
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

            sittingTime.value = TimeUtils.convertToHourMinutes(sittingMills)
            sittingPercent.value = Utils.calculatePercent(sittingMills, durationMills).toFloat()
            standingTime.value = TimeUtils.convertToHourMinutes(standingMills)
            standingPercent.value = Utils.calculatePercent(standingMills, durationMills).toFloat()
        }, {
            //Error message
            errorMessage.value = ErrorMessage(it.message)
        })
    }
}