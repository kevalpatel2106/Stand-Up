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

package com.standup.app.diary.detail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.misc.lottie.LottieJson
import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.annotations.OnlyForTesting
import com.kevalpatel2106.utils.annotations.ViewModel
import com.standup.app.billing.repo.BillingRepo
import com.standup.app.diary.R
import com.standup.app.diary.di.DaggerDiaryComponent
import com.standup.app.diary.repo.DiaryRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Keval on 22/01/18.
 * [ViewModel] for the [DetailActivity].
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@ViewModel(DetailActivity::class)
internal class DetailViewModel : BaseViewModel {

    @Inject
    internal lateinit var application: Application

    /**
     * [DiaryRepo] for loading and processing the data from the database and network.
     *
     * @see DiaryRepo
     */
    @Inject
    internal lateinit var diaryRepo: DiaryRepo

    @Inject
    internal lateinit var billingRepo: BillingRepo

    internal val isPremiumUser = MutableLiveData<Boolean>()

    /**
     * [MutableLiveData] for [DailyActivitySummary]. UI controller can observe this property to
     * get daily summary for the given day.
     *
     * @see DailyActivitySummary
     */
    internal val summary = MutableLiveData<DailyActivitySummary>()

    /**
     * Private constructor to add the custom [DiaryRepo] for testing.
     *
     * @param diaryRepo Add custom [DiaryRepo].
     */
    @VisibleForTesting
    @OnlyForTesting
    constructor(
            diaryRepo: DiaryRepo,
            billingRepo: BillingRepo,
            application: Application
    ) {
        this.diaryRepo = diaryRepo
        this.billingRepo = billingRepo
        this.application = application

        init()
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() {
        DaggerDiaryComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@DetailViewModel)

        init()
    }

    /**
     * Initialize the variables.
     */
    private fun init() {
        summary.value = null
        isPremiumUser.value = true
    }

    /**
     * Check if the user is premium user? If the user is premium user, [isPremiumUser] will be true
     * else it will be false if user is not premium user or any error occurs while retriving the
     * purchases.
     *
     * @see BillingRepo.isPremiumPurchased
     */
    internal fun checkIsPremiumUser() {
        billingRepo.isPremiumPurchased(application).subscribe({
            isPremiumUser.value = it
        }, {
            isPremiumUser.value = false
        })
    }

    /**
     * Fetch the [DailyActivitySummary] for the given date of [dayOfMonth]-[month]-[year]. This is
     * an asynchronous method which reads database and process the data to generate stats on the
     * background thread and deliver result to the main thread.
     *
     * UI controller can observe [summary] to  get notify whenever the summary gets updated.
     * This summary contains sitting and standing time statistics based on the user activity from
     * 12 am of the given [dayOfMonth].
     *
     * Whenever this method starts loading summary stats [blockUi] will be set to true.
     * If any error occurs while execrating summary, [blockUi] will be called.
     *
     * @see DailyActivitySummary
     * @see DiaryRepo.loadSummary
     */
    fun fetchData(dayOfMonth: Int, month: Int, year: Int) {
        addDisposable(diaryRepo.loadSummary(dayOfMonth, month, year)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    blockUi.value = true
                }
                .doOnTerminate {
                    blockUi.value = false

                    if (summary.value == null) {
                        val errorMsg = ErrorMessage(String.format(
                                application.getString(R.string.error_detail_no_user_activity_found),
                                dayOfMonth,
                                TimeUtils.getMonthInitials(month),
                                year))

                        errorMsg.setErrorBtn(R.string.error_view_btn_title_retry, { fetchData(dayOfMonth, month, year) })
                        errorMsg.errorImage = LottieJson.CLOUD_FLOATING
                        errorMessage.value = errorMsg
                    }
                }
                .subscribe({
                    summary.value = it
                }, {
                    val errorMsg = ErrorMessage(it.message)
                    errorMsg.setErrorBtn(R.string.error_view_btn_title_retry, { fetchData(dayOfMonth, month, year) })
                    errorMsg.errorImage = LottieJson.UFO_FLOATING
                    errorMessage.value = errorMsg
                }))
    }
}

