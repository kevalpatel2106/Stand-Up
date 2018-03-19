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

package com.standup.app.billing


import android.app.Activity
import android.content.ContentValues.TAG

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchasesResult
import com.android.billingclient.api.PurchasesUpdatedListener
import timber.log.Timber
import java.io.Closeable

import java.util.ArrayList

/**
 * Created by Keval on 17/03/18.
 * Handles all the interactions with Play Store (via Billing library), maintains connection to
 * it through BillingClient and caches temporary states/data if needed.
 *
 * @see [Play Billing Sample](https://github.com/googlesamples/android-play-billing/blob/master/TrivialDrive_v2/shared-module/src/main/java/com/example/billingmodule/billing/BillingManager.java)
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
internal class BillingManager(

        /**
         * Instance of the activity with IAP.
         */
        private val activity: Activity,

        /**
         * [BillingUpdatesListener] to get callbacks when billing detail gets updated.
         */
        private val billingUpdatesListener: BillingUpdatesListener
) : PurchasesUpdatedListener, Closeable {

    /**
     * A reference to BillingClient
     */
    private var billingClient: BillingClient = BillingClient
            .newBuilder(activity)
            .setListener(this)
            .build()

    /**
     * List of all the in-app purchased items.
     *
     * @see [Purchase]
     */
    private val purchases = ArrayList<Purchase>()

    /**
     * The standard method to execute the billing requests. Any request for [BillingClient] must be
     * execute using this function. If the [billingClient] is connected to the service, this method
     * will execute the [execute]. If [billingClient] is disconnected, it will retry to establish
     * new connections.
     *
     * @see startServiceConnection
     */
    private fun executeBillingRequest(execute: () -> Unit) {
        if (billingClient.isReady) {
            execute.invoke()
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(execute)
        }
    }

    private fun startServiceConnection(executeOnServiceConnected: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {

            override fun onBillingSetupFinished(@BillingResponse billingResponseCode: Int) {

                Timber.i("Setup finished. Response code: $billingResponseCode")

                if (billingResponseCode == BillingResponse.OK) {

                    //Run the function.
                    executeOnServiceConnected.invoke()
                } else {
                    billingUpdatesListener.onError(BILLING_MANAGER_NOT_INITIALIZED)
                }
            }

            override fun onBillingServiceDisconnected() {
                //IAP service connection failed.
                billingUpdatesListener.onError(BILLING_MANAGER_NOT_INITIALIZED)
            }
        })
    }


    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
     fun queryPurchases() {
        executeBillingRequest({
            val result = billingClient.queryPurchases(SkuType.INAPP)

            // Have we been disposed of in the meantime?
            // If so, or bad result code, then quit.
            if (result.responseCode != BillingResponse.OK) {
                Timber.w("Billing client was null or result code (" + result.responseCode
                        + ") was bad - quitting")
                return@executeBillingRequest
            }

            Timber.i(TAG, "Query inventory was successful.")

            // Update the UI and purchases inventory with new list of purchases
            purchases.clear()

            onPurchasesUpdated(BillingResponse.OK, result.purchasesList)
        })
    }

    /**
     * Handle a callback that purchases were updated from the Billing library
     */
    override fun onPurchasesUpdated(resultCode: Int, purchases: List<Purchase>?) {
        when (resultCode) {
            BillingResponse.OK -> {
                purchases?.let {

                    it.forEach { purchase -> handlePurchase(purchase) }

                    billingUpdatesListener.onPurchasesUpdated(this.purchases)
                }
            }
            BillingResponse.USER_CANCELED -> {
                Timber.i("onPurchasesUpdated() - user cancelled the purchase flow - skipping")
            }
            else -> {
                Timber.w("onPurchasesUpdated() got unknown resultCode: $resultCode")
            }
        }
    }

    /**
     * Start a purchase flow
     */
    fun initiatePurchaseFlow(skuId: String, @SkuType billingType: String) {
        executeBillingRequest({

            val purchaseParams = BillingFlowParams.newBuilder()
                    .setSku(skuId)
                    .setType(billingType)
                    .setOldSkus(null)
                    .build()

            billingClient.launchBillingFlow(activity, purchaseParams)
        })
    }

    /**
     * Disconnect the billing service and clear the resources.
     */
    override fun close() {
        if (billingClient.isReady) billingClient.endConnection()
    }

    /**
     * Handles the purchase
     *
     *
     * @param purchase Purchase to be handled
     */
    private fun handlePurchase(purchase: Purchase) {
        purchases.add(purchase)
    }

    companion object {
        const val BILLING_MANAGER_NOT_INITIALIZED = -1
    }
}
