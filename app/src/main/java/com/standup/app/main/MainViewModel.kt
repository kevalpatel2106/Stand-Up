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

package com.standup.app.main

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.utils.annotations.OnlyForTesting
import com.standup.app.billing.repo.BillingRepo
import com.standup.app.dashboard.DashboardApi
import com.standup.app.dashboard.DashboardFragment
import com.standup.app.diary.DiaryModule
import com.standup.app.diary.list.DiaryFragment
import com.standup.app.stats.StatsFragment
import com.standup.app.stats.StatsModule
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 12-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class MainViewModel : BaseViewModel {

    @Inject
    internal lateinit var dairyModule: DiaryModule

    @Inject
    internal lateinit var statsModule: StatsModule

    @Inject
    internal lateinit var dashboardApi: DashboardApi

    @Inject
    internal lateinit var application: Application

    @Inject
    internal lateinit var billingRepo: BillingRepo

    internal val isDisplayBuyPro = MutableLiveData<Boolean>()

    /**
     * [DashboardFragment] instance to display in the [MainActivity].
     */
    internal lateinit var homeFragment: DashboardFragment

    /**
     * [DiaryFragment] instance to display in the [MainActivity].
     */
    internal lateinit var diaryFragment: DiaryFragment

    /**
     * [StatsFragment] instance to display in the [MainActivity].
     */
    internal lateinit var statsFragment: StatsFragment

    @OnlyForTesting
    @VisibleForTesting
    constructor(application: Application,
                billingRepo: BillingRepo,
                diaryModule: DiaryModule,
                statsModule: StatsModule,
                dashboardApi: DashboardApi) {
        this.application = application
        this.billingRepo = billingRepo
        this.dairyModule = diaryModule
        this.statsModule = statsModule
        this.dashboardApi = dashboardApi

        init()
    }


    @Suppress("unused")
    constructor() {
        DaggerMainComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@MainViewModel)
        init()
    }

    fun init() {
        homeFragment = dashboardApi.getDashboard()
        diaryFragment = dairyModule.getDiary()
        statsFragment = statsModule.getStatsFragment()

        isDisplayBuyPro.value = false

        //Check is pro version already purchased?
        checkIfToDisplayBuyPro()
    }

    @VisibleForTesting
    internal fun checkIfToDisplayBuyPro() {
        billingRepo.isPremiumPurchased(application)
                .subscribe({
                    isDisplayBuyPro.value = it
                }, {
                    Timber.e(it.stackTrace.toString())
                    isDisplayBuyPro.value = false
                })
    }
}
