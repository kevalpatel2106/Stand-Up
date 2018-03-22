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
import android.support.annotation.UiThread
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.standup.app.billing.BillingConstants
import com.standup.app.billing.IAPException
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.Exceptions

/**
 * Created by Kevalpatel2106 on 19-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class BillingRepoImpl : BillingRepo {

    /**
     * Check if the premium product is purchased or not? This is an async call that will return true
     * in the success of [Single] if the premium item is purchased.
     *
     * @see [BillingClient]
     */
    @UiThread
    override fun isPremiumPurchased(application: Application): Single<Boolean> {
        val billingClient: BillingClient = BillingClient
                .newBuilder(application)
                .setListener { _, _ -> /* Do nothing */ }
                .build()

        return Single.create<BillingClient> {
            billingClient.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                    if (billingResponseCode != BillingClient.BillingResponse.OK) {
                        if (!it.isDisposed) it.onError(IAPException(billingResponseCode))
                        return
                    }

                    it.onSuccess(billingClient)
                }

                override fun onBillingServiceDisconnected() {
                    //IAP service connection failed.
                    it.onError(IAPException(BillingClient.BillingResponse.SERVICE_UNAVAILABLE))
                }
            })
        }.map {
            it.queryPurchases(BillingClient.SkuType.INAPP)
        }.map {

            if (it.responseCode != BillingClient.BillingResponse.OK)
                throw Exceptions.propagate(IAPException(it.responseCode))

            val purchases = it.purchasesList
            return@map purchases.find { it.sku == BillingConstants.SKU_PREMIUM } != null
        }.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { if (billingClient.isReady) billingClient.endConnection() }
                .doAfterTerminate { billingClient.endConnection() }
    }

    /**
     * Purchase the premium product. It will return [Single] witch will return false in onNext.
     */
    @UiThread
    override fun purchasePremium(activity: Activity): Single<String> {

        return Single.create<String> {

            //Prepare billing client
            val billingClient: BillingClient = BillingClient
                    .newBuilder(activity)
                    .setListener { responseCode, purchases ->

                        // Have we been disposed of in the meantime?
                        if (responseCode != BillingClient.BillingResponse.OK || purchases == null) {
                            if (!it.isDisposed) it.onError(IAPException(responseCode))
                            return@setListener
                        }

                        val premiumSku = purchases.find { it.sku == BillingConstants.SKU_PREMIUM }

                        if (premiumSku != null) {
                            it.onSuccess(premiumSku.orderId)
                        } else if (!it.isDisposed) {
                            it.onError(IAPException(BillingClient.BillingResponse.ITEM_NOT_OWNED))
                        }
                    }
                    .build()

            //Connect
            billingClient.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                    if (billingResponseCode != BillingClient.BillingResponse.OK) {
                        it.onError(IAPException(billingResponseCode))
                        return
                    }

                    //Initiate the purchase flow
                    val purchaseParams = BillingFlowParams.newBuilder()
                            .setSku(BillingConstants.SKU_PREMIUM)
                            .setType(BillingClient.SkuType.INAPP)
                            .setOldSkus(null)
                            .build()

                    val responseCode = billingClient.launchBillingFlow(activity, purchaseParams)

                    // Have we been disposed of in the meantime?
                    if (responseCode != BillingClient.BillingResponse.OK)
                        it.onError(IAPException(BillingClient.BillingResponse.SERVICE_UNAVAILABLE))
                }

                override fun onBillingServiceDisconnected() {
                    //IAP service connection failed.
                    it.onError(IAPException(BillingClient.BillingResponse.SERVICE_UNAVAILABLE))
                }
            })
        }.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
