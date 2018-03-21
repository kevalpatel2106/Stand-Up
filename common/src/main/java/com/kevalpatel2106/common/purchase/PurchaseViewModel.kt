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

package com.kevalpatel2106.common.purchase

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.R
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.di.DaggerCommonsComponent
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.annotations.OnlyForTesting
import com.standup.app.billing.repo.BillingRepo
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 19-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class PurchaseViewModel : BaseViewModel {

    @Inject
    internal lateinit var application: Application

    @Inject
    internal lateinit var billingRepo: BillingRepo

    @Inject
    internal lateinit var sharedPrefProvider: SharedPrefsProvider

    internal val isPremiumPurchased = MutableLiveData<Boolean>()

    internal val premiumOrderId = MutableLiveData<String>()

    internal val buyPremiumErrorMessage = MutableLiveData<String>()

    @Suppress("unused")
    constructor() {
        DaggerCommonsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@PurchaseViewModel)

        init()
    }

    @OnlyForTesting
    @VisibleForTesting
    constructor(application: Application,
                sharedPrefProvider: SharedPrefsProvider,
                billingRepo: BillingRepo) {
        this.application = application
        this.sharedPrefProvider = sharedPrefProvider
        this.billingRepo = billingRepo

        init()
    }

    /**
     * Set the initial values of [MutableLiveData].
     */
    private fun init() {
        isPremiumPurchased.value = false
        premiumOrderId.value = null
        buyPremiumErrorMessage.value = null

        checkIfToDisplayBuyPro()
    }

    /**
     * Check if to display buy pro version button.
     */
    @VisibleForTesting
    internal fun checkIfToDisplayBuyPro() {
        addDisposable(billingRepo.isPremiumPurchased(application)
                .doOnSubscribe { blockUi.value = true }
                .doAfterTerminate { blockUi.value = false }
                .subscribe({
                    isPremiumPurchased.value = it
                }, {
                    Timber.e(it.stackTrace.toString())
                    isPremiumPurchased.value = false

                    //Error
                    val errorMsg = ErrorMessage(it.message)
                    errorMsg.setErrorBtn(R.string.error_view_btn_title_retry, { checkIfToDisplayBuyPro() })
                    errorMessage.value = errorMsg
                }))
    }

    /**
     * Buy the pro version.
     */
    internal fun buyPro(activity: PurchaseActivity) {
        addDisposable(billingRepo.purchasePremium(activity)
                .doOnSubscribe { blockUi.value = true }
                .doAfterTerminate { blockUi.value = false }
                .subscribe({
                    //Premium item purchased
                    isPremiumPurchased.value = it != null

                    //Order id of the premium product
                    premiumOrderId.value = it
                }, {
                    Timber.e(it.stackTrace.toString())
                    isPremiumPurchased.value = false

                    //Error
                    buyPremiumErrorMessage.value = it.message
                }))
    }
}
