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

package com.kevalpatel2106.standup.dashboard

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.CallbackEvent
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.db.DailyActivitySummary
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.SUUtils
import com.kevalpatel2106.standup.dashboard.di.DaggerDashboardComponent
import com.kevalpatel2106.standup.dashboard.repo.DashboardRepo
import com.kevalpatel2106.standup.timelineview.TimeLineItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Keval on 21/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class DashboardViewModel : BaseViewModel {

    @Inject lateinit var userActivityRepo: DashboardRepo

    /**
     * Private constructor to add the custom [DashboardRepo] for testing.
     *
     * @param dashboardRepo Add your own [DashboardRepo].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(dashboardRepo: DashboardRepo) : super() {
        this.userActivityRepo = dashboardRepo
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() {
        DaggerDashboardComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build().inject(this@DashboardViewModel)
    }

    val todaySummary = MutableLiveData<DailyActivitySummary>()
    val todaySummaryErrorCallback = MutableLiveData<ErrorMessage>()
    val todaySummaryStartLoading = CallbackEvent()
    val timelineEventsList = MutableLiveData<ArrayList<TimeLineItem>>()

    internal fun getTodaysSummary() {
        userActivityRepo.getTodaySummary()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    todaySummaryStartLoading.dispatch()
                    todaySummaryErrorCallback.value = null
                }
                .doOnComplete {
                    if (timelineEventsList.value == null)
                        todaySummaryErrorCallback.value = ErrorMessage(R.string.dashboard_today_summary_not_fount)
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
