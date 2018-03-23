package com.standup.app.billing.repo

import android.app.Activity
import android.app.Application
import com.android.billingclient.api.BillingClient
import com.standup.app.billing.IAPException
import io.reactivex.Single

/**
 * Created by Kevalpatel2106 on 21-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class MockBillingRepoImpl : BillingRepo {

    var isPremiumPurchased: Boolean = true

    var isPremiumPurchasedError: Boolean = false

    var orderToken: String = ""

    var buyPremiumError: Boolean = false

    override fun isPremiumPurchased(application: Application): Single<Boolean> {
        return Single.create {
            if (isPremiumPurchasedError) {
                it.onError(IAPException(BillingClient.BillingResponse.ERROR))
            } else {
                it.onSuccess(isPremiumPurchased)
            }
        }
    }

    override fun purchasePremium(activity: Activity): Single<String> {
        return Single.create {
            if (buyPremiumError) {
                it.onError(IAPException(BillingClient.BillingResponse.ERROR))
            } else {
                it.onSuccess(orderToken)
            }
        }
    }

}