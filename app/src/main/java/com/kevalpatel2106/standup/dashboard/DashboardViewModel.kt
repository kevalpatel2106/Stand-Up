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

package com.kevalpatel2106.standup.dashboard

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.CallbackEvent
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.dashboard.repo.DashboardRepo
import com.kevalpatel2106.standup.dashboard.repo.DashboardRepoImpl
import com.kevalpatel2106.standup.db.DailyActivitySummary
import com.kevalpatel2106.standup.misc.SUUtils
import com.kevalpatel2106.standup.timelineview.TimeLineItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Keval on 21/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class DashboardViewModel : BaseViewModel {
    private val userActivityRepo: DashboardRepo

    val todaySummary = MutableLiveData<DailyActivitySummary>()
    val todaySummaryErrorCallback = MutableLiveData<ErrorMessage>()
    val todaySummaryStartLoading = CallbackEvent()

    val timelineEventsList = MutableLiveData<ArrayList<TimeLineItem>>()

    /**
     * Private constructor to add the custom [DashboardRepo] for testing.
     *
     * @param dashboardRepo Add your own [DashboardRepo].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(dashboardRepo: DashboardRepo) : super() {
        this.userActivityRepo = dashboardRepo

        //Start observing the database
        startObservingTodayEvents()
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : super() {
        //This is the original user authentication repo.
        userActivityRepo = DashboardRepoImpl()

        //Start observing the database
        startObservingTodayEvents()
    }

    @VisibleForTesting
    internal fun startObservingTodayEvents() {
        userActivityRepo.getTodaySummary()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    todaySummaryStartLoading.dispatch()
                    todaySummaryErrorCallback.value = null
                }
                .doOnComplete {
                    if (todaySummaryErrorCallback.value == null)
                        todaySummaryErrorCallback.value = ErrorMessage("No activity found today.")
                }
                .subscribe({
                    todaySummary.value = it

                    //Prepare the timeline data
                    val timelineItems = ArrayList<TimeLineItem>(it.dayActivity.size)
                    it.dayActivity.forEach { timelineItems.add(SUUtils.createTimeLineItemFromUserActivity(it)) }
                    timelineEventsList.value = timelineItems
                }, {
                    //Error message
                    todaySummaryErrorCallback.value = ErrorMessage(it.message)
                })
    }
}