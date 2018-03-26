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

package com.standup.app.about.donate

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.utils.annotations.OnlyForTesting
import com.kevalpatel2106.utils.annotations.ViewModel
import com.standup.app.about.di.DaggerAboutComponent
import com.standup.app.billing.IAPException
import com.standup.app.billing.repo.BillingRepo
import javax.inject.Inject

/**
 * Created by Keval on 23/12/17.
 * A [com.kevalpatel2106.common.annotations.ViewModel] for [SupportDevelopmentActivity].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see SupportDevelopmentActivity
 */
@ViewModel(SupportDevelopmentActivity::class)
internal class SupportDevelopmentViewModel : BaseViewModel {

    @Inject
    internal lateinit var billingRepo: BillingRepo

    @VisibleForTesting
    @OnlyForTesting
    constructor(billingRepo: BillingRepo) {
        this.billingRepo = billingRepo
    }


    constructor() {
        DaggerAboutComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SupportDevelopmentViewModel)
    }

    internal val donationOrderId = MutableLiveData<String>()

    internal val donationErrorCode = MutableLiveData<Int>()

    internal fun donate2Dollar(activity: Activity) {
        billingRepo.donate2Dollar(activity)
                .doOnSubscribe { blockUi.value = true }
                .doAfterTerminate { blockUi.value = false }
                .subscribe({
                    donationOrderId.value = it
                }, {
                    donationErrorCode.value = (it as IAPException).errorCode
                })
    }

    internal fun donate5Dollar(activity: Activity) {
        billingRepo.donate5Dollar(activity)
                .doOnSubscribe { blockUi.value = true }
                .doAfterTerminate { blockUi.value = false }
                .subscribe({
                    donationOrderId.value = it
                }, {
                    donationErrorCode.value = (it as IAPException).errorCode
                })
    }

    internal fun donate10Dollar(activity: Activity) {
        billingRepo.donate10Dollar(activity)
                .doOnSubscribe { blockUi.value = true }
                .doAfterTerminate { blockUi.value = false }
                .subscribe({
                    donationOrderId.value = it
                }, {
                    donationErrorCode.value = (it as IAPException).errorCode
                })
    }

    internal fun donate20Dollar(activity: Activity) {
        billingRepo.donate20Dollar(activity)
                .doOnSubscribe { blockUi.value = true }
                .doAfterTerminate { blockUi.value = false }
                .subscribe({
                    donationOrderId.value = it
                }, {
                    donationErrorCode.value = (it as IAPException).errorCode
                })
    }
}
