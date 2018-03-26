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

package com.standup.app.billing.repo

import android.app.Activity
import android.app.Application
import com.standup.app.billing.BuildConfig
import io.reactivex.Single

/**
 * Created by Kevalpatel2106 on 19-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface BillingRepo {


    /**
     * Check if the premium product is purchased or not? This is an async call that will return true
     * in the success of [Single] if the premium item is purchased.
     */
    fun isPremiumPurchased(application: Application): Single<Boolean>

    /**
     * Purchase the premium version of the app. This method will buy product [BuildConfig.PRO_VERSION_ID]
     * and return the order id.
     */
    fun purchasePremium(activity: Activity): Single<String>

    /**
     * Donate $2. This method will buy product [BuildConfig.DONATE_2]
     * and return the order id.
     */
    fun donate2Dollar(activity: Activity): Single<String>

    /**
     * Donate $5. This method will buy product [BuildConfig.DONATE_5]
     * and return the order id.
     */
    fun donate5Dollar(activity: Activity): Single<String>

    /**
     * Donate $10. This method will buy product [BuildConfig.DONATE_10]
     * and return the order id.
     */
    fun donate10Dollar(activity: Activity): Single<String>

    /**
     * Donate $20. This method will buy product [BuildConfig.DONATE_20]
     * and return the order id.
     */
    fun donate20Dollar(activity: Activity): Single<String>
}
