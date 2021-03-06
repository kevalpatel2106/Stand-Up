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

package com.standup.app.dashboard

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.CallbackEvent
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.kevalpatel2106.utils.annotations.OnlyForTesting
import com.kevalpatel2106.utils.annotations.ViewModel
import com.kevalpatel2106.utils.rxbus.RxBus
import com.standup.app.dashboard.di.DaggerDashboardComponent
import com.standup.app.dashboard.repo.DashboardRepo
import com.standup.core.CoreConfig
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Keval on 21/12/17.
 * [ViewModel] for the [DashboardFragment].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see DashboardFragment
 */
@ViewModel(DashboardFragment::class)
internal class DashboardViewModel : BaseViewModel {
    private lateinit var notificationEvenDisposable: Disposable

    /**
     * [DashboardRepo] for loading and processing the data from the database and network.
     *
     * @see DashboardRepo
     */
    @Inject
    lateinit var dashboardRepo: DashboardRepo

    /**
     * Private constructor to add the custom [DashboardRepo] for testing.
     *
     * @param dashboardRepo Add custom [DashboardRepo].
     */
    @Suppress("unused")
    @VisibleForTesting
    @OnlyForTesting
    constructor(dashboardRepo: DashboardRepo) : super() {
        this.dashboardRepo = dashboardRepo

        init()
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() {
        DaggerDashboardComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@DashboardViewModel)

        init()
    }

    private fun init() {
        //Register to listen notification schedule events
        notificationEvenDisposable = RxBus.register(CoreConfig.TAG_RX_NOTIFICATION_TIME_UPDATED)
                .subscribe({ loadNextNotificationTime() })
    }

    override fun onCleared() {
        super.onCleared()
        notificationEvenDisposable.dispose()
    }

    /**
     * [MutableLiveData] for [DailyActivitySummary]. UI controller can observe this property to
     * get daily summary for current day.
     *
     * @see DailyActivitySummary
     */
    val todaySummary = MutableLiveData<DailyActivitySummary>()

    /**
     * [MutableLiveData] for [ErrorMessage]. UI controller can observe this property to
     * get notify whenever any error occurs while loading the summary.
     *
     * @see ErrorMessage
     */
    val todaySummaryErrorCallback = MutableLiveData<ErrorMessage>()

    /**
     * [CallbackEvent] to notify whenever summary loading starts .
     *
     * @see CallbackEvent
     */
    val todaySummaryStartLoading = CallbackEvent()

    /**
     * [MutableLiveData] to store the text to display for the next reminder.
     */
    val nextReminderStatus = MutableLiveData<String>()

    internal fun loadNextNotificationTime() {
        nextReminderStatus.value = dashboardRepo.getNextReminderStatus()
    }

    /**
     * Get the summary off today's daily activity. This is an asynchronous method which reads database
     * and process the data to generate stats on the background thread and deliver result to the
     * main thread.
     *
     * UI controller can observe [todaySummary] to  get notify whenever the summary gets updated.
     * This summary contains sitting and standing time statistics based on the user activity from
     * 12 am of the current day.
     *
     * Whenever this method starts loading summary stats [todaySummaryStartLoading] will be set to true.
     * If any error occurs while execrating summary, [todaySummaryErrorCallback] will be called.
     *
     * @see DailyActivitySummary
     * @see DashboardRepo.getTodaySummary
     */
    internal fun getTodaysSummary() {
        dashboardRepo.getTodaySummary()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    todaySummaryStartLoading.dispatch()
                    todaySummaryErrorCallback.value = null
                }
                .doOnComplete {
                    if (todaySummary.value == null)
                        todaySummaryErrorCallback.value = ErrorMessage(R.string.dashboard_today_summary_not_fount)
                }
                .subscribe({
                    todaySummary.value = it
                }, {
                    //Error message
                    todaySummaryErrorCallback.value = ErrorMessage(it.message)
                })
    }
}
